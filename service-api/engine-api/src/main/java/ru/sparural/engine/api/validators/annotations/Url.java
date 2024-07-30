package ru.sparural.engine.api.validators.annotations;

import ru.sparural.engine.api.validators.UrlValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Vorobyev Vyacheslav
 */
@Constraint(validatedBy = UrlValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Url {

    String message() default "Invalid url address";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
