package com.example.demo.controller;


import com.example.demo.service.AuthenticationService;
import com.example.demo.util.AuthenticationResponse;
import com.example.demo.util.LoginUserDto;
import com.example.demo.util.RegisterUserDto;
import com.example.demo.util.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth/")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("signUp")
    public ResponseEntity<AuthenticationResponse> signup(@RequestBody RegisterUserDto registerUserDto) {
        return authenticationService.signup(registerUserDto);
    }

    @PostMapping("login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        return authenticationService.authenticate(loginUserDto);
    }
}
