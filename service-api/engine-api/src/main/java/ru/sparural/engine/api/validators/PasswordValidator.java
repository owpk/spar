package ru.sparural.engine.api.validators;

import ru.sparural.engine.api.validators.annotations.Password;

import javax.validation.ConstraintValidatorContext;

/**
 * @author Vorobyev Vyacheslav
 */
public class PasswordValidator extends AbsDtoValidator<Password, String> {

    public static final String regexp = "^.*(?=..*[0-9])(?=.*[A-Z]).*$";
    private Integer min;
    private Integer max;

    @Override
    public void initialize(Password constraintAnnotation) {
        super.initialize(constraintAnnotation);
        this.max = constraintAnnotation.max();
        this.min = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (checkForNullField(value)) return true;
        if (value.length() < min || value.length() > max) {
            setCustomMessage(String.format("Password must contains at least %d characters (max %d)", min, max), context);
            return false;
        }
        return value.matches(regexp);
    }
}
