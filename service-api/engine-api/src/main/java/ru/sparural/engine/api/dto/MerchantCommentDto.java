package ru.sparural.engine.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.merchant.MerchantsForCommentsDto;
import ru.sparural.engine.api.dto.user.UserForCommentsDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MerchantCommentDto {
    Long id;
    @JsonProperty
    UserForCommentsDto user;
    MerchantsForCommentsDto merchant;
    Integer grade;
    String comment;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<AnswerDTO> options;
    Long createdAt;
    Long updatedAt;
}
