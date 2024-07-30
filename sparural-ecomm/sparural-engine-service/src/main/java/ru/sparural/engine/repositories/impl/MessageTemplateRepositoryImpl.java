package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.MessageTemplate;
import ru.sparural.engine.entity.MessageType;
import ru.sparural.engine.repositories.MessageTemplateRepository;
import ru.sparural.tables.MessagesTemplates;
import ru.sparural.tables.MessagesTypes;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageTemplateRepositoryImpl implements MessageTemplateRepository {
    private final DSLContext dslContext;
    private final MessagesTemplates table = MessagesTemplates.MESSAGES_TEMPLATES;

    @Override
    public List<MessageTemplate> list(Integer offset, Integer limit, String messageType) {
        var type = dslContext
                .select(MessagesTypes.MESSAGES_TYPES.ID)
                .from(MessagesTypes.MESSAGES_TYPES)
                .where(MessagesTypes.MESSAGES_TYPES.NAME.eq(messageType))
                .fetchOptionalInto(MessageType.class);

        return type.map(value -> dslContext.selectFrom(table)
                .where(table.MESSAGE_TYPE_ID.eq(value.getId()))
                .orderBy(table.CREATED_AT)
                .limit(limit)
                .offset(offset)
                .fetch()
                .into(MessageTemplate.class)).orElseGet(ArrayList::new);
    }

}
