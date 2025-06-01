package com.bridge.medic.admin.controller;

import com.bridge.medic.admin.service.AdminService;
import com.bridge.medic.config.security.authorization.RoleEnum;
import com.bridge.medic.config.security.service.AuthenticatedUserService;
import com.bridge.medic.user.dto.UserDto;
import com.bridge.medic.user.mapper.UserMapper;
import com.bridge.medic.user.model.User;
import com.bridge.medic.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final AdminService adminService;
    private final AuthenticatedUserService authenticatedUserService;
    private final UserMapper userMapper;

    @PostMapping("/add-role")
    public ResponseEntity<?> addUserRole(@RequestParam("userId") Long userId,
                                            @RequestParam("newRole") String newRole) {
        if (Stream.of(RoleEnum.values()).map(Enum::name).anyMatch(x -> x.equals(newRole))) {
            adminService.addRole(userId, RoleEnum.valueOf(newRole));
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/remove-role")
    public ResponseEntity<?> removeRole(@RequestParam("userId") Long userId,
                                            @RequestParam("newRole") String newRole) {
        if (Stream.of(RoleEnum.values()).map(Enum::name).anyMatch(x -> x.equals(newRole))) {
            adminService.removeRole(userId, RoleEnum.valueOf(newRole));
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/lock")
    public ResponseEntity<?> blockUser(@RequestParam("userId") Long userId) {
        adminService.blockUser(userId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/unlock")
    public ResponseEntity<?> unblockUser(@RequestParam("userId") Long userId) {
        adminService.unblockUser(userId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    public Page<UserDto> getUsers(
            @RequestParam(required = false) String login,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        return adminService.getFilteredUsers(login, email, role, pageable)
                .map(this::toDto);
    }

    private UserDto toDto(User user) {
        return userMapper.userToUserDto(user);
    }
}
