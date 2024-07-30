package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.check.Item;
import ru.sparural.engine.entity.LoymaxChecksItem;

import java.util.List;

public interface ItemService {
    Item createDtoFromEntity(ru.sparural.engine.entity.Item entity);

    ru.sparural.engine.entity.Item createEntityFromDto(Item dto);

    Item getFromLoymaxTable(String itemId);

    Item save(Item item);

    Item update(Item item);

    void saveLoymaxItem(Long checkItemId, String itemId);

    List<Item> getListByCheckId(Long checkId);

    List<Item> createDtoList(List<ru.sparural.engine.entity.Item> entityList);

    List<ru.sparural.engine.entity.Item> batchSave(List<ru.sparural.engine.entity.Item> list);

    List<LoymaxChecksItem> batchLoymaxSave(List<LoymaxChecksItem> list);
}
