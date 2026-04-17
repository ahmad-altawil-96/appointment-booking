package com.example.Appointment_Booking.controller;


import com.example.Appointment_Booking.Service.DoctorService;
import com.example.Appointment_Booking.dto.AppointmentRequest;
import com.example.Appointment_Booking.dto.AvailabilityRequest;
import com.example.Appointment_Booking.dto.DoctorScheduleRequest;
import com.example.Appointment_Booking.model.Appointment;
import com.example.Appointment_Booking.model.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping("/schedule")
    public Map<String, List<LocalTime>> generateDoctorSchedule(Authentication authentication) {
        String email = authentication.getName();
        return doctorService.generateDoctorSchedule(email);
    }

    @PostMapping("/schedule/create")
    public String createDoctorSchedule(@RequestBody DoctorScheduleRequest request,Authentication authentication) {
        String email = authentication.getName();
        doctorService.createOrUpdateDoctorSchedule(request,email);
        return "Schedule created successfully";
    }
    @PostMapping("/availability/create")
    public String createDoctoravailability(@RequestBody AvailabilityRequest request, Authentication authentication) {
        String email = authentication.getName();
        doctorService.createOrUpdateDoctorAvailability(request,email);
        return "availability created successfully";
    }

    @GetMapping("/slots/booked")
    public List<Appointment>  getDoctorAppointement(Authentication authentication,@RequestParam(required = false) Status status) {
        String email = authentication.getName();
        return doctorService.getBookedAppointments(email,status);
    }

    @PostMapping("/slots/add")
    public Appointment addDoctorAppointment(@RequestBody AppointmentRequest request, Authentication authentication) {
        String email = authentication.getName();
        return doctorService.addAppointment(email,request);
    }

    @DeleteMapping("slots/delete/{appointmentId}")
    public Appointment deleteDoctorAppointment(Authentication authentication,@PathVariable Long appointmentId) {
        String email = authentication.getName();
        return doctorService.cancelAppointment(appointmentId,email);
    }

    @PutMapping("/slots/update//{appointmentId}")
    public Appointment updateDoctorAppointment(@RequestBody AppointmentRequest request, Authentication authentication,@PathVariable Long appointmentId) {
        String email = authentication.getName();
        return doctorService.updateAppointment(appointmentId,email,request);
    }

    @GetMapping("/slots/available")
    public Map<String, List<LocalTime>> getAvailableSlots(Authentication authentication) {
        String email = authentication.getName();
        return doctorService.getAvailableSlots(email);
    }

}
