package com.example.demo.service;


import com.example.demo.model.Appointment;
import com.example.demo.util.ResponseData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalenderService {

    List<Appointment> appointments = new ArrayList<>();

    public ResponseEntity<ResponseData<?>> addAppointment(Appointment appointment) {
        if (isConflict(appointment)) {
            ResponseData<?> responseData = new ResponseData<>(null, "There is conflict");
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        }
        appointments.add(appointment);
        ResponseData<Appointment> responseData = new ResponseData<>(appointment, "Appointment Added successfully");
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    public ResponseEntity<ResponseData<?>> getRecommendations(int n, float duration,  LocalDate date) {
        List<Appointment> list = new ArrayList<>();
        LocalTime start, end;
        LocalDate appointDate;
        int period = (int) Math.ceil(duration);

        if (date == null) {
            appointDate = LocalDate.now();
            if (LocalTime.now().getHour() + 1 + period > 24) { // end time tommorrow
                appointDate = appointDate.plusDays(1);
                start = LocalTime.MIN;
                end = start.plusHours(period);
            } else if (LocalTime.now().getHour() + 1 + period == 24) {
                start =  LocalTime.of(LocalTime.now().getHour() + 1, 0, 0);
                end = LocalTime.MAX;
            } else {
                start =  LocalTime.of(LocalTime.now().getHour() + 1, 0, 0);
                end = start.plusHours(period);
            }
        } else {
            appointDate = date;
            start = LocalTime.MIN;
            end = start.plusHours(period);
        }

        while (list.size() < n) {
            Appointment appointment = new Appointment(appointDate, start, end);
            if (!isConflict(appointment)) {
                list.add(appointment);
            }
            if (end == LocalTime.MAX) {
                if (date == null) {
                    appointDate = appointDate.plusDays(1);
                    start = LocalTime.MIN;
                    end = start.plusHours(period);
                } else {
                    break;
                }
            } else {
                start = start.plusHours(1);
                if (end.getHour() + 1 == 24) {
                    end = LocalTime.MAX;
                } else {
                    end = end.plusHours(1);
                }
            }
        }

        if (list.size() < n) {
            ResponseData<?> responseData = new ResponseData<>(null, String.format("No available times for %s recommendations", n));
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        }
        ResponseData<List<Appointment>> responseData = new ResponseData<>(list, "Recommendations found");
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    public boolean isConflict(Appointment appointment) {
        for (Appointment appoint: appointments) {
            if (!appoint.getDate().equals(appointment.getDate()))
                continue;
            if (appoint.getFrom().equals(appointment.getFrom()) || appoint.getTo().equals(appointment.getTo())) {
                return true;
            } else if (appoint.getFrom().isBefore(appointment.getFrom()) && appoint.getTo().isAfter(appointment.getFrom())) {
                return true;
            } else if (appoint.getFrom().isAfter(appointment.getFrom()) && appoint.getTo().isBefore(appointment.getTo())) {
                return true;
            } else if (appoint.getFrom().isBefore(appointment.getTo()) && appoint.getTo().isAfter(appointment.getTo())) {
                return true;
            }
        }
        return false;
    }
}
