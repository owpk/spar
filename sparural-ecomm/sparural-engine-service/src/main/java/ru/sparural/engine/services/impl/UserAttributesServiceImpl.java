package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.engine.entity.UserAttributesEntity;
import ru.sparural.engine.entity.UserIdLoymaxIdEntry;
import ru.sparural.engine.loymax.rest.dto.attribute.LoymaxUserAttributeInfoDto;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.repositories.UserAttributesRepository;
import ru.sparural.engine.services.UserAttributesService;
import ru.sparural.engine.services.UserService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAttributesServiceImpl implements UserAttributesService {
    private final UserAttributesRepository userAttributesRepository;
    private final UserService userService;
    private final LoymaxService loymaxService;

    @Override
    public List<UserAttributesEntity> index(Integer offset, Integer limit) {
        return userAttributesRepository.list(offset, limit);
    }

    @Override
    public UserAttributesEntity get(Long id) {
        return userAttributesRepository.fetchById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.valueOf(id)));
    }

    @Override
    public UserAttributesEntity update(Long id, UserAttributesEntity data) {
        return userAttributesRepository.update(id, data)
                .orElseThrow(() -> new RuntimeException("Cannot update user attribute with id: " + id));
    }

    @Override
    public Boolean delete(Long id) {
        return userAttributesRepository.delete(id);
    }

    @Override
    public UserAttributesEntity create(UserAttributesEntity data) {
        return userAttributesRepository.create(data)
                .orElseThrow(() -> new RuntimeException("Cannot create user attribute"));
    }

    @Override
    @Transactional
    public void importUserAttribute(List<Long> loymaxUserIds) {
        Map<String, UserAttributesEntity> attributeNameEntity = loadAndMapAttributesFromDatabase();

        Map<Long, Set<String>> loymaxIdAttributeNames = new HashMap<>();

        List<UserAttributesEntity> savedAttributes = loadAndBatchSaveAttributesFromLoymax(
                loymaxUserIds, loymaxIdAttributeNames, attributeNameEntity);

        attributeNameEntity.putAll(savedAttributes.stream()
                .collect(Collectors.toMap(UserAttributesEntity::getAttributeName, Function.identity())));

        // забиндили user id к loymax user id
        Map<Long, Long> loymaxIdsUserIds = userService.findUserIdsByLoymaxUserIds(loymaxUserIds)
                .stream().collect(Collectors.toMap(UserIdLoymaxIdEntry::getLoymaxUserId, UserIdLoymaxIdEntry::getUserId));

        // находим user id по loymax user id, выбираем userAttributes entity
        // по loymax имени и биндим к user Id
        Map<Long, Set<Long>> actualAttributes = loymaxIdAttributeNames.entrySet()
                .stream().collect(Collectors.toMap(e -> loymaxIdsUserIds.get(e.getKey()),
                        e -> e.getValue().stream()
                                .map(loymaxAttrName -> attributeNameEntity.get(loymaxAttrName).getId())
                                .collect(Collectors.toSet())));

        batchBindAttributesToUsers(actualAttributes);
    }

    private Map<String, UserAttributesEntity> loadAndMapAttributesFromDatabase() {
        // выбрали атрибуты из бд
        var userAttributesList = index(null, null);
        // забиндили имя атрибута к соотв. сущности из бд
        return userAttributesList.stream()
                .collect(Collectors.toMap(UserAttributesEntity::getAttributeName, Function.identity()));
    }

    /**
     * For each user import attributes from loymax,
     * batch save loymax attributes to database
     * fill {@param loymaxIdAttributeNames} with actual user attributes bindings from loymax
     *
     * @param loymaxUserIds - list of loymax user ids
     * @param loymaxIdAttributeNames - map with
     *                               key: loymax user id
     *                               value: list of loymax attributes logical names
     * @param attributeNameEntity - existing attributes entities
     * @return saved non-existent attributes
     */
    private List<UserAttributesEntity> loadAndBatchSaveAttributesFromLoymax(
            List<Long> loymaxUserIds,
            Map<Long, Set<String>> loymaxIdAttributeNames,
            Map<String, UserAttributesEntity> attributeNameEntity) {

        var entitiesToSave = new ArrayList<UserAttributesEntity>();
        var adminToken = loymaxService.exchangeForTokenAdmin();

        loymaxUserIds.forEach(id -> {
            var result = loymaxService.importUserAttribute(adminToken.getAccessToken(), id, null);

            // заполняем мап данными: для лоймакс юзер id добавляем логические имена атрибутов из лоймакса,
            // для того чтобы в дальнейшем сохранить связи в нашей бд
            loymaxIdAttributeNames.put(id, result.stream()
                    .map(attribute -> attribute.getAttribute().getLogicalName())
                    .collect(Collectors.toSet()));

            // для каждого юзера импортируем список атрибутов из лоймакс,
            // сравниваем атрибут из лоймакса с существующим атрибутом из нашей бд
            // если атрибута нет в нашей бд добавляем атрибут из лоймакса в список для сохранения
            // если атрибут из лоймакса не равен атрибуту из бд добавляем атрибут из лоймакса в список для апдейта
            result.forEach(loymaxAttribute -> {
                var mappedFromLoymax = mapLoymaxAttrToEntity(loymaxAttribute.getAttribute(),
                        loymaxAttribute.getValue());
                var entity = attributeNameEntity.get(loymaxAttribute.getAttribute().getLogicalName());
                if (entity == null | !mappedFromLoymax.equals(entity))
                    entitiesToSave.add(mappedFromLoymax);
            });
        });

        return batchSaveUserAttributes(entitiesToSave);
    }

    private void batchBindAttributesToUsers(Map<Long, Set<Long>> actualAttributes) {
        var existingAttributes = userAttributesRepository.fetchAllByUserIds(actualAttributes.keySet());
        // вычисляем сущности которые нужно удалить относительно того что пришло из лоймакс
        Map<Long, Set<Long>> recordsToDelete = new HashMap<>();
        // вычисляем сущности которые нужно сохранить относительно того что пришло из лоймакс
        Map<Long, Set<Long>> recordsToBind = new HashMap<>();

        actualAttributes.forEach((userId, attributesIds) -> {
            Set<Long> existingUserAttributes = Optional.ofNullable(existingAttributes.get(userId))
                    .orElse(Collections.emptySet());
            Set<Long> attributesToSave = new HashSet<>();
            Set<Long> attributesToDelete = new HashSet<>();
            if (Objects.nonNull(attributesIds)) {
                attributesIds.forEach(loymaxAttribute -> {
                    if (!existingUserAttributes.contains(loymaxAttribute))
                        attributesToSave.add(loymaxAttribute);
                });
                existingUserAttributes.forEach(existingAttribute -> {
                    if (!attributesIds.contains(existingAttribute))
                        attributesToDelete.add(existingAttribute);
                });
            }
            recordsToBind.put(userId, attributesToSave);
            recordsToDelete.put(userId, attributesToDelete);
        });

        userAttributesRepository.deleteAllByUserIdAttributeId(recordsToDelete);
        userAttributesRepository.batchBind(recordsToBind);
    }

    private List<UserAttributesEntity> batchSaveUserAttributes(List<UserAttributesEntity> entitiesToSave) {
        return userAttributesRepository.batchSaveUserAttributes(entitiesToSave);
    }

    // TODO value аттрибута оставили на будущее
    private UserAttributesEntity mapLoymaxAttrToEntity(LoymaxUserAttributeInfoDto attribute, String value) {
        var entity = new UserAttributesEntity();
        entity.setAttributeName(attribute.getLogicalName());
        entity.setName(attribute.getName());
        return entity;
    }
}