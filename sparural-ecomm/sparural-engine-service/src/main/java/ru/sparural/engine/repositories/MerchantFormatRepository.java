package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.MerchantFormat;

import java.util.List;
import java.util.Optional;

public interface MerchantFormatRepository {
    List<MerchantFormat> list(Integer offset, Integer limit, List<String> nameNotEqual);

    Optional<MerchantFormat> get(Long id);

    Optional<MerchantFormat> getByName(String name);

    Optional<MerchantFormat> create(MerchantFormat entity);

    List<MerchantFormat> batchSave(List<MerchantFormat> merchantFormats);

}
