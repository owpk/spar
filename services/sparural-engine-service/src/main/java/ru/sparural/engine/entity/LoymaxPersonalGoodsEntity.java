package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Data
@ToString
public class LoymaxPersonalGoodsEntity {
    private List<Long> loymaxGoodId;
    private List<Long> personalGoodId;
}
