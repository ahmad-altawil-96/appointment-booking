package com.example.Appointment_Booking.controller;

import com.example.Appointment_Booking.Service.PatientService;
import com.example.Appointment_Booking.dto.AppointmentRequest;
import com.example.Appointment_Booking.dto.AppointmentResponse;
import com.example.Appointment_Booking.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {
        private final PatientService patientService;

        @GetMapping("/doctors")
        public List<UserResponse> findAllDoctors() {
            return patientService.findAllDoctors();
        }


        @GetMapping("/slots/{doctorId}")
        public Map<String,List<LocalTime>> viewAvailableSlots(@PathVariable Long doctorId) {
            return patientService.viewAvailableSlots(doctorId);
        }

        @PostMapping("/book")
        public AppointmentResponse createAppointment(Long doctorId , @RequestBody AppointmentRequest request) {
            return patientService.createAppointment(doctorId, request);
        }

        @DeleteMapping("/cancel/{doctorId}/{appointmentId}")
        public AppointmentResponse deleteAppointment(@PathVariable Long doctorId,@PathVariable Long appointmentId) {
            return patientService.deleteAppointment(doctorId, appointmentId);
        }

        @GetMapping("/appointment")
        public List<AppointmentResponse> findAllAppointments (Authentication authentication) {
            String email = authentication.getName();
            return patientService.findAllAppointments(email);
        }
}
