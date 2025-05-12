package com.bridge.medic.user.service;

import com.bridge.medic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MedicBridgeUserDetailsService implements UserDetailsService {
    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String emailOrLogin) throws UsernameNotFoundException {
        return repository.findByEmailOrLogin(emailOrLogin).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
