package com.example.demo.util;

import com.example.demo.model.Appointment;
import com.example.demo.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Util {
    public static int[] calculateDurationParts(float duration) {
        int hours = (int) duration;
        double fractionalPart = duration - hours;
        int minutes = (int) (fractionalPart * 60);
        double remainingFractionalPart = (fractionalPart * 60) - minutes;
        int seconds = (int) (remainingFractionalPart * 60);
        return new int[]{hours, minutes, seconds};
    }

    public static Object[] handleStart(LocalDate date, int hours, int minutes, int seconds) {
        LocalDate appointDate;
        LocalTime start, end;
        if (date == null) {
            appointDate = LocalDate.now();

            if (LocalTime.now().getHour() + 1 + hours > 24) { // new day
                appointDate = appointDate.plusDays(1);
                start = LocalTime.MIN;
                end = start.plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
            } else if (LocalTime.now().getHour() + 1 + hours == 24){
                if (minutes == 0 && seconds == 0) {
                    start =  LocalTime.of(LocalTime.now().getHour() + 1, 0, 0);
                    end = LocalTime.MAX;
                } else {
                    appointDate = appointDate.plusDays(1);
                    start = LocalTime.MIN;
                    end = start.plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
                }
            } else {
                start =  LocalTime.of(LocalTime.now().getHour() + 1, 0, 0);
                end = start.plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
            }
        } else {
            appointDate = date;
            start = LocalTime.MIN;
            end = start.plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
        }
        return new Object[]{start, end, appointDate};
    }

    public static boolean isConflict(List<Appointment> appointments, Appointment appointment) {
        for (Appointment appoint: appointments) {
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

    public static User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
