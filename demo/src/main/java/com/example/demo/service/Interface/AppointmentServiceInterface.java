package com.example.demo.service.Interface;

import com.example.demo.model.Appointment;
import com.example.demo.util.AppointmentRequest;
import com.example.demo.util.utilInterfaces.Response;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AppointmentServiceInterface {
    ResponseEntity<Response<?>> createAppointment(AppointmentRequest appointmentRequest);
    public ResponseEntity<Response<?>> cancelAppointment(long id);
    public ResponseEntity<List<Appointment>> getAppointments(int page, int size);
}
