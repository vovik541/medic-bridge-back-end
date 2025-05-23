package com.bridge.medic.auth.controller;

import com.bridge.medic.auth.dto.AuthenticationRequest;
import com.bridge.medic.auth.dto.AuthenticationResponse;
import com.bridge.medic.auth.dto.RegisterRequest;
import com.bridge.medic.auth.exception.UserAlreadyExistsException;
import com.bridge.medic.auth.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public Object register(
            @RequestBody RegisterRequest request
    ) {
        AuthenticationResponse response;
        try {
            response = service.register(request);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.unprocessableEntity();
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }
}
