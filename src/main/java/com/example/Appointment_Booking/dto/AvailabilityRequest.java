package com.example.Appointment_Booking.dto;

import com.example.Appointment_Booking.model.AvailabilityType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AvailabilityRequest {
    private AvailabilityType availabilityType;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer slotDurationMinutes;
    private LocalTime breakStartTime;
    private LocalTime breakEndTime;
}
