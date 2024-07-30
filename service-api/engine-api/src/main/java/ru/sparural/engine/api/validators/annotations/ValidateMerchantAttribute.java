package ru.sparural.engine.api.validators.annotations;

import ru.sparural.engine.api.validators.MerchantAttributeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = MerchantAttributeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateMerchantAttribute {
    String message() default "Please enter name of merchants attribute";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
