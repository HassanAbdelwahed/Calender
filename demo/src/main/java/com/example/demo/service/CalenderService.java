package com.example.demo.service;


import com.example.demo.Repository.CalenderRepository;
import com.example.demo.model.Appointment;
import com.example.demo.service.Interface.CalenderServiceInterface;
import com.example.demo.util.ResponseData;
import com.example.demo.util.Util;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalenderService implements CalenderServiceInterface {

    private final CalenderRepository calenderRepository = new CalenderRepository();

    @Override
    public ResponseEntity<ResponseData<?>> addAppointment(Appointment appointment) {
        if (calenderRepository.isConflict(appointment)) {
            ResponseData<?> responseData = new ResponseData<>(null, "There is conflict");
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        }
        calenderRepository.addAppointment(appointment);
        ResponseData<Appointment> responseData = new ResponseData<>(appointment, "Appointment Added successfully");
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseData<?>> getRecommendations(int n, float duration,  LocalDate date) {
        List<Appointment> list = generateRecommendations(n, duration, date);
        ResponseData<List<Appointment>> responseData = new ResponseData<>(list, "Recommendations found");
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    public List<Appointment> generateRecommendations(int n, float duration,  LocalDate date) {
        List<Appointment> list = new ArrayList<>();

        int[] durationParts = Util.calculateDurationParts(duration);
        int hours = durationParts[0];
        int minutes = durationParts[1];
        int seconds = durationParts[2];

        Object[] res = Util.handleStart(date, hours, minutes, seconds);
        LocalTime start = (LocalTime) res[0];
        LocalTime end = (LocalTime) res[1];
        LocalDate appointDate = (LocalDate) res[2];


        while (list.size() < n) {
            Appointment appointment = new Appointment(appointDate, start, end);
            if (!calenderRepository.isConflict(appointment)) {
                list.add(appointment);
            }

            LocalTime newEnd = end.plusHours(1);

            if (end.isBefore(LocalTime.MIDNIGHT) && newEnd.isAfter(LocalTime.MIDNIGHT) && date != null) {
                break;
            } else if ((end.isBefore(LocalTime.MIDNIGHT) || end.equals(LocalTime.MIN) || end.equals(LocalTime.MAX)) && newEnd.isAfter(LocalTime.MIDNIGHT) && date == null) {
                appointDate = appointDate.plusDays(1);
                start = LocalTime.MIN;
                end = start.plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
            } else {
                start = start.plusHours(1);
                end = newEnd;
            }
        }
        return list;
    }


}
