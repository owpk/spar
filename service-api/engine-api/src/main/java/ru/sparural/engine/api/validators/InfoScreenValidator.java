package ru.sparural.engine.api.validators;

import ru.sparural.engine.api.dto.InfoScreenDto;
import ru.sparural.engine.api.enums.CitySelectValues;
import ru.sparural.engine.api.validators.annotations.ValidateInfoScreen;

import javax.validation.ConstraintValidatorContext;

/**
 * @author Vorobyev Vyacheslav
 */
public class InfoScreenValidator extends AbsDtoValidator<ValidateInfoScreen, InfoScreenDto> {

    @Override
    public boolean isValid(InfoScreenDto value, ConstraintValidatorContext context) {
        var draft = value.getDraft();
        if (!checkForNullField(draft) && !draft) {
            var citySelect = value.getCitySelect();
            if (checkForNullField(citySelect)) {
                setCustomMessage("When draft, city select is required", context);
                return false;
            }
        }

        var citySelect = value.getCitySelect();
        var cities = value.getCities();
        if (!checkForNullField(citySelect) && citySelect.equals(CitySelectValues.SELECTION.getVal())) {
            if (checkForNullOr(cities, x -> x.size() == 0)) {
                setCustomMessage("When city select is present, cities required", context);
                return false;
            }
            if (cities.stream().anyMatch(x -> x.getId() == null)) {
                setCustomMessage("ids required for cities", context);
                return false;
            }
        }
        return true;
    }
}
