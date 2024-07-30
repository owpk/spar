package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.MessageType;
import ru.sparural.engine.repositories.MessageTypeRepository;
import ru.sparural.tables.MessagesTypes;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class MessageTypeRepositoryImpl implements MessageTypeRepository {
    private final DSLContext dslContext;

    @Override
    public List<MessageType> findAll() {
        return dslContext.selectFrom(MessagesTypes.MESSAGES_TYPES).fetchInto(MessageType.class);
    }
}
