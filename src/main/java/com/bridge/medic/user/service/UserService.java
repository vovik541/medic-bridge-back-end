package com.bridge.medic.user.service;

import com.bridge.medic.user.dto.request.ChangePasswordRequest;
import com.bridge.medic.user.model.User;
import com.bridge.medic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;

    public Optional<User> finUserByEmailOrLogin(String emailOrLogin) {
        return repository.findByEmailOrLogin(emailOrLogin);
    }

    public Optional<User> finUserByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Optional<User> finUserByLogin(String email) {
        return repository.findByLogin(email);
    }

    public Optional<User> finUserById(int userId) {
        return repository.findById(userId);
    }

    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        repository.save(user);
    }
}
