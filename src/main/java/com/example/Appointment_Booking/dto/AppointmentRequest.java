package com.example.Appointment_Booking.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentRequest {
    @Email
    private String patientEmail;
    private LocalDateTime appointmentTime;
}
