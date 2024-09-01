package com.example.demo;

import com.example.demo.Repository.CalenderRepository;
import com.example.demo.model.Appointment;
import com.example.demo.service.CalenderService;
import com.example.demo.util.ResponseData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ApplicationTest {

    @Autowired
    private CalenderService calenderService;

    @BeforeEach
    void setUp() {
    }

    // test cases for Add Appointment

    @Test
    void test_AddAppointment_Success() {
        Appointment appointment = new Appointment(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0));
        ResponseEntity<ResponseData<?>> response = calenderService.addAppointment(appointment);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Appointment Added successfully", response.getBody().getMessage());
    }

    @Test
    void test_AddAppointment_With_Conflict() {
        Appointment existingAppointment = new Appointment(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0));
        calenderService.addAppointment(existingAppointment);

        Appointment newAppointment = new Appointment(LocalDate.now(), LocalTime.of(10, 30), LocalTime.of(11, 30));
        ResponseEntity<ResponseData<?>> response = calenderService.addAppointment(newAppointment);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("There is conflict", response.getBody().getMessage());
    }

    @Test
    void test_GetRecommendations_Success() {
        // Assuming a duration of 2.5 hours, and asking for 3 recommendations
        ResponseEntity<ResponseData<?>> response = calenderService.getRecommendations(3, 2.5f, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Appointment> recommendations = (List<Appointment>) response.getBody().getData();
        assertEquals(3, recommendations.size());

        for (Appointment appointment : recommendations) {
            assertTrue(appointment.getFrom().isBefore(appointment.getTo()));
        }
    }

    @Test
    void test_GetRecommendations_With_Date() {
        LocalDate specificDate = LocalDate.now().plusDays(1);
        ResponseEntity<ResponseData<?>> response = calenderService.getRecommendations(2, 1.5f, specificDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Appointment> recommendations = (List<Appointment>) response.getBody().getData();
        assertEquals(2, recommendations.size());

        for (Appointment appointment : recommendations) {
            assertEquals(specificDate, appointment.getDate());
            assertTrue(appointment.getFrom().isBefore(appointment.getTo()));
        }
    }

    @Test
    void test_GetRecommendations_HandlesCurrentDay_When_Date_is_Null() {
        ResponseEntity<ResponseData<?>> response = calenderService.getRecommendations(1, 1f, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Appointment> recommendations = (List<Appointment>) response.getBody().getData();

        Appointment appointment = recommendations.get(0);
        assertEquals(appointment.getDate(), LocalDate.now());
    }


    @Test
    void test_GetRecommendations_HandlesNextDayTransition_When_Date_is_Null() {
        ResponseEntity<ResponseData<?>> response = calenderService.getRecommendations(1, 23.5f, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Appointment> recommendations = (List<Appointment>) response.getBody().getData();

        assertEquals(1, recommendations.size());
        Appointment appointment = recommendations.get(0);

        assertTrue(appointment.getFrom().isBefore(appointment.getTo()));
        assertTrue(appointment.getTo().isAfter(LocalTime.MIDNIGHT));
    }
}
