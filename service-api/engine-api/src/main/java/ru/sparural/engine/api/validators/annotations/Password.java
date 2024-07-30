package ru.sparural.engine.api.validators.annotations;

import ru.sparural.engine.api.validators.PasswordValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Vorobyev Vyacheslav
 */
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {

    int max() default 25;

    int min() default 6;

    String message() default "Пароль должен содержать как минимум один символ нижнего регистра, символ верхнего регистра и цифру.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}