package ru.sparural.rest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Vorobyev Vyacheslav
 */
@NoArgsConstructor
@Setter
@Getter
public class ConfirmRegistrationRest {
    private String code;
}
