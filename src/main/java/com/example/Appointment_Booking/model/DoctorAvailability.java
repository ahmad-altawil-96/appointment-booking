package com.example.Appointment_Booking.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name="doctor_availability")
@Data
public class DoctorAvailability {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private User doctor;

    private LocalDate date;

    private LocalTime startTime;
    private LocalTime endTime;

    private Integer slotDurationMinutes;

    @Enumerated(EnumType.STRING)
    private AvailabilityType type;

    private LocalTime breakStartTime;
    private LocalTime breakEndTime;
}
