package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.LoymaxMerchant;
import ru.sparural.engine.entity.Merchant;

import java.util.List;
import java.util.Optional;

public interface MerchantRepository {
    Optional<Merchant> saveOrUpdate(Merchant createMerchantRequest);

    Optional<Merchant> get(Long id);

    Boolean delete(Long id);

    Optional<Merchant> update(Long id, Merchant entity);

    List<Merchant> list(Integer offset,
                        Integer limit,
                        Double topLeftLongitude,
                        Double topLeftLatitude,
                        Double bottomRightLongitude,
                        Double bottomRightLatitude,
                        String status,
                        Long[] format,
                        Long[] attributes,
                        Boolean isAdmin);

    Optional<LoymaxMerchant> getLoymaxMerchant(String locationId);

    void insertAttributesToMerchant(Merchant result);

    Optional<Merchant> findById(Long id);

}

