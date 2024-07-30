package ru.sparural.engine.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.*;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MerchantCommentsDto {
    @NotNull(message = "Пожалуйста, выберите магазин")
    Long merchantId;
    @NotNull(message = "Пожалуйста, поставьте оценку")
    @Min(value = 0, message = "Оценка должна быть больше 0")
    @Max(value = 5, message = "Оценка не должна быть больше 5")
    Integer grade;
    @Size(max = 255, message = "Максимальное количество символов в комментарии - 255")
//    @NotBlank(message = "Комментарий пустой")
//    @NotNull(message = "Пожалуйста, оставьте комментарий")
    String comment;
    List<QuestionDto> questions;
}
