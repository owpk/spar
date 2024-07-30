package ru.sparural.rest.security;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * @author Vorobyev Vyacheslav
 */
@ControllerAdvice
public class SecurityControllerAdvice {

    /**
     * Controller UserPrincipal argument resolver
     *
     * @param authentication authentication
     */
    @ModelAttribute
    public UserPrincipal customPrincipal(Authentication authentication) {
        return authentication == null ? null :
                (UserPrincipal) authentication.getPrincipal();
    }
}
