package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class UsersGroupUser {
    private List<Long> users;
}
