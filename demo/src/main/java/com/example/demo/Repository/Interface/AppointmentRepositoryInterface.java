package com.example.demo.Repository.Interface;

import com.example.demo.model.Appointment;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.demo.model.InvitationStatus;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepositoryInterface extends JpaRepository<Appointment, Long> {
    @Query("SELECT a FROM Appointment a LEFT JOIN Invitation i " +
            "WHERE (a.appointmentOwner = :user OR i.user = :user) " +
            "AND a.date = :date " +
            "AND (i.status IS NULL OR i.status == ACCEPTED)")
    List<Appointment> findUserAppointments(@Param("user") User user, @Param("date") LocalDate date);

    @Query("SELECT a FROM Appointment a LEFT JOIN Invitation i " +
            "WHERE (a.appointmentOwner = :user OR i.user = :user) " +
            "AND a.date >= LocalDate.now() " +
            "AND (i.status IS NULL OR i.status == ACCEPTED)")
    List<Appointment> findUserAppointments(@Param("user") User user);
}
