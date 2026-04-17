package com.example.Appointment_Booking.Service;

import com.example.Appointment_Booking.Repository.AppointmentRepository;
import com.example.Appointment_Booking.Repository.DoctorAvailabilityRepository;
import com.example.Appointment_Booking.Repository.DoctorScheduleRepository;
import com.example.Appointment_Booking.Repository.UserRepository;
import com.example.Appointment_Booking.dto.AppointmentRequest;
import com.example.Appointment_Booking.dto.AvailabilityRequest;
import com.example.Appointment_Booking.dto.DoctorScheduleRequest;
import com.example.Appointment_Booking.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private  final DoctorScheduleRepository doctorScheduleRepository;
    private  final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    public Map<String, List<LocalTime>> generateDoctorSchedule(String email) {
        Map<String, List<LocalTime>> doctorSchedule = new LinkedHashMap<>();
        User doctor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        LocalDate date = LocalDate.now();
        LocalDate endDate = date.plusDays(30);
        while (date.isBefore(endDate)) {
            DayOfWeek day = date.getDayOfWeek();
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                date = date.plusDays(1);
                continue;
            }

            Optional<DoctorAvailability> availability =
                    doctorAvailabilityRepository.findByDoctorIdAndDate(doctor.getId(), date);

            if (availability.isPresent()) {
                DoctorAvailability avail = availability.get();
                if (avail.getType() != AvailabilityType.DAY_OFF) {
                    addAvailableSlots(doctorSchedule, doctor.getId(), date,
                            avail.getStartTime(), avail.getEndTime(),
                            avail.getBreakStartTime(), avail.getBreakEndTime(),
                            avail.getSlotDurationMinutes());
                }
            } else {
                Optional<DoctorSchedule> schedule = doctorScheduleRepository
                        .findByDoctorIdAndDayOfWeek(doctor.getId(), date.getDayOfWeek());
                if (schedule.isPresent()) {
                    DoctorSchedule scheduled = schedule.get();
                    addAvailableSlots(doctorSchedule, doctor.getId(), date,
                            scheduled.getStartTime(), scheduled.getEndTime(),
                            scheduled.getBreakStartTime(), scheduled.getBreakEndTime(),
                            scheduled.getSlotDurationMinutes());
                }
            }

            date = date.plusDays(1);
        }
        return doctorSchedule;
    }
    private List<LocalTime> generateSlots(LocalTime startTime, LocalTime endTime, LocalTime breakStart, LocalTime breakEnd, Integer slotDuration){
        List<LocalTime> slots = new ArrayList<>();
        while (startTime.isBefore(endTime)) {
            if (startTime.isBefore(breakEnd) && startTime.plusMinutes(slotDuration).isAfter(breakStart)) {
                startTime = breakEnd;
                continue;
            }
            slots.add(startTime);
            startTime = startTime.plusMinutes(slotDuration);
        }
        return slots;
    }
    private void addAvailableSlots(Map<String, List<LocalTime>> doctorSchedule,
                                   Long doctorId,
                                   LocalDate date,
                                   LocalTime startTime,
                                   LocalTime endTime,
                                   LocalTime breakStart,
                                   LocalTime breakEnd,
                                   Integer slotDuration) {
        List<LocalTime> timeList = generateSlots(startTime, endTime, breakStart, breakEnd, slotDuration);

        String dayKey = date.format(DateTimeFormatter.ofPattern("EEEE dd.MM.yyyy", Locale.GERMAN));
        doctorSchedule.put(dayKey, timeList);
    }
    public void createOrUpdateDoctorSchedule(DoctorScheduleRequest request,String email) {
        User doctor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        DoctorSchedule doctorSchedule = doctorScheduleRepository
                .findByDoctorIdAndDayOfWeek(doctor.getId(), request.getDayOfWeek())
                .orElse(new DoctorSchedule());
        doctorSchedule.setDoctor(doctor);
        doctorSchedule.setDayOfWeek(request.getDayOfWeek());
        doctorSchedule.setSlotDurationMinutes(request.getSlotDurationMinutes());
        doctorSchedule.setBreakStartTime(request.getBreakStartTime());
        doctorSchedule.setBreakEndTime(request.getBreakEndTime());
        doctorSchedule.setStartTime(request.getStartTime());
        doctorSchedule.setEndTime(request.getEndTime());
        doctorScheduleRepository.save(doctorSchedule);
    }
    public void createOrUpdateDoctorAvailability(AvailabilityRequest availability,String email) {
        User doctor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        DoctorAvailability  doctorAvailability = doctorAvailabilityRepository.findByDoctorIdAndDate(doctor.getId(),availability.getDate())
                .orElse(new DoctorAvailability());
        doctorAvailability.setDoctor(doctor);
        doctorAvailability.setSlotDurationMinutes(availability.getSlotDurationMinutes());
        doctorAvailability.setBreakStartTime(availability.getBreakStartTime());
        doctorAvailability.setBreakEndTime(availability.getBreakEndTime());
        doctorAvailability.setStartTime(availability.getStartTime());
        doctorAvailability.setEndTime(availability.getEndTime());
        doctorAvailability.setDate(availability.getDate());
        doctorAvailability.setType(availability.getAvailabilityType());
        doctorAvailabilityRepository.save(doctorAvailability);
    }

    public List<DoctorSchedule> doctorScheduleList(String email) {
        User doctor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return doctorScheduleRepository.findByDoctorId(doctor.getId());
    }

    public List<Appointment> getBookedAppointments(String email, Status status) {
        User doctor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        if (status != null) {
            return appointmentRepository.findByDoctorIdAndStatus(doctor.getId(), status);
        }
        return appointmentRepository.findByDoctorId(doctor.getId());
    }

    public Appointment addAppointment (String email, AppointmentRequest appointmentRequest) {
        User doctor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        User patient = userRepository.findByEmail(appointmentRequest.getPatientEmail())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Map<String, List<LocalTime>> available = getAvailableSlots(email);

        LocalDateTime requested = appointmentRequest.getAppointmentTime();

        String key = requested.format(DateTimeFormatter.ofPattern("EEEE dd.MM.yyyy", Locale.GERMAN));

        if (!available.containsKey(key) ||
                !available.get(key).contains(requested.toLocalTime())) {

            throw new RuntimeException("Invalid or unavailable slot");
        }
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setAppointmentTime(appointmentRequest.getAppointmentTime());
        appointment.setPatient(patient);
        appointment.setStatus(Status.CONFIRMED);
        try {
            appointmentRepository.save(appointment);
        } catch (Exception e) {
            throw new RuntimeException("Slot already booked");
        }
        return appointment;
    }
    public Appointment cancelAppointment(Long appointmentId, String email) {
        User doctor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        if (appointment.getStatus() == Status.CANCELLED) {
            throw new RuntimeException("Appointment already cancelled");
        }

        if (appointment.getAppointmentTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot cancel past appointment");
        }
        appointment.setStatus(Status.CANCELLED);
        appointmentRepository.save(appointment);
        return appointment;
    }

    public Appointment updateAppointment(Long appointmentId, String email, AppointmentRequest appointmentRequest) {
        cancelAppointment(appointmentId, email);
      return addAppointment(email, appointmentRequest);
    }

    public Map<String, List<LocalTime>> getAvailableSlots(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, List<LocalTime>> slots = generateDoctorSchedule(user.getEmail());

        List<Appointment> bookedAppointments =
                appointmentRepository.findByDoctorIdAndStatus(user.getId(), Status.CONFIRMED);

        for (Appointment appt : bookedAppointments) {
            String key = appt.getAppointmentTime()
                    .format(DateTimeFormatter.ofPattern("EEEE dd.MM.yyyy", Locale.GERMAN));

            LocalTime time = appt.getAppointmentTime().toLocalTime();

            if (slots.containsKey(key)) {
                slots.get(key).remove(time);
            }
        }
        return slots;
    }

}
