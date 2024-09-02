package com.example.demo;


import com.example.demo.Repository.Interface.UserRepository;
import com.example.demo.exceptionHandling.ResourceNotFoundException;
import com.example.demo.model.User;
import com.example.demo.security.JwtService;
import com.example.demo.service.Interface.AuthenticationServiceInterface;
import com.example.demo.util.AuthenticationResponse;
import com.example.demo.util.LoginUserDto;
import com.example.demo.util.RegisterUserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Autowired
    private AuthenticationServiceInterface authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignup() {
        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setEmail("test@example.com");
        registerUserDto.setFullName("Test User");
        registerUserDto.setPassword("password");

        User user = User.builder()
                .email(registerUserDto.getEmail())
                .fullName(registerUserDto.getFullName())
                .password("encodedPassword")
                .build();

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("mockJwtToken");

        ResponseEntity<AuthenticationResponse> response = authenticationService.signup(registerUserDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test User", response.getBody().getFullName());
        assertEquals("test@example.com", response.getBody().getEmail());
        assertEquals("mockJwtToken", response.getBody().getAccessToken());

        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, times(1)).generateToken(any(User.class));
    }

    @Test
    void testAuthenticate() {
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("test@example.com");
        loginUserDto.setPassword("password");

        User user = User.builder()
                .email("test@example.com")
                .fullName("Test User")
                .password("encodedPassword")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("mockJwtToken");

        ResponseEntity<AuthenticationResponse> response = authenticationService.authenticate(loginUserDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test User", response.getBody().getFullName());
        assertEquals("test@example.com", response.getBody().getEmail());
        assertEquals("mockJwtToken", response.getBody().getAccessToken());

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(jwtService, times(1)).generateToken(any(User.class));
    }
}
