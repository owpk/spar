package ru.sparural.triggers.dto;

import lombok.*;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TriggerInfo {
    private String action;
    private int urgency;
}
