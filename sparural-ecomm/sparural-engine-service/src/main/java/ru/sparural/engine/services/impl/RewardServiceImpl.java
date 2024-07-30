package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.check.Reward;
import ru.sparural.engine.repositories.RewardRepository;
import ru.sparural.engine.services.RewardService;
import ru.sparural.engine.services.exception.ServiceException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

    private final RewardRepository rewardRepository;
    private final DtoMapperUtils dtoMapperUtils;
    private final CurrencyServiceImpl currencyService;

    @Override
    public Reward saveOrUpdate(Reward dto) {
        return createDtoFromEntity(rewardRepository.saveOrUpdate(createEntityFromDto(dto))
                .orElseThrow(() -> new ServiceException("Failed to create checks reward")));
    }

    @Override
    public ru.sparural.engine.entity.Reward createEntityFromDto(Reward dto) {
        return dtoMapperUtils.convert(dto, ru.sparural.engine.entity.Reward.class);
    }

    @Override
    public Reward createDtoFromEntity(ru.sparural.engine.entity.Reward entity) {
        return dtoMapperUtils.convert(entity, Reward.class);
    }

    @Override
    public List<Reward> getListByCheckId(Long checkId) {
        List<ru.sparural.engine.entity.Reward> entityList = rewardRepository.getListByCheckId(checkId);
        if (!entityList.isEmpty()) {
            List<Reward> dtoList = createDtoList(entityList);
            int i = 0;
            for (Reward dto : dtoList) {
                dto.setCurrency(currencyService.get(entityList.get(i).getCurrencyId()));
                i++;
            }
            return dtoList;
        }
        return null;
    }

    @Override
    public List<Reward> createDtoList(List<ru.sparural.engine.entity.Reward> entityList) {
        return dtoMapperUtils.convertList(Reward.class, entityList);
    }

    @Override
    public List<ru.sparural.engine.entity.Reward> batchSave(List<ru.sparural.engine.entity.Reward> list) {
        return rewardRepository.batchSave(list);
    }
}
