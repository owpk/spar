package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.MainBlock;

import java.util.List;
import java.util.Optional;

public interface MainBlockRepository {

    List<MainBlock> getList(int offset, int limit);

    Optional<MainBlock> updateByCode(String code, MainBlock mainBlock);
}
