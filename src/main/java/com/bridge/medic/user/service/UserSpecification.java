package com.bridge.medic.user.service;

import com.bridge.medic.config.security.authorization.model.Role;
import com.bridge.medic.user.model.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> loginLike(String login) {
        return (root, query, cb) ->
                login == null || login.isBlank()
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("login")), "%" + login.toLowerCase() + "%"); // ⬅ тут
    }

    public static Specification<User> emailLike(String email) {
        return (root, query, cb) ->
                email == null || email.isBlank()
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<User> hasRole(String roleName) {
        return (root, query, cb) -> {
            if (roleName == null || roleName.isBlank()) {
                return cb.conjunction();
            }
            Join<User, Role> roleJoin = root.join("roles", JoinType.LEFT);
            return cb.equal(cb.lower(roleJoin.get("name")), roleName.toLowerCase());
        };
    }
}