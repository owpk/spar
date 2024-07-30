package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.Screen;
import ru.sparural.engine.repositories.PushActionsRepository;
import ru.sparural.tables.Screens;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PushActionsRepositoryImpl implements PushActionsRepository {

    private final DSLContext dslContext;

    @Override
    public List<Screen> list(Integer offset, Integer limit) {
        return dslContext
                .selectFrom(Screens.SCREENS)
                .orderBy(Screens.SCREENS.ID.desc())
                .offset(offset)
                .limit(limit)
                .fetch().into(Screen.class);
    }
}
