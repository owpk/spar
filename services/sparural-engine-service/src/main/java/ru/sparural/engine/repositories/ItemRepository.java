package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.Item;
import ru.sparural.engine.entity.LoymaxChecksItem;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Optional<Item> save(Item entity);

    Optional<Item> get(Long id);

    Optional<Item> update(Item entity);

    Optional<LoymaxChecksItem> getFromLoymaxItem(String itemId);

    void saveLoymaxItem(Long checkItemId, String itemId);

    List<Item> getListByCheckId(Long checkId);

    List<Item> batchSave(List<Item> list);

    List<LoymaxChecksItem> batchLoymaxSave(List<LoymaxChecksItem> list);
}
