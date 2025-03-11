package com.example.authService.service;

import com.example.authService.config.JwtUtil;
import com.example.authService.config.NotValidDataException;
import com.example.authService.model.User;
import com.example.authService.model.dto.UserDTORegister;
import com.example.authService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String registerPlayer(UserDTORegister userDTORegister) {
        User user = new User();
        user.setRole("player");
        user.setPassword(passwordEncoder.encode(userDTORegister.getPassword()));
        user.setEmail(userDTORegister.getEmail());
        user.setUsername(userDTORegister.getUsername());

        // verificar si existe el usuario y el correo
        verifyUsernameExisting(user);
        verifyEmailExisting(user);

        userRepository.save(user);
        return String.valueOf(user.getId());
    }

    // verificar si existe el username
    private void verifyUsernameExisting(User user) {
        String username = user.getUsername();
        Optional<User> existingUser = userRepository.findByUsername(username);

        if (existingUser.isPresent())
            throw NotValidDataException.of("USERNAME_FOUND");
    }

    // verificar si existe el mail
    private void verifyEmailExisting(User user) {
        String email = user.getEmail();
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent())
            throw NotValidDataException.of("EMAIL_FOUND");
    }

    public String loginUser(String userEmail, String password) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        if (passwordEncoder.matches(password, user.getPassword())) {
            return jwtUtil.generateToken(Long.toString(user.getId())); // Devuelve el token
        }
        throw new RuntimeException("Invalid credentials");
    }
}