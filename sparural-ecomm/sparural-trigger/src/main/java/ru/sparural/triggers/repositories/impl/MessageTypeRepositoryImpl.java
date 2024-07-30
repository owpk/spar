package ru.sparural.triggers.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.tables.MessagesTypes;
import ru.sparural.triggers.entities.MessageType;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageTypeRepositoryImpl implements ru.sparural.triggers.repositories.MessageTypeRepository {
    private final DSLContext dslContext;
    private final MessagesTypes table = MessagesTypes.MESSAGES_TYPES;

    @Override
    public Optional<MessageType> get(Long id) {
        return dslContext.selectFrom(table)
                .where(table.ID.eq(id))
                .fetchOptionalInto(MessageType.class);
    }

    @Override
    public Optional<MessageType> findByName(String messageType) {
        return dslContext.selectFrom(table)
                .where(table.NAME.eq(messageType))
                .fetchOptionalInto(MessageType.class);
    }
}
