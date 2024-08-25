package com.example.demo.model;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class Appointment {

    @NonNull
    private LocalDate date;

    @NonNull
    private LocalTime from;

    @NonNull
    private LocalTime to;

    public Appointment() {
    }

    public Appointment(@NonNull LocalDate date, LocalTime from, LocalTime to) {
        this.date = date;
        this.from = from;
        this.to = to;
    }
}
