package com.example.demo.service;

import com.example.demo.Repository.Interface.UserRepository;
import com.example.demo.model.User;
import com.example.demo.security.JwtService;
import com.example.demo.service.Interface.AuthenticationServiceInterface;
import com.example.demo.util.AuthenticationResponse;
import com.example.demo.util.LoginUserDto;
import com.example.demo.util.RegisterUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements AuthenticationServiceInterface {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    @Autowired
    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }


    @Override
    public ResponseEntity<AuthenticationResponse> signup(RegisterUserDto registerUserDto) {
        User user = User.builder()
                .email(registerUserDto.getEmail())
                .fullName(registerUserDto.getFullName())
                .password(passwordEncoder.encode(registerUserDto.getPassword()))
                .build();

        User savedUsed = userRepository.save(user);
        String jwtToken = jwtService.generateToken(savedUsed);

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder().
                fullName(savedUsed.getFullName())
                .email(savedUsed.getEmail())
                .accessToken(jwtToken)
                .build();

        return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
    }


    @Override
    public  ResponseEntity<AuthenticationResponse> authenticate(LoginUserDto loginUserDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserDto.getEmail(),
                        loginUserDto.getPassword()
                )
        );

        User user = userRepository.findByEmail(loginUserDto.getEmail())
                .orElseThrow();
        String jwtToken = jwtService.generateToken(user);

        AuthenticationResponse authenticationResponse =  AuthenticationResponse.builder().
                fullName(user.getFullName())
                .email(user.getEmail())
                .accessToken(jwtToken)
                .build();
        return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
    }
}