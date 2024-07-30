package ru.sparural.rest.security;

import lombok.AllArgsConstructor;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 * Wrap principal to custom principal to obtain user id
 */
@AllArgsConstructor
public class UserPrincipal {
    private Long userId;
    private String name;
    private List<String> securedRoles;

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public List<String> getSecuredRoles() {
        return securedRoles;
    }
}
