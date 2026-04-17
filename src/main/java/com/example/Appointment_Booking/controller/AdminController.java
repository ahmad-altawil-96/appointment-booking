package com.example.Appointment_Booking.controller;

import com.example.Appointment_Booking.Service.Adminservice;
import com.example.Appointment_Booking.dto.UserResponse;
import com.example.Appointment_Booking.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final Adminservice adminservice;

    @PutMapping("/update")
    public UserResponse updateUser(@RequestParam String email, @RequestParam Role role) {
        return adminservice.updateUser(email, role);
    }

    @DeleteMapping("/delete")
    public String deleteUser(@RequestParam String email) {
        return adminservice.deleteUser(email);
    }

    @GetMapping("/userlist")
    public List<UserResponse> getAllUsers(@RequestParam(required = false)Role role) {
        return adminservice.findAllByRole(role);
    }
}
