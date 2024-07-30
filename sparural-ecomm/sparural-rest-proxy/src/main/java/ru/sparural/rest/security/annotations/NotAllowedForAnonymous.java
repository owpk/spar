package ru.sparural.rest.security.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static ru.sparural.gobals.RolesConstants.*;

/**
 * @author Vorobyev Vyacheslav
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole('" + ROLE_MANAGER + "','" + ROLE_ADMIN + "','" + ROLE_CLIENT + "')")
public @interface NotAllowedForAnonymous {
}