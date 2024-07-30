package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;
import ru.sparural.engine.api.enums.Genders;

import java.util.List;

@Data
@ToString
public class User {
    private Long id;
    private List<Role> roles;
    private String email;
    private String phoneNumber;
    private String password;
    private String firstName;
    private String lastName;
    private Long birthday;
    private String patronymicName;
    private Genders gender;
    private Boolean draft;
    private Boolean smsAllowed;
    private Boolean emailAllowed;
    private Boolean viberAllowed;
    private Boolean whatsappAllowed;
    private Boolean pushAllowed;
    private Boolean rejectPaperChecks;
}
