package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class MerchantComment {
    List<Answer> options;
    private Long id;
    private User user;
    private Merchant merchant;
    private Integer grade;
    private String comment;
    private Long createdAt;
    private Long updatedAt;
}
