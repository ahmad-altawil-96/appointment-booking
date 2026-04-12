package com.example.Appointment_Booking.Repository;

import com.example.Appointment_Booking.model.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {
    Optional<DoctorAvailability> findByDoctorIdAndDate(Long aLong, LocalDate date);
}
