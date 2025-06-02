package com.bridge.medic.user.service;

import com.bridge.medic.config.security.service.AuthenticatedUserService;
import com.bridge.medic.user.dto.request.ChangePasswordRequest;
import com.bridge.medic.user.dto.request.UpdateUserInfoRequest;
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
    private final UserRepository userRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public void deleteUser(){
        User currentUser = authenticatedUserService.getCurrentUser();
        userRepository.delete(currentUser);
    }

    public void updateUserInfo(UpdateUserInfoRequest request) {
        User currentUser = authenticatedUserService.getCurrentUser();
        if (!currentUser.getEmail().equals(request.getEmail())) {
            if (this.finUserByEmail(request.getEmail()).isEmpty()) {
                currentUser.setEmail(request.getEmail());
            }
        }
        if (!currentUser.getLogin().equals(request.getLogin())) {
            if (this.finUserByLogin(request.getLogin()).isEmpty()) {
                currentUser.setLogin(request.getLogin());
            }
        }

        if (!currentUser.getFirstName().equals(request.getFirstName())) {
            currentUser.setFirstName(request.getFirstName());
        }

        if (!currentUser.getLastName().equals(request.getLastName())) {
            currentUser.setLastName(request.getLastName());
        }

        userRepository.save(currentUser);
    }

    public Optional<User> finUserByEmailOrLogin(String emailOrLogin) {
        return userRepository.findByEmailOrLogin(emailOrLogin);
    }

    public Optional<User> finUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> finUserByLogin(String email) {
        return userRepository.findByLogin(email);
    }

    public Optional<User> finUserById(int userId) {
        return userRepository.findById(userId);
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

        userRepository.save(user);
    }
}
