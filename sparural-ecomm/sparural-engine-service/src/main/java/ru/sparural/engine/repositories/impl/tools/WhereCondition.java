package ru.sparural.engine.repositories.impl.tools;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WhereCondition {
    private String field;
    private Object value;
    private SearchOperators condition;
    private String full;

    public WhereCondition(String field, Object value) {
        this(field, value, SearchOperators.EQUAL, null);
    }

    public WhereCondition(String field, Object value, SearchOperators condition) {
        this(field, value, condition, null);
    }

    public WhereCondition(String field, Object value, SearchOperators condition, String full) {
        this.field = field;
        this.value = value;
        this.condition = condition;
        this.full = full;
    }
}
