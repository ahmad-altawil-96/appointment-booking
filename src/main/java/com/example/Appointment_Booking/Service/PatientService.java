package com.example.Appointment_Booking.Service;

import com.example.Appointment_Booking.Repository.AppointmentRepository;
import com.example.Appointment_Booking.Repository.UserRepository;
import com.example.Appointment_Booking.dto.AppointmentRequest;
import com.example.Appointment_Booking.dto.AppointmentResponse;
import com.example.Appointment_Booking.dto.UserResponse;
import com.example.Appointment_Booking.model.Appointment;
import com.example.Appointment_Booking.model.Role;
import com.example.Appointment_Booking.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorService doctorService;


    public List<UserResponse> findAllDoctors() {
        return userRepository.findByRole(Role.DOCTOR)
                .stream()
                .map(user -> new UserResponse(
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getRole()))
                .toList();
    }

    public Map<String, List<LocalTime>> viewAvailableSlots(Long doctorId) {
        User user = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return doctorService.getAvailableSlots(user.getEmail());
    }

    public AppointmentResponse createAppointment(long doctorId, AppointmentRequest appointmentRequest) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("doctor not found"));
        Appointment app = doctorService.addAppointment(doctor.getEmail(),appointmentRequest);

        return new  AppointmentResponse(app);
    }
    public AppointmentResponse deleteAppointment(Long doctorId, Long appointmentId) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("doctor not found"));
        Appointment app = doctorService.cancelAppointment(appointmentId,doctor.getEmail());
        return new  AppointmentResponse(app);
    }


    public List<AppointmentResponse> findAllAppointments(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return appointmentRepository.findByPatientId(user.getId())
                .stream()
                .map(AppointmentResponse::new)
                .toList();
    }
}
