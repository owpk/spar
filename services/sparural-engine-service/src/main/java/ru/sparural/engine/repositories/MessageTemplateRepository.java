package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.MessageTemplate;

import java.util.List;

public interface MessageTemplateRepository {
    List<MessageTemplate> list(Integer offset, Integer limit, String messageType);

}
