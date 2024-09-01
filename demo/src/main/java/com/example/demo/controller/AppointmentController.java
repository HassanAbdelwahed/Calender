package com.example.demo.controller;


import com.example.demo.model.Appointment;
import com.example.demo.service.AppointmentService;
import com.example.demo.util.AppointmentRequest;
import com.example.demo.util.ResponseDataOrError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<ResponseDataOrError<?>> createAppointment(@RequestBody AppointmentRequest appointmentRequest) {
        return appointmentService.createAppointment(appointmentRequest);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDataOrError<?>> cancelAppointment(@PathVariable("id") long id) {
        return appointmentService.cancelAppointment(id);
    }

    @GetMapping
    public ResponseEntity<List<Appointment>> getAppointments(@RequestParam(name = "page", defaultValue = "0") int page,
                                                             @RequestParam(name = "size", defaultValue = "10") int size) {
        return appointmentService.getAppointments(page, size);
    }

}
