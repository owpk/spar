package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class UserRequest {
    private String firstName;
    private String lastName;
    private String patronymicName;
    private String phoneNumber;
    private String email;
    private String gender;
    private Long birthday;
    private boolean draft;
    private List<String> roles;
}
