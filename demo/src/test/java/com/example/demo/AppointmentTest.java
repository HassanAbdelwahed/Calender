package com.example.demo;


import com.example.demo.Repository.Interface.AppointmentRepositoryInterface;
import com.example.demo.Repository.Interface.UserRepository;
import com.example.demo.exceptionHandling.ResourceNotFoundException;
import com.example.demo.model.Appointment;
import com.example.demo.model.Invitation;
import com.example.demo.model.InvitationStatus;
import com.example.demo.model.User;
import com.example.demo.service.AppointmentService;
import com.example.demo.util.*;
import com.example.demo.util.utilInterfaces.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest

public class AppointmentTest {

    @Autowired
    private AppointmentService appointmentService;
    @Mock
    private AppointmentRepositoryInterface appointmentRepository;
    @Mock
    private UserRepository userRepository;
    private User testUser;

    @BeforeEach
    void setUp() {
        this.appointmentService = new AppointmentService(appointmentRepository, userRepository);
        this.testUser = User.builder()
                .fullName("Test User")
                .email("testuser@example.com")
                .password("password")
                .id(2L)
                .build();
    }

    @Test
    void testCreateAppointment_Success() {
        AppointmentRequest request = AppointmentRequest.builder()
                .date(LocalDate.now().plusDays(1))
                .from(LocalTime.of(10, 0))
                .to(LocalTime.of(11, 0))
                .inviteesEmails(List.of("invitee@example.com"))
                .build();

        User user = new User();
        User inviteeUser = new User();
        when(userRepository.findByEmail("invitee@example.com")).thenReturn(Optional.of(inviteeUser));

        List<Appointment> appointments = new ArrayList<>();
        when(appointmentRepository.findUserAppointments(any(), any())).thenReturn(appointments);
        when(appointmentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<Util> mockedStatic = Mockito.mockStatic(Util.class)) {
            mockedStatic.when(Util::getCurrentAuthenticatedUser).thenReturn(user);
            mockedStatic.when(() -> Util.isConflict(any(), any())).thenReturn(false);

            ResponseEntity<Response<?>> response = appointmentService.createAppointment(request);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            ResponseData<?> responseData = (ResponseData<?>) response.getBody();
            assertNotNull(responseData);
            assertTrue(responseData.getData() instanceof Appointment);

            Appointment savedAppointment = (Appointment) responseData.getData();
            assertNotNull(savedAppointment.getInvitations());
            assertEquals(1, savedAppointment.getInvitations().size());

            Invitation invitation = savedAppointment.getInvitations().get(0);
            assertEquals(inviteeUser, invitation.getUser());
            assertEquals(InvitationStatus.AVAILABLE, invitation.getStatus());
        }
    }

    @Test
    void createAppointment_ShouldReturnBadRequest_WhenTimeIsInvalid() {
        AppointmentRequest request = AppointmentRequest.builder()
                .date(LocalDate.now().minusDays(1))
                .from(LocalTime.of(10, 0))
                .to(LocalTime.of(11, 0))
                .build();

        try (MockedStatic<Util> mockedUtil = Mockito.mockStatic(Util.class)) {
            mockedUtil.when(Util::getCurrentAuthenticatedUser).thenReturn(new User());
            ResponseEntity<Response<?>> response = appointmentService.createAppointment(request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Not Valid Time", ((ResponseDataOrError<?>) Objects.requireNonNull(response.getBody())).getMessage());
        }
    }


    @Test
    void createAppointment_ShouldReturnBadRequest_WhenThereIsAConflict() {
        // Arrange
        AppointmentRequest request = AppointmentRequest.builder()
                .date(LocalDate.now().plusDays(1))
                .from(LocalTime.of(10, 0))
                .to(LocalTime.of(11, 0))
                .build();

        Appointment existingAppointment = Appointment.builder()
                .date(LocalDate.now().plusDays(1))
                .from(LocalTime.of(10, 0))
                        .to(LocalTime.of(11, 0))
                                .build();

        try (MockedStatic<Util> mockedUtil = Mockito.mockStatic(Util.class)) {
            mockedUtil.when(Util::getCurrentAuthenticatedUser).thenReturn(this.testUser);
            mockedUtil.when(() -> Util.isConflict(any(), any())).thenReturn(true);

            when(appointmentRepository.findUserAppointments(any(User.class), any(LocalDate.class)))
                    .thenReturn(List.of(existingAppointment));

            // Act
            ResponseEntity<Response<?>> response = appointmentService.createAppointment(request);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("There is a conflict", ((ResponseDataOrError<?>) Objects.requireNonNull(response.getBody())).getMessage());
        }
    }

    @Test
    void cancelAppointment_ShouldThrowResourceNotFoundException_WhenAppointmentDoesNotExist() {
        long appointmentId = 1L;
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());
        try (MockedStatic<Util> mockedUtil = Mockito.mockStatic(Util.class)) {
            mockedUtil.when(Util::getCurrentAuthenticatedUser).thenReturn(this.testUser);
            assertThrows(ResourceNotFoundException.class, () -> appointmentService.cancelAppointment(appointmentId));
        }
    }

