package ru.sparural.rest.dto;

import lombok.Data;

/**
 * @author Vyacheslav Vorobev
 */
@Data
public class JwtRequest {
    private String username;
    private String password;
}