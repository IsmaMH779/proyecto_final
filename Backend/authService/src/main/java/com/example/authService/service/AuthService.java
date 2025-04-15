package com.example.authService.service;

import com.example.authService.config.JwtUtil;
import com.example.authService.config.exceptions.NotValidDataException;
import com.example.authService.model.User;
import com.example.authService.model.dto.UserDTORegister;
import com.example.authService.model.dto.UserUpdateDTO;
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

    /*
     * REGISTER
     */

    public String registerPlayer(UserDTORegister userDTORegister) {
        User user = new User();
        user.setRole("player");
        user.setPassword(passwordEncoder.encode(userDTORegister.getPassword()));
        user.setEmail(userDTORegister.getEmail());
        user.setUsername(userDTORegister.getUsername());

        // verificar si existe el usuario y el correo
        verifyUsernameExisting(user.getUsername());
        verifyEmailExisting(user.getEmail());

        userRepository.save(user);
        return String.valueOf(user.getId());
    }

    public String registerOrganizer(UserDTORegister userDTORegister) {
        User user = new User();
        user.setRole("organizer");
        user.setPassword(passwordEncoder.encode(userDTORegister.getPassword()));
        user.setEmail(userDTORegister.getEmail());
        user.setUsername(userDTORegister.getUsername());

        // verificar si existe el usuario y el correo
        verifyUsernameExisting(user.getUsername());
        verifyEmailExisting(user.getEmail());

        userRepository.save(user);
        return String.valueOf(user.getId());
    }

    // verificar si existe el username
    private void verifyUsernameExisting(String username) {
        Optional<User> existingUser = userRepository.findByUsername(username);

        if (existingUser.isPresent())
            throw NotValidDataException.of("USERNAME_FOUND");
    }

    // verificar si existe el mail
    private void verifyEmailExisting(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent())
            throw NotValidDataException.of("EMAIL_FOUND");
    }

    /*
    * LOGIN
    */

    public String loginUser(String userEmail, String password) {
        // verificar que el correo existe y obtener el user
        User user = verifyEmailExistsAndGetUser(userEmail);
        // verificar que la contraseña es correcta y obtener el token
        String token = verifyMatchingPasswordAndGetGenerateToken(user, password);

        return token;
    }

    // verificar si existe el username
    private User verifyEmailExistsAndGetUser(String userEmail) {
        Optional<User> existingUser = userRepository.findByEmail(userEmail);

        if (existingUser.isEmpty()) throw NotValidDataException.of("EMAIL_NOT_FOUND");

        return existingUser.get();
    }

    private String verifyMatchingPasswordAndGetGenerateToken(User user, String password ) {
        if (passwordEncoder.matches(password, user.getPassword())) {
            return jwtUtil.generateToken(Long.toString(user.getId()));
        }
        throw NotValidDataException.of("INCORRECT_PASSWORD");
    }

    /*
     *  Update
     */

    public void updateUser(long userId, UserUpdateDTO userUpdateDTO) {
        Optional<User> userFromDb = userRepository.findById(userId);
        User user = null;

        // verificar que el nombre no este ya en la base de datos
        verifyUsernameExisting(userUpdateDTO.getUsername());

        // verificar que el usuario existe
        if (userFromDb.isPresent()) {
            user = userFromDb.get();
            user.setUsername(userUpdateDTO.getUsername());
        }

        // si user es null no guardar
        if (user != null) {
            userRepository.save(user);
        }
    }


}