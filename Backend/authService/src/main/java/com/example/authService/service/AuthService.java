package com.example.authService.service;

import com.example.authService.config.JwtUtil;
import com.example.authService.model.User;
import com.example.authService.model.dto.UserDTORegister;
import com.example.authService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public User registerPlayer(UserDTORegister userDTORegister) {
        User user = new User();
        user.setRole("player");
        user.setPassword(passwordEncoder.encode(userDTORegister.getPassword()));
        user.setEmail(userDTORegister.getEmail());
        user.setUsername(userDTORegister.getUsername());
        return userRepository.save(user);
    }

    public String loginUser(String userEmail, String password) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        if (passwordEncoder.matches(password, user.getPassword())) {
            return jwtUtil.generateToken(Long.toString(user.getId())); // Devuelve el token
        }
        throw new RuntimeException("Invalid credentials");
    }
}