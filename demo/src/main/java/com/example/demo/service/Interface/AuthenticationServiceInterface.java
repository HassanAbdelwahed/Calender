package com.example.demo.service.Interface;

import com.example.demo.util.AuthenticationResponse;
import com.example.demo.util.LoginUserDto;
import com.example.demo.util.RegisterUserDto;
import org.springframework.http.ResponseEntity;

public interface AuthenticationServiceInterface {
    ResponseEntity<AuthenticationResponse> signup(RegisterUserDto registerUserDto);
    ResponseEntity<AuthenticationResponse> authenticate(LoginUserDto loginUserDto);
}
