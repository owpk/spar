package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.check.Item;
import ru.sparural.engine.entity.LoymaxChecksItem;
import ru.sparural.engine.repositories.ItemRepository;
import ru.sparural.engine.services.ItemService;
import ru.sparural.engine.services.exception.ServiceException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final DtoMapperUtils dtoMapperUtils;
    private final ItemRepository itemRepository;

    @Override
    public Item save(Item item) {
        return createDtoFromEntity(itemRepository.save(createEntityFromDto(item))
                .orElseThrow(() -> new ServiceException("Failed to create item")));
    }

    @Override
    public Item update(Item item) {
        return createDtoFromEntity(itemRepository.save(createEntityFromDto(item))
                .orElseThrow(() -> new ServiceException("Failed to create item")));
    }

    @Override
    public void saveLoymaxItem(Long checkItemId, String itemId) {
        itemRepository.saveLoymaxItem(checkItemId, itemId);
    }

    @Override
    public List<Item> getListByCheckId(Long checkId) {
        List<ru.sparural.engine.entity.Item> entityList = itemRepository
                .getListByCheckId(checkId);
        if (!entityList.isEmpty()) {
            return createDtoList(entityList);
        }
        return null;
    }

    @Override
    public List<Item> createDtoList(List<ru.sparural.engine.entity.Item> entityList) {
        return dtoMapperUtils.convertList(Item.class, entityList);
    }

    @Override
    public List<ru.sparural.engine.entity.Item> batchSave(List<ru.sparural.engine.entity.Item> list) {
        return itemRepository.batchSave(list);
    }

    @Override
    public List<LoymaxChecksItem> batchLoymaxSave(List<LoymaxChecksItem> list) {
        return itemRepository.batchLoymaxSave(list);
    }

    @Override
    public Item createDtoFromEntity(ru.sparural.engine.entity.Item entity) {
        return dtoMapperUtils.convert(entity, Item.class);
    }

    @Override
    public ru.sparural.engine.entity.Item createEntityFromDto(Item dto) {
        return dtoMapperUtils.convert(dto, ru.sparural.engine.entity.Item.class);
    }

    @Override
    public Item getFromLoymaxTable(String itemId) {
        LoymaxChecksItem loymaxChecksItem = itemRepository.getFromLoymaxItem(itemId)
                .orElse(null);
        if (loymaxChecksItem != null) {
            return createDtoFromEntity(itemRepository.get(loymaxChecksItem.getCheckItemId())
                    .orElse(null));
        }
        return null;
    }
}
