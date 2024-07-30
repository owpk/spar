package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.AccountsLifeTimesByTimeDTO;
import ru.sparural.engine.services.AccountsLifeTimesByTimeService;
import ru.sparural.engine.utils.DtoMapperUtils;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;

@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@RequiredArgsConstructor
public class AccountsListLifeTimesByTimeController {
    private final AccountsLifeTimesByTimeService accountsLifeTimesByTimeService;
    private final DtoMapperUtils dtoMapperUtils;

    @KafkaSparuralMapping("accounts/account-life-time-by-time")
    public List<AccountsLifeTimesByTimeDTO> get(@RequestParam Long userId, @RequestParam Long id, @RequestParam Integer offset,
                                                @RequestParam Integer limit) {
        return dtoMapperUtils.convertList(AccountsLifeTimesByTimeDTO.class, () ->
                accountsLifeTimesByTimeService.list(offset, limit, id, userId));
    }

}
