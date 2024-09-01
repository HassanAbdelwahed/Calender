package com.example.demo.service.Interface;

import com.example.demo.model.Appointment;
import com.example.demo.util.AppointmentRequest;
import com.example.demo.util.ResponseData;
import com.example.demo.util.ResponseDataOrError;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AppointmentServiceInterface {
    ResponseEntity<ResponseDataOrError<?>> createAppointment(AppointmentRequest appointmentRequest);
    public ResponseEntity<ResponseDataOrError<?>> cancelAppointment(long id);
    public ResponseEntity<List<Appointment>> getAppointments(int page, int size);
}
