package com.example.demo.util;

import java.time.LocalDate;
import java.time.LocalTime;

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
            LocalTime endTime = LocalTime.now()
                    .plusHours(1 + hours)
                    .plusMinutes(minutes)
                    .plusSeconds(seconds);
            if (endTime.isAfter(LocalTime.MIDNIGHT)) { // new day
                appointDate = appointDate.plusDays(1);
                start = LocalTime.MIN;
                end = start.plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
            } else {
                start =  LocalTime.of(LocalTime.now().getHour() + 1, 0, 0);
                end = endTime;
            }
        } else {
            appointDate = date;
            start = LocalTime.MIN;
            end = start.plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
        }
        return new Object[]{start, end, appointDate};
    }
}
