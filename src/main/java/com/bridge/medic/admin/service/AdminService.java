package com.bridge.medic.admin.service;

import com.bridge.medic.config.security.authorization.RoleEnum;
import com.bridge.medic.config.security.authorization.model.Role;
import com.bridge.medic.config.security.authorization.repozitory.RoleRepository;
import com.bridge.medic.user.model.User;
import com.bridge.medic.user.repository.UserRepository;
import com.bridge.medic.user.service.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public void addRole(Long userId, RoleEnum role) {
        User user = userRepository.findById(userId.intValue()).orElseThrow();
        Role roleEntity = roleRepository.findByName(role.name()).orElseThrow();
        user.addRoleIfAbsent(roleEntity);
        userRepository.save(user);
    }

    public void removeRole(Long userId, RoleEnum role) {
        User user = userRepository.findById(userId.intValue()).orElseThrow();
        Role roleEntity = roleRepository.findByName(role.name()).orElseThrow();
        user.removeRoleIfPresent(roleEntity);
        userRepository.save(user);
    }

    public void blockUser(Long userId) {
        User user = userRepository.findById(userId.intValue()).orElseThrow();
        user.setIsLocked(true);
        userRepository.save(user);
    }
    public void unblockUser(Long userId) {
        User user = userRepository.findById(userId.intValue()).orElseThrow();
        user.setIsLocked(false);
        userRepository.save(user);
    }
    public Page<User> getFilteredUsers(String login, String email, String role, Pageable pageable) {
        Specification<User> spec = Specification.where(UserSpecification.loginLike(login))
                .and(UserSpecification.emailLike(email))
                .and(UserSpecification.hasRole(role));

        return userRepository.findAll(spec, pageable);
    }
}
