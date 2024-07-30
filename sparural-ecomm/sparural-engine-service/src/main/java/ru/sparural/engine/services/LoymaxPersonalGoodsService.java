package ru.sparural.engine.services;

import java.util.List;

public interface LoymaxPersonalGoodsService {
    void bindPersonalGoodToLoymaxGood(Long personalGoodId, Long loymaxGoodId);

    void bindPersonalGoodToLoymaxGood(List<Long> personalGoodId, List<Long> loymaxGoodId);
}

