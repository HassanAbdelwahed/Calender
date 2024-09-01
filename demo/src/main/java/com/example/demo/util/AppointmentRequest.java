package com.example.demo.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class AppointmentRequest {

    @NonNull
    private LocalDate date;
    @NonNull
    private LocalTime from;
    @NonNull
    private LocalTime to;
    @NonNull
    @JsonProperty("owner-id")
    private long ownerID;
    @JsonProperty("invitees-emails")
    List<String> inviteesEmails;
}
