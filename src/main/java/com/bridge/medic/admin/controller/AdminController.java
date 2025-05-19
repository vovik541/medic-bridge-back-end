package com.bridge.medic.admin.controller;

import com.bridge.medic.config.security.authorization.RoleEnum;
import com.bridge.medic.config.security.service.AuthenticatedUserService;
import com.bridge.medic.user.model.User;
import com.bridge.medic.user.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final AuthenticatedUserService authenticatedUserService;

    @GetMapping
    @PreAuthorize("hasAuthority('admin:read')")
    public String get() {
        return "GET:: admin controller";
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin:create')")
    @Hidden
    public String post() {
        return "POST:: admin controller";
    }
    @PostMapping("/change-role")
    public ResponseEntity<?> changeUserRole(@RequestParam("userId") Long userId,
                                                @RequestParam("newRole") String newRole) {
        if (Stream.of(RoleEnum.values()).map(Enum::name).anyMatch(x -> x.equals(newRole))){
            userService.changeUserRole(userId, RoleEnum.valueOf(newRole));
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('admin:update')")
    @Hidden
    public String put() {
        return "PUT:: admin controller";
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('admin:delete')")
    @Hidden
    public String delete() {
        return "DELETE:: admin controller";
    }
}
