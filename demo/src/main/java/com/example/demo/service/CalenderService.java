package com.example.demo.service;


import com.example.demo.Repository.CalenderRepository;
import com.example.demo.Repository.Interface.AppointmentRepositoryInterface;
import com.example.demo.Repository.Interface.UserRepository;
import com.example.demo.model.Appointment;
import com.example.demo.model.Invitation;
import com.example.demo.model.InvitationStatus;
import com.example.demo.model.User;
import com.example.demo.service.Interface.CalenderServiceInterface;
import com.example.demo.util.AppointmentRequest;
import com.example.demo.util.ResponseData;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.demo.util.Util.isConflict;

@Service
public class CalenderService implements CalenderServiceInterface {

    private final CalenderRepository calenderRepository;

    private final AppointmentRepositoryInterface appointmentRepository;

    private final UserRepository userRepository;

    public CalenderService(CalenderRepository calenderRepository, AppointmentRepositoryInterface appointmentRepository, UserRepository userRepository) {
        this.calenderRepository = calenderRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }


    @Override
    public ResponseEntity<ResponseData<?>> addAppointment(Appointment appointment) {
//        if (calenderRepository.isConflict(appointment)) {
//            ResponseData<?> responseData = new ResponseData<>(null, "There is conflict");
//            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
//        }
//        calenderRepository.addAppointment(appointment);
//        ResponseData<Appointment> responseData = new ResponseData<>(appointment, "Appointment Added successfully");
//        return new ResponseEntity<>(responseData, HttpStatus.OK);
        return null;
    }

    @Override
    public ResponseEntity<ResponseData<?>> getRecommendations(int n, float duration,  LocalDate date) {
//        List<Appointment> list = generateRecommendations(n, duration, date);
//        ResponseData<List<Appointment>> responseData = new ResponseData<>(list, "Recommendations found");
//        return new ResponseEntity<>(responseData, HttpStatus.OK);
        return null;
    }

//    public List<Appointment> generateRecommendations(int n, float duration,  LocalDate date) {
//        List<Appointment> list = new ArrayList<>();
//
//        int[] durationParts = Util.calculateDurationParts(duration);
//        int hours = durationParts[0];
//        int minutes = durationParts[1];
//        int seconds = durationParts[2];
//
//        Object[] res = Util.handleStart(date, hours, minutes, seconds);
//        LocalTime start = (LocalTime) res[0];
//        LocalTime end = (LocalTime) res[1];
//        LocalDate appointDate = (LocalDate) res[2];
//
//        while (list.size() < n) {
//            Appointment appointment = new Appointment(appointDate, start, end);
//            if (!calenderRepository.isConflict(appointment)) {
//                list.add(appointment);
//            }
//
//            if (end.getHour() + 1 == 24) {
//                if (end.getMinute() == 0 && end.getSecond() == 0) {
//                    start =  start.plusHours(1);
//                    end = LocalTime.MAX;
//                } else {
//                    if (date == null) {
//                        appointDate = appointDate.plusDays(1);
//                        start = LocalTime.MIN;
//                        end = start.plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
//                    } else {
//                        break;
//                    }
//                }
//            } else {
//                start = start.plusHours(1);
//                end = end.plusHours(1);
//            }
//        }
//        return list;
//    }

}
