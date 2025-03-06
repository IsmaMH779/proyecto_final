package com.example.userManagementService.service;

import com.example.userManagementService.config.JwtUtil;
import com.example.userManagementService.model.User;
import com.example.userManagementService.model.dto.PlayerDTO;
import com.example.userManagementService.model.dto.RegisterPlayerDTO;
import com.example.userManagementService.repository.UserRepository;
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

    public void registerPlayer(RegisterPlayerDTO registerPlayerDTO, String userId) {
        User user = new User();

        Long userID = Long.valueOf(userId);
        user.setId(userID);
        user.setRole("player");
        user.setEmail(registerPlayerDTO.getEmail());
        user.setUserName(registerPlayerDTO.getUserName());
        user.setProfileImageURL(registerPlayerDTO.getProfileImageURL());
        user.setBirthday(registerPlayerDTO.getBirthday());
        user.setLocation(registerPlayerDTO.getLocation());

        // guardar el los datos en la base de datos
        userRepository.save(user);
    }


    public PlayerDTO getUserData(String token) {

        Long userId = Long.valueOf(jwtUtil.getIDFromToken(token));

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User  not found"));

        PlayerDTO playerDTO = new PlayerDTO();

        playerDTO.setRole(user.getRole());
        playerDTO.setMail(user.getEmail());
        playerDTO.setUserName(user.getUserName());
        playerDTO.setProfileImageURL(user.getProfileImageURL());
        playerDTO.setPhoneNumber(user.getNumber1());
        playerDTO.setBirthday(user.getBirthday());
        playerDTO.setLocation(user.getLocation());

        return playerDTO;
    }
}