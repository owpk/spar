package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.Screen;
import ru.sparural.engine.repositories.ScreensRepository;
import ru.sparural.tables.Screens;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class ScreensRepositoryImpl implements ScreensRepository {

    private final DSLContext dslContext;
    private Screens table;

    @PostConstruct
    private void init() {
        this.table = Screens.SCREENS;
    }

    @Override
    public List<Screen> fetch(Long offset, Long limit) {
        return dslContext.select()
                .from(table)
                .orderBy(table.ID)
                .offset(offset)
                .limit(limit)
                .fetchInto(Screen.class);
    }

    @Override
    public Optional<Screen> get(Long id) {
        return dslContext.selectFrom(table)
                .where(table.ID.eq(id))
                .fetchOptionalInto(Screen.class);
    }

    @Override
    public Optional<String> findCodeById(Long screenId) {
        return dslContext.select(table.CODE)
                .from(table)
                .where(table.ID.eq(screenId))
                .limit(1)
                .fetchOptionalInto(String.class);
    }

    @Override
    public Optional<Long> findIdByCode(String screen) {
        return dslContext.select(table.ID)
                .from(table)
                .where(table.CODE.eq(screen))
                .limit(1)
                .fetchOptionalInto(Long.class);
    }
}
