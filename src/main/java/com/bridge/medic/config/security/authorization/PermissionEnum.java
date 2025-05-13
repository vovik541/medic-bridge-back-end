package com.bridge.medic.config.security.authorization;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PermissionEnum {

    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),
    SUPPORT_READ("support:read"),
    SUPPORT_UPDATE("support:update"),
    SUPPORT_CREATE("support:create"),
    SUPPORT_DELETE("support:delete"),
    SPECIALIST_READ("specialist:read"),
    SPECIALIST_UPDATE("specialist:update"),
    SPECIALIST_CREATE("specialist:create"),
    SPECIALIST_DELETE("specialist:delete"),
    USER_READ("user:read"),
    USER_UPDATE("user:update"),
    USER_CREATE("user:create"),
    USER_DELETE("user:delete");
    @Getter
    private final String permission;
}
