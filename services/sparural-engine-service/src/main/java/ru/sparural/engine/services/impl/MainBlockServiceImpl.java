package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.MainBlockDto;
import ru.sparural.engine.entity.MainBlock;
import ru.sparural.engine.repositories.MainBlockRepository;
import ru.sparural.engine.services.MainBlockService;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.lang.module.ResolutionException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MainBlockServiceImpl implements MainBlockService {

    private final MainBlockRepository mainBlockRepository;
    private final DtoMapperUtils dtoMapperUtils;

    public MainBlockDto update(String code, MainBlockDto block) {
        var updated = mainBlockRepository.updateByCode(code, dtoMapperUtils.convert(block, MainBlock.class));
        return dtoMapperUtils.convert(updated.orElseThrow(ResolutionException::new), MainBlockDto.class);
    }

    public List<MainBlockDto> list(int offset, int limit) {
        return dtoMapperUtils.convertList(MainBlockDto.class, mainBlockRepository.getList(offset, limit));
    }

}
