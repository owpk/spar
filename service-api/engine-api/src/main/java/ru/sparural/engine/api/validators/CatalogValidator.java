package ru.sparural.engine.api.validators;

import ru.sparural.engine.api.dto.CatalogDto;
import ru.sparural.engine.api.enums.CitySelectValues;
import ru.sparural.engine.api.validators.annotations.ValidateCatalog;

import javax.validation.ConstraintValidatorContext;

/**
 * @author Vorobyev Vyacheslav
 */
public class CatalogValidator extends AbsDtoValidator<ValidateCatalog, CatalogDto> {

    @Override
    public boolean isValid(CatalogDto value, ConstraintValidatorContext context) {
        var draft = value.getDraft();
        if (!checkForNullField(draft)) {
            if (!draft) {
                var name = value.getName();
                if (checkForNullOr(name, String::isBlank)) {
                    setCustomMessage("When not draft, name is required", context);
                    return false;
                }
                var url = value.getUrl();
                if (checkForNullOr(url, String::isBlank)) {
                    setCustomMessage("When not draft, url is required", context);
                    return false;
                }
            }
        }
        var citySelection = value.getCitySelect();
        var cities = value.getCities();
        if (!checkForNullField(citySelection) && citySelection.equals(CitySelectValues.SELECTION.getVal())) {
            if (checkForNullOr(cities, x -> x.stream().anyMatch(i -> i.getId() == null))) {
                setCustomMessage("When city selection is Selection, cities ids is required", context);
                return false;
            }
        }
        return true;
    }
}
