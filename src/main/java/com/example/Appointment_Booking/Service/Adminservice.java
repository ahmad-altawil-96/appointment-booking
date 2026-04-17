package com.example.Appointment_Booking.Service;

import com.example.Appointment_Booking.Repository.UserRepository;
import com.example.Appointment_Booking.dto.UserResponse;
import com.example.Appointment_Booking.model.Role;
import com.example.Appointment_Booking.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Adminservice {
    private final UserRepository userRepository;


    public UserResponse updateUser(String email, Role role) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(role);
        userRepository.save(user);
        return  new UserResponse(user.getFirstName(),user.getLastName(),user.getEmail(),user.getRole());
    }

    public List<UserResponse> findAllByRole(Role role) {
        if  (role == null) {
            return userRepository.findAll().stream().map(user -> new UserResponse(
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getRole())).toList();
        }
        return userRepository.findByRole(role).stream()
                .map(user -> new UserResponse(
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getRole()))
                .toList();
    }

    public String deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
        return user.getEmail()+" has been deleted";
    }
}
