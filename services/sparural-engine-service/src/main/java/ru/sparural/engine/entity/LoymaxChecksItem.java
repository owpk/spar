package ru.sparural.engine.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LoymaxChecksItem {
    private Long id;
    private Long checkItemId;
    private String itemId;
}
