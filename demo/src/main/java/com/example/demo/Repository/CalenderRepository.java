package com.example.demo.Repository;

import com.example.demo.Repository.Interface.CalenderRepositoryInterface;
import com.example.demo.model.Appointment;

import java.util.ArrayList;
import java.util.List;

public class CalenderRepository implements CalenderRepositoryInterface {
    private final List<Appointment> appointments = new ArrayList<>();

    public void addAppointment(Appointment appointment) {
        this.appointments.add(appointment);
    }

    public List<Appointment> getAllAppointments() {
        return this.appointments;
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
