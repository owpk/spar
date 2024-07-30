package ru.sparural.engine.repositories.impl;


import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.Content;
import ru.sparural.engine.repositories.ContentsRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.Contents;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContentsRepositoryImpl implements ContentsRepository {

    private final DSLContext dslContext;

    @Override
    public Optional<Content> create(Content data) {
        return dslContext.insertInto(Contents.CONTENTS)
                .set(Contents.CONTENTS.ALIAS, data.getAlias())
                .set(Contents.CONTENTS.CONTENT, data.getContent())
                .set(Contents.CONTENTS.TITLE, data.getTitle())
                .set(Contents.CONTENTS.CREATEDAT, TimeHelper.currentTime())
                .onConflict(Contents.CONTENTS.ALIAS)
                .doNothing()
                .returning()
                .fetchOptionalInto(Content.class);
    }

    @Override
    public boolean deleteByAlias(String alias) {
        return dslContext.delete(Contents.CONTENTS).where(Contents.CONTENTS.ALIAS.eq(alias)).execute() == 1;
    }

    @Override
    public Content updateByAlias(String alias, Content data) {
        var update = dslContext.update(Contents.CONTENTS)
                .set(Contents.CONTENTS.ALIAS, data.getAlias())
                .set(Contents.CONTENTS.CONTENT, data.getContent())
                .set(Contents.CONTENTS.TITLE, data.getTitle())
                .set(Contents.CONTENTS.UPDATEDAT, TimeHelper.currentTime())
                .where(Contents.CONTENTS.ALIAS.eq(alias))
                .returning()
                .fetchOne();
        if (update == null) {
            return null;
        }

        return update.into(Content.class);
    }

    @Override
    public Content getByAlias(String alias) {
        var result = dslContext.selectFrom(Contents.CONTENTS).where(Contents.CONTENTS.ALIAS.eq(alias)).fetchOne();
        if (result == null)
            return null;
        return result.into(Content.class);
    }

    @Override
    public List<Content> getList(int offset, int limit) {
        return dslContext.selectFrom(Contents.CONTENTS)
                .orderBy(Contents.CONTENTS.ID.desc())
                .offset(offset).limit(limit).fetch().into(Content.class);
    }

}