    @Test
    void cancelAppointment_ShouldReturnUnauthorized_WhenUserIsNotTheOwner() {
        // Arrange
        long appointmentId = 1L;
        User owner = new User();
        owner.setId(1L);

        Appointment appointment = Appointment.builder()
                .date(LocalDate.now().plusDays(1))
                .from(LocalTime.of(10, 0))
                .to(LocalTime.of(11, 0))
                .appointmentOwner(owner)
                .build();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        try (MockedStatic<Util> mockedUtil = Mockito.mockStatic(Util.class)) {
            mockedUtil.when(Util::getCurrentAuthenticatedUser).thenReturn(this.testUser);

            ResponseEntity<Response<?>> response = appointmentService.cancelAppointment(appointmentId);

            // Assert
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("you are not the owner of this appointment", ((ResponseDataOrError<?>) Objects.requireNonNull(response.getBody())).getMessage());
        }
    }

    @Test
    void cancelAppointment_ShouldCancelAndReturnOk_WhenConditionsAreMet() {
        long appointmentId = 1L;

        Appointment appointment = Appointment.builder()
                .date(LocalDate.now().plusDays(1))
                .from(LocalTime.now().plusHours(1))
                .to(LocalTime.now().plusHours(2))
                .appointmentOwner(this.testUser)
                .build();
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        try (MockedStatic<Util> mockedUtil = Mockito.mockStatic(Util.class)) {
            mockedUtil.when(Util::getCurrentAuthenticatedUser).thenReturn(this.testUser);

            ResponseEntity<Response<?>> response = appointmentService.cancelAppointment(appointmentId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Appointment Canceled successfully", ((ResponseDataOrError<?>) Objects.requireNonNull(response.getBody())).getMessage());
        }
    }

    @Test
    void testCancelAppointment_Success() {
        // Arrange
        long appointmentId = 1L;

        User user = new User();
        user.setId(1L);

        User inviteeUser = new User();
        inviteeUser.setId(2L);

        Appointment appointment = Appointment.builder()
                .date(LocalDate.now().plusDays(1))
                .from(LocalTime.now().plusHours(1))
                .to(LocalTime.now().plusHours(2))
                .appointmentOwner(this.testUser)
                .id(appointmentId)
                .build();


        Invitation invitation = Invitation.builder()
                .user(inviteeUser)
                        .status(InvitationStatus.AVAILABLE)
                                .build();
        appointment.setInvitations(List.of(invitation));

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<Util> mockedStatic = Mockito.mockStatic(Util.class)) {
            mockedStatic.when(Util::getCurrentAuthenticatedUser).thenReturn(this.testUser);

            ResponseEntity<Response<?>> response = appointmentService.cancelAppointment(appointmentId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            Response<?> responseData = response.getBody();
            assertNotNull(responseData);
            assertEquals("Appointment Canceled successfully", ((ResponseDataOrError<?>) Objects.requireNonNull(response.getBody())).getMessage());

            // Verify that the appointment was canceled
            assertTrue(appointment.isCanceled());

            assertEquals(InvitationStatus.CANCELED, appointment.getInvitations().get(0).getStatus());
        }
    }

    @Test
    void testGetAppointments_Success() {
        int page = 0;
        int size = 10;

        User user = new User();
        user.setId(1L);

        Appointment appointment1 = Appointment.builder()
                .id(1L)
                    .appointmentOwner(user)
                                .build();

        Appointment appointment2 = Appointment.builder()
                .id(2L)
                        .appointmentOwner(user)
                                .build();

        List<Appointment> appointmentsList = List.of(appointment1, appointment2);
        Page<Appointment> appointmentsPage = new PageImpl<>(appointmentsList);

        when(appointmentRepository.findAll(any(PageRequest.class))).thenReturn(appointmentsPage);

        ResponseEntity<List<Appointment>> response = appointmentService.getAppointments(page, size);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Appointment> responseAppointments = response.getBody();
        assertNotNull(responseAppointments);
        assertEquals(2, responseAppointments.size());
        assertEquals(appointment1.getId(), responseAppointments.get(0).getId());
        assertEquals(appointment2.getId(), responseAppointments.get(1).getId());
    }

    // test cases for isConflict method
    @Test
    void testIsConflict_noConflict() {
        Appointment existingAppointment = Appointment.builder()
                .date(LocalDate.now().plusDays(1))
                .from(LocalTime.of(9, 0))
                .to(LocalTime.of(10, 0))
                .appointmentOwner(this.testUser)
                .build();

        List<Appointment> appointments = List.of(existingAppointment);

        Appointment newAppointment = Appointment.builder()
                .date(LocalDate.now().plusDays(1))
                .from(LocalTime.of(10, 0))
                .to(LocalTime.of(11, 0))
                .appointmentOwner(this.testUser)
                .build();

        assertFalse(Util.isConflict(appointments, newAppointment));
    }

    @Test
    void testIsConflict_surroundingExistingAppointment() {
        Appointment existingAppointment = Appointment.builder()
                .date(LocalDate.now().plusDays(1))
                .from(LocalTime.of(10, 0))
                .to(LocalTime.of(11, 0))
                .appointmentOwner(this.testUser)
                .build();

        List<Appointment> appointments = List.of(existingAppointment);


        Appointment newAppointment = Appointment.builder()
                .date(LocalDate.now().plusDays(1))
                .from(LocalTime.of(9, 0))
                .to(LocalTime.of(12, 0))
                .appointmentOwner(this.testUser)
                .build();

        assertTrue(Util.isConflict(appointments, newAppointment));
    }

}
