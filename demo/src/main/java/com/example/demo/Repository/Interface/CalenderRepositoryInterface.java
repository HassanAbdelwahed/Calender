package com.example.demo.Repository.Interface;

import com.example.demo.model.Appointment;

import java.util.List;

public interface CalenderRepositoryInterface {
    void addAppointment(Appointment appointment);

    List<Appointment> getAllAppointments();

    boolean isConflict(Appointment appointment);
}
