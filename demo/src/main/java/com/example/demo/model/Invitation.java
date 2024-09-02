package com.example.demo.model;


import jakarta.persistence.*;
import lombok.*;

@Table(name = "invitations")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name="appointment_id")
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private InvitationStatus status;

    boolean seen = false;
}
