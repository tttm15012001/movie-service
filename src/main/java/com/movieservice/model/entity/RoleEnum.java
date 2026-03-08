package com.movieservice.model.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum RoleEnum {
    USER("user"),
    ADMIN("admin");

    private final String role;

    RoleEnum(String role) {
        this.role = role;
    }

    public static String getRoleByCode(String code) {
        return Arrays.stream(values())
                .filter(r -> r.name().equalsIgnoreCase(code))
                .map(RoleEnum::getRole)
                .findFirst()
                .orElse(null);
    }
}
