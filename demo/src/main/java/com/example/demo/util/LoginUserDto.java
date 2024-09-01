package com.example.demo.util;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class LoginUserDto {
    private String email;
    private String password;

}