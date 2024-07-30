package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.MainBlock;
import ru.sparural.engine.repositories.MainBlockRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.MainBlocks;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MainBlockRepositoryImpl implements MainBlockRepository {

    private final DSLContext dslContext;

    @Override
    public List<MainBlock> getList(int offset, int limit) {
        return dslContext
                .selectFrom(MainBlocks.MAIN_BLOCKS)
                .offset(offset).limit(limit)
                .fetch().into(MainBlock.class);
    }

    @Override
    public Optional<MainBlock> updateByCode(String code, MainBlock mainBlock) {
        return dslContext.update(MainBlocks.MAIN_BLOCKS)
                .set(MainBlocks.MAIN_BLOCKS.NAME, mainBlock.getName())
                .set(MainBlocks.MAIN_BLOCKS.ORDER, mainBlock.getOrder())
                .set(MainBlocks.MAIN_BLOCKS.SHOW_COUNTER, mainBlock.isShowCounter())
                .set(MainBlocks.MAIN_BLOCKS.SHOW_END_DATE, mainBlock.isShowEndDate())
                .set(MainBlocks.MAIN_BLOCKS.SHOW_PERCENTS, mainBlock.isShowPercents())
                .set(MainBlocks.MAIN_BLOCKS.SHOW_BILLET, mainBlock.isShowBillet())
                .set(MainBlocks.MAIN_BLOCKS.UPDATED_AT, TimeHelper.currentTime())
                .where(MainBlocks.MAIN_BLOCKS.CODE.eq(code))
                .returning().fetchOptionalInto(MainBlock.class);
    }

}
