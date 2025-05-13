package com.bridge.medic.config.security.authorization;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bridge.medic.config.security.authorization.PermissionEnum.*;

@RequiredArgsConstructor
public enum RoleEnum {

    ADMIN(
            Set.of(
                    ADMIN_READ,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    ADMIN_CREATE,
                    SUPPORT_READ,
                    SUPPORT_UPDATE,
                    SUPPORT_DELETE,
                    SUPPORT_CREATE,
                    SPECIALIST_READ,
                    SPECIALIST_UPDATE,
                    SPECIALIST_DELETE,
                    SPECIALIST_CREATE,
                    USER_READ,
                    USER_UPDATE,
                    USER_DELETE,
                    USER_CREATE
            )
    ),
    SUPPORT(
            Set.of(
                    SUPPORT_READ,
                    SUPPORT_UPDATE,
                    SUPPORT_DELETE,
                    SUPPORT_CREATE
            )
    ),
    SPECIALIST(
            Set.of(
                    SPECIALIST_READ,
                    SPECIALIST_UPDATE,
                    SPECIALIST_DELETE,
                    SPECIALIST_CREATE
            )
    ),
    USER(
            Set.of(
                    USER_READ,
                    USER_UPDATE,
                    USER_DELETE,
                    USER_CREATE
            )
    );

    @Getter
    private final Set<PermissionEnum> permissionEnums;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissionEnums()
                .stream()
                .map(permissionEnum -> new SimpleGrantedAuthority(permissionEnum.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
