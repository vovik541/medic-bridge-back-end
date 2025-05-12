package com.bridge.medic.user.controller;

import com.bridge.medic.user.dto.ChangePasswordRequest;
import com.bridge.medic.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PatchMapping
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        service.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check")
    public boolean doesUserAlreadyExist(String emailOrLogin) {
        return service.finUserByEmailOrLogin(emailOrLogin).isPresent();
    }

    @GetMapping("/check-by-login")
    public boolean doesUserAlreadyExistByLogin(String login) {
        return service.finUserByLogin(login).isPresent();
    }

    @GetMapping("/check-by-email")
    public boolean doesUserAlreadyExistByEmail(String email) {
        return service.finUserByEmail(email).isPresent();
    }
}
