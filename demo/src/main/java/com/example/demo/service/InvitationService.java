package com.example.demo.service;

import com.example.demo.Repository.Interface.AppointmentRepositoryInterface;
import com.example.demo.Repository.Interface.InvitationRepositoryInterface;
import com.example.demo.model.Appointment;
import com.example.demo.model.Invitation;
import com.example.demo.model.InvitationStatus;
import com.example.demo.model.User;
import com.example.demo.service.Interface.InvitationServiceInterface;
import com.example.demo.exceptionHandling.ResourceNotFoundException;
import com.example.demo.util.ResponseData;
import com.example.demo.util.ResponseDataOrError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.example.demo.util.Util.getCurrentAuthenticatedUser;
import static com.example.demo.util.Util.isConflict;

@Service
public class InvitationService implements InvitationServiceInterface {

    private final InvitationRepositoryInterface invitationRepository;
    private final AppointmentRepositoryInterface appointmentRepository;


    public InvitationService(InvitationRepositoryInterface invitationRepository, AppointmentRepositoryInterface appointmentRepository) {
        this.invitationRepository = invitationRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public ResponseEntity<ResponseDataOrError<?>> acceptInvitation(long id) {
        User user = getCurrentAuthenticatedUser();
        Optional<Invitation> invitation = invitationRepository.findById(id);
        if (invitation.isEmpty()) {
            throw new ResourceNotFoundException(String.format("There is no invitation with id = %s", id));
        }

        Invitation inv = invitation.get();
        if (inv.getUser().getId() != user.getId()) {
            ResponseDataOrError<?> responseData = new ResponseDataOrError<>("you are not the owner of this invitation");
            return new ResponseEntity<>(responseData, HttpStatus.UNAUTHORIZED);
        }

        InvitationStatus invitationStatus = inv.getStatus();

        if (invitationStatus == InvitationStatus.AVAILABLE && !isValidTime(inv.getAppointment())) {
            inv.setStatus(InvitationStatus.TIMEOVER);
            invitationRepository.save(inv);
            ResponseDataOrError<?> responseDataOrError = new ResponseDataOrError<>("Invitation can not be accepted as it's time is over");
            return new ResponseEntity<>(responseDataOrError, HttpStatus.BAD_REQUEST);
        } else if (invitationStatus != InvitationStatus.AVAILABLE) {
            ResponseDataOrError<?> responseDataOrError = new ResponseDataOrError<>(String.format("Invitation can not be accepted as invitation is %s",
                                                                                    invitationStatus.toString().toLowerCase()));
            return new ResponseEntity<>(responseDataOrError, HttpStatus.BAD_REQUEST);
        }

        inv.setStatus(InvitationStatus.ACCEPTED);
        Invitation updatedInvitation = invitationRepository.save(inv);

        ResponseDataOrError<?> responseDataOrError = new ResponseData<>(updatedInvitation, "Invitation accepted successfully");
        return new ResponseEntity<>(responseDataOrError, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseDataOrError<?>> rejectInvitation(long id) {
        User user = getCurrentAuthenticatedUser();
        Optional<Invitation> invitation = invitationRepository.findById(id);
        if (invitation.isEmpty()) {
            throw new ResourceNotFoundException(String.format("There is no invitation with id = %s", id));
        }
        Invitation inv = invitation.get();
        if (inv.getUser().getId() != user.getId()) {
            ResponseDataOrError<?> responseData = new ResponseDataOrError<>("you are not the owner of this invitation");
            return new ResponseEntity<>(responseData, HttpStatus.UNAUTHORIZED);
        }

        InvitationStatus invitationStatus = inv.getStatus();

        if (invitationStatus == InvitationStatus.AVAILABLE && !isValidTime(inv.getAppointment())) {
            inv.setStatus(InvitationStatus.TIMEOVER);
            invitationRepository.save(inv);
            ResponseDataOrError<?> responseDataOrError = new ResponseDataOrError<>("Invitation can not be rejected as it's time is over");
            return new ResponseEntity<>(responseDataOrError, HttpStatus.BAD_REQUEST);
        } else if (invitationStatus != InvitationStatus.AVAILABLE) {
            ResponseDataOrError<?> responseDataOrError = new ResponseDataOrError<>(String.format("Invitation can not be rejected as invitation is %s", invitationStatus.toString().toLowerCase()));
            return new ResponseEntity<>(responseDataOrError, HttpStatus.BAD_REQUEST);
        }

        inv.setStatus(InvitationStatus.REJECTED);
        Invitation updatedInvitation = invitationRepository.save(inv);

        ResponseDataOrError<?> responseDataOrError = new ResponseData<>(updatedInvitation, "Invitation rejected successfully");
        return new ResponseEntity<>(responseDataOrError, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseDataOrError<?>> openInvitation(long id) {
        User user = getCurrentAuthenticatedUser();
        Optional<Invitation> invitation = invitationRepository.findById(id);
        if (invitation.isEmpty()) {
            throw new ResourceNotFoundException(String.format("There is no invitation with id = %s", id));
        }
        Invitation inv = invitation.get();

        if (inv.getUser().getId() != user.getId()) {
            ResponseDataOrError<?> responseData = new ResponseDataOrError<>("you are not the owner of this invitation");
            return new ResponseEntity<>(responseData, HttpStatus.UNAUTHORIZED);
        }

        inv.setSeen(true);
        Invitation updatedInvitation = invitationRepository.save(inv);

        ResponseDataOrError<?> responseDataOrError = new ResponseData<>(updatedInvitation, "Invitation seen");
        return new ResponseEntity<>(responseDataOrError, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseDataOrError<?>> refuseAttendance(long id) {
        User user = getCurrentAuthenticatedUser();
        Optional<Invitation> invitation = invitationRepository.findById(id);
        if (invitation.isEmpty()) {
            throw new ResourceNotFoundException(String.format("There is no invitation with id = %s", id));
        }

        Invitation inv = invitation.get();

        if (inv.getUser().getId() != user.getId()) {
            ResponseDataOrError<?> responseData = new ResponseDataOrError<>("you are not the owner of this invitation");
            return new ResponseEntity<>(responseData, HttpStatus.UNAUTHORIZED);
        }

        if (inv.getStatus() == InvitationStatus.ACCEPTED && !isValidTime(inv.getAppointment())) {
            ResponseDataOrError<?> responseDataOrError = new ResponseDataOrError<>("You should not refuse to attend after meeting start");
            return new ResponseEntity<>(responseDataOrError, HttpStatus.BAD_REQUEST);
        }

        if (inv.getStatus() != InvitationStatus.ACCEPTED) {
            ResponseDataOrError<?> responseDataOrError = new ResponseDataOrError<>("You should not refuse to attend before accept invitation");
            return new ResponseEntity<>(responseDataOrError, HttpStatus.BAD_REQUEST);
        }

        inv.setStatus(InvitationStatus.REFUSED);
        Invitation savedInvitation = invitationRepository.save(inv);

        ResponseDataOrError<?> responseDataOrError = new ResponseData<>(savedInvitation, "Attendance Refused Successfully");
        return new ResponseEntity<>(responseDataOrError, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Invitation>> getUserInvitations() {
        User user = getCurrentAuthenticatedUser();
        List<Invitation> invitations = invitationRepository.findUserInvitations(user);

        // get user Appointment
        List<Appointment> appointments = appointmentRepository.findUserAppointments(user);

        checkInvitationsValidity(invitations, appointments);
        return new ResponseEntity<>(invitations, HttpStatus.OK);
    }

    public void checkInvitationsValidity(List<Invitation> invitations, List<Appointment> appointments ) {
        for (Invitation invitation: invitations) {
            if (invitation.getStatus() == InvitationStatus.AVAILABLE) {
                if (!isValidTime(invitation.getAppointment())) {
                    invitation.setStatus(InvitationStatus.TIMEOVER);
                    invitationRepository.save(invitation);
                } else if (isConflict(appointments, invitation.getAppointment())) {
                    invitation.setStatus(InvitationStatus.CONFLICTED);
                    invitationRepository.save(invitation);
                }
            }
        }
    }

    public boolean isValidTime(Appointment appointment) {
        if (appointment.getDate().isBefore(LocalDate.now()))
            return false;
        if (appointment.getDate().isEqual(LocalDate.now()) && appointment.getFrom().isBefore(LocalTime.now())) {
            return false;
        }
        return true;
    }
}
