package ru.sparural.engine.repositories.impl;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sparural.engine.repositories.CrudRepository;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
public abstract class CrudRepositoryImpl<L, R extends Record, T extends TableImpl<R>>
        implements CrudRepository<L, R> {
    protected DSLContext dsl;
    protected T table;
    protected String idFieldName;

    @Autowired
    public void setDslContext(DSLContext dslContext) {
        this.dsl = dslContext;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public L insert(R t) {
        var insertStep = dsl.insertInto(table).set(t).returningResult();
        var res = insertStep.fetchOne();
        if (res != null) {
            L val = (L) res.getValue(idFieldName);
            t.set(DSL.field(idFieldName), val);
            return val;
        }
        return null;
    }

    public L saveOrUpdate(R t) {
        var insertStep = dsl.insertInto(table).set(t)
                .onDuplicateKeyUpdate()
                .set(t)
                .returningResult();
        var res = insertStep.fetchOne();
        if (res != null) {
            L val = (L) res.getValue(idFieldName);
            t.set(DSL.field(idFieldName), val);
            return val;
        }
        return null;
    }

    @Override
    public Integer update(R t) {
        return dsl.update(table).set(t).execute();
    }

    @Override
    public Optional<R> get(Long id) {
        return Optional.ofNullable(dsl.selectFrom(table).where(table.field(idFieldName)
                .like(String.valueOf(id))).fetchOne());
    }

    @Override
    public Integer delete(Long id) {
        return dsl.delete(table).where(table.field(idFieldName)
                .like(String.valueOf(id))).execute();
    }

    public R createRecord() {
        return dsl.newRecord(table);
    }

    public SelectJoinStep<Record> basicSelect() {
        return dsl.select(table.fields()).from(table);
    }

}