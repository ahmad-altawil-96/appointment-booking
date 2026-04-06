package com.example.Appointment_Booking.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="appointments")
@Data
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private User patient;
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private User doctor;
    private LocalDateTime appointmentTime;
    @Enumerated(EnumType.STRING)
    private Status status;


}
