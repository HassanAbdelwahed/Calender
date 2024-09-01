package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Table(name = "appointments")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    @NonNull
    private LocalDate date;

    @NonNull
    private LocalTime from;

    @NonNull
    private LocalTime to;

    private boolean canceled;

    @ManyToOne
    @JoinColumn(name="owner_id")
    private User appointmentOwner;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Invitation> invitations;

}
