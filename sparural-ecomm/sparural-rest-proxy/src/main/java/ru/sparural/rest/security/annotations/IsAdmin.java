package ru.sparural.rest.security.annotations;

import org.springframework.security.access.annotation.Secured;
import ru.sparural.gobals.RolesConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Vorobyev Vyacheslav
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Secured(RolesConstants.ROLE_ADMIN)
public @interface IsAdmin {
}
