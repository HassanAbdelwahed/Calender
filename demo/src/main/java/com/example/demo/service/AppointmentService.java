package com.example.demo.service;

import com.example.demo.Repository.Interface.AppointmentRepositoryInterface;
import com.example.demo.Repository.Interface.UserRepository;
import com.example.demo.exceptionHandling.BadRequestException;
import com.example.demo.exceptionHandling.ResourceNotFoundException;
import com.example.demo.model.Appointment;
import com.example.demo.model.Invitation;
import com.example.demo.model.InvitationStatus;
import com.example.demo.model.User;
import com.example.demo.service.Interface.AppointmentServiceInterface;
import com.example.demo.util.*;
import com.example.demo.util.utilInterfaces.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.demo.util.Util.*;


@Service
public class AppointmentService implements AppointmentServiceInterface {
    private final AppointmentRepositoryInterface appointmentRepository;
    private final UserRepository userRepository;

    @Autowired
    public AppointmentService(AppointmentRepositoryInterface appointmentRepository, UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<Response<?>> createAppointment(AppointmentRequest appointmentRequest) {

        User user = getCurrentAuthenticatedUser();
        Appointment appointment = Appointment.builder()
                .date(appointmentRequest.getDate())
                .from(appointmentRequest.getFrom())
                .to(appointmentRequest.getTo())
                .appointmentOwner(user)
                .build();

        // 1) check time validation
        if (!isValidTime(appointment)) {
            Response<?> responseData = new ResponseDataOrError<>("Not Valid Time");
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        }

        // 2) check conflict
        List<Appointment> appointments = appointmentRepository.findUserAppointments(user, appointmentRequest.getDate());
        System.out.println(Arrays.toString(appointments.toArray()));
        if (isConflict(appointments, appointment)) {
            Response<?> responseData = new ResponseDataOrError<>("There is a conflict");
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        }

        // 3) Check invitees
        List<Invitation> invitations = checkInvitees(appointmentRequest, appointment);
        appointment.setInvitations(invitations);

        // 4) save Appointment
        Appointment savedAppointment = appointmentRepository.save(appointment);
        Response<Appointment> responseData = new ResponseData<>(savedAppointment, "Appointment created successfully");
        return new ResponseEntity<>(responseData, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Response<?>> cancelAppointment(long id) {
        User user = getCurrentAuthenticatedUser();
        Optional<Appointment> appointment = appointmentRepository.findById(id);
        if (appointment.isEmpty())
            throw new ResourceNotFoundException(String.format("There is no appointment with id = %s", id));
        Appointment appoint = appointment.get();

        if (user.getId() != appoint.getAppointmentOwner().getId()) {
            ResponseDataOrError<?> responseData = new ResponseDataOrError<>("you are not the owner of this appointment");
            return new ResponseEntity<>(responseData, HttpStatus.UNAUTHORIZED);
        }


        if (!isValidTime(appoint)) {
            ResponseDataOrError<?> responseData = new ResponseDataOrError<>("you can not cancel appointment after time is over");
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        }

        cancelInvitations(appoint);
        appointment.get().setCanceled(true);
        appointmentRepository.save(appoint);

        ResponseDataOrError<?> responseData = new ResponseDataOrError<>("Appointment Canceled successfully");
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Appointment>> getAppointments(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Appointment> appointments = appointmentRepository.findAll(pageRequest);
        return new ResponseEntity<>(appointments.getContent(), HttpStatus.OK);
    }

    public List<Invitation> checkInvitees(AppointmentRequest appointmentRequest, Appointment appointment) {
        List<Invitation> invitations = new ArrayList<>();
        if (appointmentRequest.getInviteesEmails() == null)
            return invitations;
        for (String email: appointmentRequest.getInviteesEmails()) {
            Optional<User> inviteeUser = userRepository.findByEmail(email);
            if (inviteeUser.isEmpty()) {
                throw new BadRequestException(String.format("There is no user with email = %s", email));
            }

            List<Appointment> inviteeAppointments = appointmentRepository.findUserAppointments(inviteeUser.get(), appointment.getDate());
            Invitation invitation = Invitation.builder()
                    .appointment(appointment)
                    .user(inviteeUser.get())
                    .build();

            if (isConflict(inviteeAppointments, appointment)) {
                invitation.setStatus(InvitationStatus.CONFLICTED);
            } else {
                invitation.setStatus(InvitationStatus.AVAILABLE);
            }

            invitations.add(invitation);
        }
        return invitations;
    }

    public void cancelInvitations(Appointment appointment) {
        if (appointment.getInvitations() == null)
            return;
        for (Invitation invitation: appointment.getInvitations()) {
            invitation.setStatus(InvitationStatus.CANCELED);
        }
    }

}
