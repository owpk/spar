package ru.sparural.rest.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import ru.sparural.engine.api.dto.AnswerDTO;
import ru.sparural.engine.api.dto.MerchantCommentDto;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Vorobyev Vyacheslav
 */
public class CSVWriter {

    // Дата;Пользователь;Оценка;Причина;Отзыв;Магазин
    //07.07.2022;Другая Любая;5;;Тест;SPAR г. Челябинск, Труда, 166
    public static void writeCSVContent(PrintWriter writer, List<MerchantCommentDto> merchantComments) throws IOException {
        writer.write('\uFEFF');
        try (CSVPrinter printer = new CSVPrinter(writer, CSVFormat.EXCEL.builder()
                .setDelimiter(';')
                .setNullString("")
                .build())) {
            printer.printRecord(format("Дата"), format("Пользователь"), format("Оценка"),
                    format("Причинa"), format("Отзыв"), format("Магазин"));
            for (var dto : merchantComments) {
                printer.printRecord(
                        format(new SimpleDateFormat("dd.MM.yyyy")
                                .format(new Date(TimeUnit.MILLISECONDS.convert(
                                        dto.getCreatedAt(), TimeUnit.SECONDS)))),
                        format(dto.getUser().getFirstName() + " " + dto.getUser().getLastName()),
                        format(dto.getGrade()),
                        format(String.format("\"%s\"", Optional.of(dto.getOptions().stream()
                                        .filter(x -> x != null && x.getAnswer() != null)
                                        .map(AnswerDTO::getAnswer)
                                        .collect(Collectors.joining(" | ")))
                                .orElse(""))),
                        format(String.format("\"%s\"", dto.getComment())),
                        format(String.format("\"%s\"", dto.getMerchant().getTitle() + " " + dto.getMerchant().getAddress())));
            }
            printer.flush();
            writer.flush();
            writer.close();
        }
    }

    private static String format(Object text) {
        return text.toString();
    }
}
