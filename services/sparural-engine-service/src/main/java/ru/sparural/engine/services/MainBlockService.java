package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.MainBlockDto;

import java.util.List;

public interface MainBlockService {
    List<MainBlockDto> list(int offset, int limit);

    MainBlockDto update(String code, MainBlockDto mainBlockDto);
}
