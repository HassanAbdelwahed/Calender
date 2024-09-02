package com.example.demo;

import com.example.demo.Repository.Interface.AppointmentRepositoryInterface;
import com.example.demo.Repository.Interface.InvitationRepositoryInterface;
import com.example.demo.exceptionHandling.ResourceNotFoundException;
import com.example.demo.model.Appointment;
import com.example.demo.model.Invitation;
import com.example.demo.model.InvitationStatus;
import com.example.demo.model.User;
import com.example.demo.service.Interface.InvitationServiceInterface;
import com.example.demo.util.utilInterfaces.Response;
import com.example.demo.util.ResponseData;
import com.example.demo.util.ResponseDataOrError;
import com.example.demo.util.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class InvitationTest {

    @Mock
    private InvitationRepositoryInterface invitationRepository;
    @Mock
    private AppointmentRepositoryInterface appointmentRepository;
    @Autowired
    private InvitationServiceInterface invitationService;
    private User testUser;
    private Invitation invitation;

    private Appointment appointment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = User.builder()
                .fullName("Test User")
                .email("testuser@example.com")
                .password("password")
                .id(1L)
                .build();

        appointment = Appointment.builder()
                .date(LocalDate.now().plusDays(1))
                .from(LocalTime.of(10, 0))
                .to(LocalTime.of(11, 0))
                .build();

        invitation = Invitation.builder()
                .id(1L)
                .user(testUser)
                .status(InvitationStatus.AVAILABLE)
                .appointment(appointment)
                .build();
    }

    @Test
    void testAcceptInvitation_Success() {
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        when(invitationRepository.save(any(Invitation.class))).thenReturn(invitation);

        try (MockedStatic<Util> utilities = mockStatic(Util.class)) {
            utilities.when(Util::getCurrentAuthenticatedUser).thenReturn(testUser);

            ResponseEntity<Response<?>> response = invitationService.acceptInvitation(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());

            Response<?> responseBody = response.getBody();
            assertTrue(responseBody instanceof ResponseData);
            ResponseData<?> responseData = (ResponseData<?>) response.getBody();
            assertNotNull(responseData);
            assertTrue(responseData.getData() instanceof Invitation);

            assertEquals("Invitation accepted successfully", responseData.getMessage());
            assertEquals(InvitationStatus.ACCEPTED, ((Invitation) responseData.getData()).getStatus());
        }
    }

    @Test
    void testAcceptInvitation_UnAvailable() {
        Invitation invitation = Invitation.builder()
                .id(1L)
                .user(testUser)
                .status(InvitationStatus.CONFLICTED)
                .appointment(this.appointment)
                .build();
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        when(invitationRepository.save(any(Invitation.class))).thenReturn(invitation);

        try (MockedStatic<Util> utilities = mockStatic(Util.class)) {
            utilities.when(Util::getCurrentAuthenticatedUser).thenReturn(testUser);

            ResponseEntity<Response<?>> response = invitationService.acceptInvitation(1L);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            Response<?> responseBody = response.getBody();
            assertTrue(responseBody instanceof ResponseDataOrError<?>);
            assertEquals("Invitation can not be accepted as invitation is CONFLICTED", ((ResponseDataOrError<?>) responseBody).getMessage());
        }
    }

    @Test
    void testAcceptInvitation_NotOwner() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        invitation.setUser(anotherUser);

        when(invitationRepository.findById(1L)).thenReturn(Optional.of(invitation));

        try (MockedStatic<Util> utilities = mockStatic(Util.class)) {
            utilities.when(Util::getCurrentAuthenticatedUser).thenReturn(testUser);

            ResponseEntity<Response<?>> response = invitationService.acceptInvitation(1L);

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            Response<?> responseBody = response.getBody();
            assertTrue(responseBody instanceof ResponseDataOrError);
            assertEquals("you are not the owner of this invitation", ((ResponseDataOrError<?>) responseBody).getMessage());
        }
    }

    @Test
    void acceptInvitation_InvalidTime_ShouldSetTimeOver() {
        try (var mockedUtil = mockStatic(Util.class)) {
            mockedUtil.when(Util::getCurrentAuthenticatedUser).thenReturn(testUser);
            mockedUtil.when(() -> Util.isValidTime(any(Appointment.class))).thenReturn(false);

            when(invitationRepository.findById(invitation.getId())).thenReturn(Optional.of(invitation));

            ResponseEntity<Response<?>> response = invitationService.acceptInvitation(invitation.getId());

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Invitation can not be accepted as it's time is over", ((ResponseDataOrError<?>) Objects.requireNonNull(response.getBody())).getMessage());
            assertEquals(InvitationStatus.TIMEOVER, invitation.getStatus());

            verify(invitationRepository, times(1)).save(invitation);
        }
    }

    @Test
    void acceptInvitation_AlreadyAccepted_ShouldReturnBadRequest() {
        invitation.setStatus(InvitationStatus.ACCEPTED);

        try (var mockedUtil = mockStatic(Util.class)) {
            mockedUtil.when(Util::getCurrentAuthenticatedUser).thenReturn(testUser);

            when(invitationRepository.findById(invitation.getId())).thenReturn(Optional.of(invitation));

            ResponseEntity<Response<?>> response = invitationService.acceptInvitation(invitation.getId());

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Invitation can not be accepted as invitation is accepted", ((ResponseDataOrError<?>) response.getBody()).getMessage());

            verify(invitationRepository, times(0)).save(invitation);
        }
    }

    @Test
    void acceptInvitation_InvitationNotFound_ShouldThrowResourceNotFoundException() {
        try (var mockedUtil = mockStatic(Util.class)) {
            mockedUtil.when(Util::getCurrentAuthenticatedUser).thenReturn(testUser);

            when(invitationRepository.findById(invitation.getId())).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> invitationService.acceptInvitation(invitation.getId()));

            verify(invitationRepository, times(0)).save(invitation);
        }
    }

    @Test
    void testRejectInvitation_Success() {
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        when(invitationRepository.save(any(Invitation.class))).thenReturn(invitation);

        try (MockedStatic<Util> utilities = mockStatic(Util.class)) {
            utilities.when(Util::getCurrentAuthenticatedUser).thenReturn(testUser);

            ResponseEntity<Response<?>> response = invitationService.rejectInvitation(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            Response<?> responseBody = response.getBody();
            assertTrue(responseBody instanceof ResponseData);
            assertEquals("Invitation rejected successfully", ((ResponseData<?>) responseBody).getMessage());
            assertEquals(InvitationStatus.REJECTED, ((Invitation) ((ResponseData<?>) responseBody).getData()).getStatus());
        }
    }

    @Test
    void testRejectInvitation_UnAvailable() {
        Invitation invitation = Invitation.builder()
                .id(1L)
                .user(testUser)
                .status(InvitationStatus.CONFLICTED)
                .appointment(this.appointment)
                .build();
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        when(invitationRepository.save(any(Invitation.class))).thenReturn(invitation);

        try (MockedStatic<Util> utilities = mockStatic(Util.class)) {
            utilities.when(Util::getCurrentAuthenticatedUser).thenReturn(testUser);

            ResponseEntity<Response<?>> response = invitationService.rejectInvitation(1L);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            Response<?> responseBody = response.getBody();
            assertTrue(responseBody instanceof ResponseDataOrError<?>);
            assertEquals("Invitation can not be rejected as invitation is CONFLICTED", ((ResponseDataOrError<?>) responseBody).getMessage());
        }
    }

    @Test
    void testOpenInvitation_Success() {
        invitation.setSeen(false);
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        when(invitationRepository.save(any(Invitation.class))).thenReturn(invitation);

        try (MockedStatic<Util> utilities = mockStatic(Util.class)) {
            utilities.when(Util::getCurrentAuthenticatedUser).thenReturn(testUser);

            ResponseEntity<Response<?>> response = invitationService.openInvitation(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            Response<?> responseBody = response.getBody();
            assertTrue(responseBody instanceof ResponseData);
            assertEquals("Invitation seen", ((ResponseData<?>) responseBody).getMessage());
            assertTrue(((Invitation) ((ResponseData<?>) responseBody).getData()).isSeen());
        }
    }

    @Test
    void testRefuseAttendance_Success() {
        invitation.setStatus(InvitationStatus.ACCEPTED);
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        when(invitationRepository.save(any(Invitation.class))).thenReturn(invitation);

        try (MockedStatic<Util> utilities = mockStatic(Util.class)) {
            utilities.when(Util::getCurrentAuthenticatedUser).thenReturn(testUser);

            ResponseEntity<Response<?>> response = invitationService.refuseAttendance(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            Response<?> responseBody = response.getBody();
            assertTrue(responseBody instanceof ResponseData);
            assertEquals("Attendance Refused Successfully", ((ResponseData<?>) responseBody).getMessage());
            assertEquals(InvitationStatus.REFUSED,  ((Invitation) ((ResponseData<?>) responseBody).getData()).getStatus());
        }
    }

    @Test
    void testGetUserInvitations_Success() {
        when(invitationRepository.findAllInvitations(testUser)).thenReturn(Arrays.asList(invitation));
        when(appointmentRepository.findUpcomingAppointments(testUser, LocalDate.now())).thenReturn(Arrays.asList(appointment));

        try (MockedStatic<Util> utilities = mockStatic(Util.class)) {
            utilities.when(Util::getCurrentAuthenticatedUser).thenReturn(testUser);
            utilities.when(() -> Util.isConflict(anyList(), any(Appointment.class))).thenReturn(false);

            ResponseEntity<List<Invitation>> response = invitationService.getUserInvitations();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            List<Invitation> responseBody = response.getBody();
            assertNotNull(responseBody);
            assertEquals(1, responseBody.size());
            assertEquals(invitation, responseBody.get(0));
        }
    }

}
