package ru.sparural.engine.api.validators;

import ru.sparural.engine.api.dto.OnboxBannerDto;
import ru.sparural.engine.api.enums.CitySelectValues;
import ru.sparural.engine.api.validators.annotations.ValidateOnboxBanner;
import ru.sparural.engine.api.validators.utils.ReflectUtils;

import javax.validation.ConstraintValidatorContext;

/**
 * @author Vorobyev Vyacheslav
 */
public class OnboxBannerValidator extends AbsDtoValidator<ValidateOnboxBanner, OnboxBannerDto> {

    @Override
    public boolean isValid(OnboxBannerDto value, ConstraintValidatorContext context) {
        if (value.getDraft() != null) {
            var draft = value.getDraft();
            if (!draft) {
                var title = value.getTitle();
                if (checkForNullOr(title, String::isBlank)) {
                    setCustomMessage("When not draft, title is required", context);
                    return false;
                }
                var desc = value.getDescription();
                if (checkForNullOr(desc, String::isBlank)) {
                    setCustomMessage("When not draft, description is required", context);
                    return false;
                }
                var order = value.getOrder();
                if (order == null) {
                    setCustomMessage("When not draft, order is required", context);
                    return false;
                }
                var citySelect = value.getCitySelect();
                if (citySelect == null) {
                    setCustomMessage("When not draft, city select is required", context);
                    return false;
                }
            }
        }

        var cities = value.getCities();
        var citySelection = value.getCitySelect();
        if (citySelection != null && citySelection.equals(CitySelectValues.SELECTION.getVal())) {
            if (cities == null || cities.size() == 0) {
                setCustomMessage("When city select is 'Selection', cities is required", context);
                return false;
            }
            if (cities.stream().anyMatch(x -> x.getId() == null)) {
                setCustomMessage("Cities ids is required", context);
                return false;
            }
        }

        var url = value.getUrl();
        var nav = value.getMobileNavigateTarget();
        if (url != null) {
            if (ReflectUtils.checkIfSomeFieldsIsPresent(nav)) {
                setCustomMessage("Such a navigation target exists, if url is specified, then this field should be absent", context);
                return false;
            } else {
                return url.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
            }
        }
        return true;
    }

}