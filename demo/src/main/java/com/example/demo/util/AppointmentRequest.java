package com.example.demo.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRequest {

    @NonNull
    private LocalDate date;
    @NonNull
    @JsonProperty("start_time")
    private LocalTime from;
    @NonNull
    @JsonProperty("end_time")
    private LocalTime to;
    @NonNull
    @JsonProperty("owner_id")
    private long ownerID;
    @JsonProperty("invitees_emails")
    List<String> inviteesEmails;
}
