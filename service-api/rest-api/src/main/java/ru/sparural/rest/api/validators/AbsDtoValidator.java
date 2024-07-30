package ru.sparural.rest.api.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.util.function.Predicate;

/**
 * @author Vorobyev Vyacheslav
 */
public abstract class AbsDtoValidator<C extends Annotation, R> implements ConstraintValidator<C, R> {

    protected void setCustomMessage(String msg, ConstraintValidatorContext ctx) {
        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate(msg)
                .addConstraintViolation();
    }

    protected Boolean checkForNullField(Object field) {
        return field == null;
    }

    protected <T> Boolean checkForNullOr(T object, Predicate<T> predicate) {
        return object == null || predicate.test(object);
    }

}