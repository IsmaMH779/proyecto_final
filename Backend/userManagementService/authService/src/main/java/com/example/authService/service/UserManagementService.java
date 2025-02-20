package com.example.authService.service;

import com.example.authService.config.JwtUtil;
import com.example.authService.model.User;
import com.example.authService.model.dto.UserDTO;
import com.example.authService.model.dto.UserDTORegister;
import com.example.authService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserManagementService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public void registerPlayer(UserDTORegister userDTORegister) {
        User user = new User();

        Long userID = Long.valueOf(jwtUtil.getIDFromToken(userDTORegister.getToken()));

        user.setId(userID);
        user.setRole("player");
        user.setEmail(userDTORegister.getEmail());
        user.setUserName(userDTORegister.getUserName());
        user.setProfileImageURL(userDTORegister.getProfileImageURL());
        user.setBirthday(userDTORegister.getBithday());
        user.setLocation(userDTORegister.getLocation());

        // guardar el los datos en la base de datos
        userRepository.save(user);
    }


    public UserDTO getUserData(String userEmail, String password) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (passwordEncoder.matches(password, user.getPassword())) {
            return jwtUtil.generateToken(Long.toString(user.getId())); // Devuelve el token
        }
        throw new RuntimeException("Invalid credentials");
    }
}