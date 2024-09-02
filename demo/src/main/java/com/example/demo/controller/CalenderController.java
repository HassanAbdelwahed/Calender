//package com.example.demo.controller;
//
//
//import com.example.demo.model.Appointment;
//import com.example.demo.service.CalenderService;
//import com.example.demo.util.ResponseData;
//import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//
//@RestController
//@RequestMapping("api/")
//public class CalenderController {
//    @Autowired
//    private CalenderService calenderService;
//
//    @PostMapping
//    public ResponseEntity<ResponseData<?>> addAppointment(@RequestBody Appointment appointment) {
//        return calenderService.addAppointment(appointment);
//    }
//
//    @GetMapping("recommendations")
//    public ResponseEntity<ResponseData<?>> getRecommendations(@RequestParam(name = "n", defaultValue = "3") int n,
//                                                              @RequestParam(name = "duration") float duration,
//                                                              @RequestParam(name = "date", required = false) LocalDate date) {
//        return calenderService.getRecommendations(n, duration, date);
//    }
//
//}
