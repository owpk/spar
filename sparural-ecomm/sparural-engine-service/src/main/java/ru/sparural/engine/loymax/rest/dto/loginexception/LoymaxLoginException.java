package ru.sparural.engine.loymax.rest.dto.loginexception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoymaxLoginException {
    private String error;
    private String error_description;
}
