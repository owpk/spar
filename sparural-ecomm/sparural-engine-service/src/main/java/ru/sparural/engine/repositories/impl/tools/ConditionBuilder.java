package ru.sparural.engine.repositories.impl.tools;

import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Vorobyev Vyacheslav
 */
public class ConditionBuilder {

    private final List<WhereCondition> filter;
    private SelectConditionStep<Record> selectStep;

    public ConditionBuilder(List<WhereCondition> filter) {
        this.filter = filter;
    }

    public ConditionBuilder(SelectConditionStep<Record> conditionStep) {
        this.selectStep = conditionStep;
        filter = new ArrayList<>();
    }

    public ConditionBuilder addCondition(WhereCondition whereCondition) {
        filter.add(whereCondition);
        return this;
    }

    public ConditionBuilder addCondition(WhereCondition... whereCondition) {
        filter.addAll(Stream.of(whereCondition).collect(Collectors.toList()));
        return this;
    }

    public ConditionBuilder addCondition(String field, Object value) {
        filter.add(new WhereCondition(field, value));
        return this;
    }

    public ConditionBuilder addCondition(String field, Object value, SearchOperators searchOperator) {
        filter.add(new WhereCondition(field, value, searchOperator));
        return this;
    }

    public ConditionBuilder addCondition(String field, Object value, SearchOperators searchOperator, String full) {
        filter.add(new WhereCondition(field, value, searchOperator, full));
        return this;
    }

    public SelectConditionStep<Record> buildCondition() {
        filter.forEach(filterCondition -> {
            if (filterCondition.getValue() != null
                    && !filterCondition.getValue().equals(-1)
                    && !filterCondition.getValue().equals(-1L)
                    && !filterCondition.getValue().equals("")
            ) {
                buildCondition(selectStep, filterCondition);
            }
        });
        return selectStep;
    }

    private void buildCondition(SelectConditionStep<Record> step, WhereCondition where) {
        String field = String.format("\"%s\"", where.getField());
        if (where.getFull() != null) {
            field = String.format("\"%s\".%s", where.getFull(), field);
        }
        field = field.replace("\"", "");
        switch (where.getCondition()) {
            case NOT_EQUAL:
                step.and(
                        DSL.field(field).notEqual(where.getValue())
                );
                break;
            case MIN:
                step.and(
                        DSL.field(field).greaterOrEqual(where.getValue())
                );
                break;
            case MAX:
                step.and(
                        DSL.field(field).lessOrEqual(where.getValue())
                );
                break;
            case LIKE:
                step.or(
                        DSL.field(field).like("%" + where.getValue() + "%")
                );
                break;
            default:
                step.and(DSL.field(field).eq(where.getValue()));
        }
    }
}
