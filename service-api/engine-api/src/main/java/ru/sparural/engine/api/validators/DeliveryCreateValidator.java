package ru.sparural.engine.api.validators;

import ru.sparural.engine.api.dto.DeliveryCreateDTO;
import ru.sparural.engine.api.validators.annotations.delivery.ValidateDeliveryCreate;

import javax.validation.ConstraintValidatorContext;

public class DeliveryCreateValidator extends AbsDtoValidator<ValidateDeliveryCreate, DeliveryCreateDTO> {
    @Override
    public void initialize(ValidateDeliveryCreate constraintAnnotation) {
        super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(DeliveryCreateDTO value, ConstraintValidatorContext context) {
        var draft = value.getDraft();
        if (!checkForNullField(draft)) {
            if (!draft) {

                var title = value.getTitle();
                if (checkForNullOr(title, String::isBlank)) {
                    setCustomMessage("When not draft, title is required", context);
                    return false;
                }

                var shortDescription = value.getShortDescription();
                if (checkForNullOr(shortDescription, String::isBlank)) {
                    setCustomMessage("When not draft, shortDescription is required", context);
                    return false;
                }
            }
        } else {
            value.setDraft(true);
        }

        if (checkForNullField(value.getIsPublic())) {
            value.setIsPublic(false);
        }

        var url = value.getUrl();
        if (url != null && url.length() > 0) {
            if (checkForNullOr(url, String::isBlank)) {
                setCustomMessage("Url must not be an empty string", context);
                return false;
            }
        } else {
            value.setUrl(null);
        }
        return true;
    }
}
