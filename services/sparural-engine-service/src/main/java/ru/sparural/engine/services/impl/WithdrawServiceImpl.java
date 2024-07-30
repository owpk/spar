package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.check.Withdraw;
import ru.sparural.engine.repositories.WithdrawRepository;
import ru.sparural.engine.services.CurrencyService;
import ru.sparural.engine.services.WithdrawService;
import ru.sparural.engine.services.exception.ServiceException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WithdrawServiceImpl implements WithdrawService {

    private final DtoMapperUtils dtoMapperUtils;
    private final WithdrawRepository withdrawRepository;
    private final CurrencyService currencyService;

    @Override
    public Withdraw saveOrUpdate(Withdraw dto) {
        return createDtoFromEntity(withdrawRepository.saveOrUpdate(createEntityFromDto(dto))
                .orElseThrow(() -> new ServiceException("Failed to create checks_withdraws")));
    }

    @Override
    public ru.sparural.engine.entity.Withdraw createEntityFromDto(Withdraw dto) {
        return dtoMapperUtils.convert(dto, ru.sparural.engine.entity.Withdraw.class);
    }

    @Override
    public Withdraw createDtoFromEntity(ru.sparural.engine.entity.Withdraw entity) {
        return dtoMapperUtils.convert(entity, Withdraw.class);
    }

    @Override
    public List<Withdraw> getListByCheckId(Long checkId) {
        List<ru.sparural.engine.entity.Withdraw> entityList = withdrawRepository
                .getListByCheckId(checkId);
        if (!entityList.isEmpty()) {
            List<Withdraw> dtoList = createDtoList(entityList);
            int i = 0;
            for (Withdraw dto : dtoList) {
                dto.setCurrency(currencyService.get(entityList.get(i).getCurrencyId()));
                i++;
            }
            return dtoList;
        }
        return null;
    }

    @Override
    public List<Withdraw> createDtoList(List<ru.sparural.engine.entity.Withdraw> entityList) {
        return dtoMapperUtils.convertList(Withdraw.class, entityList);
    }

    @Override
    public List<ru.sparural.engine.entity.Withdraw> batchSave(List<ru.sparural.engine.entity.Withdraw> list) {
        return withdrawRepository.batchSave(list);
    }
}
