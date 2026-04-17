package com.example.Appointment_Booking.dto;

import com.example.Appointment_Booking.model.Appointment;
import com.example.Appointment_Booking.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class AppointmentResponse {
    private Long id;
    private String patientName;
    private String doctorName;
    private LocalDateTime appointmentTime;
    private Status status;

    public AppointmentResponse(Appointment app) {
        this.id = app.getId();
        this.patientName = app.getPatient().getFirstName() + " " + app.getPatient().getLastName();
        this.doctorName = app.getDoctor().getFirstName() + " " + app.getDoctor().getLastName();
        this.appointmentTime = app.getAppointmentTime();
        this.status = app.getStatus();
    }
}
