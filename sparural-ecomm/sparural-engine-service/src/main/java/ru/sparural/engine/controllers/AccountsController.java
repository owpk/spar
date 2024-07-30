package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.sparural.engine.api.dto.Currency;
import ru.sparural.engine.api.dto.account.AccountsDto;
import ru.sparural.engine.api.dto.account.AccountsTypeDto;
import ru.sparural.engine.api.dto.user.UserFilterDto;
import ru.sparural.engine.entity.Account;
import ru.sparural.engine.entity.AccountFull;
import ru.sparural.engine.loymax.rest.dto.account.LoymaxUserBalanceInfoDto;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.services.*;
import ru.sparural.engine.utils.mappers.AccountLifeTimeByPeriodMapper;
import ru.sparural.engine.utils.mappers.AccountLifeTimeByTimeMapper;
import ru.sparural.engine.utils.mappers.AccountsMapper;
import ru.sparural.engine.utils.mappers.CurrencyMapper;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@RequiredArgsConstructor
@Slf4j
public class AccountsController {

    private final LoymaxService loymaxService;
    private final CurrencyService currencyService;
    private final AccountsTypeService accountsTypeService;
    private final AccountsService accountsService;
    private final AccountsLifeTimesByTimeService accountsLifeTimesByTimeService;
    private final AccountsLifeTimesByPeriodService accountsLifeTimesByPeriodService;
    private final AccountUserService accountUserService;

    /*
     * returns account user ids by specific filter
     */
    @KafkaSparuralMapping("accounts/lifespan-trigger-info")
    public List<Long> lifespanTriggerInfo(@RequestParam Long currencyId,
                                          @RequestParam Integer burningTime) {
        return accountsService
                .searchAccountsByFilter(currencyId, burningTime, false)
                .stream()
                .map(Account::getUserId)
                .collect(Collectors.toList());
    }

    @KafkaSparuralMapping("accounts/set-trigger-fired")
    public void setTriggerFired(@Payload UserFilterDto userFilterDto) {
        accountsService.setTriggerFired(userFilterDto.getUserIds(), true);
    }

    @KafkaSparuralMapping("accounts/types")
    public List<AccountsTypeDto> accountsTypes(@RequestParam Integer offset,
                                               @RequestParam Integer limit) {
        return accountsService.accountTypesList(offset, limit);
    }

    @KafkaSparuralMapping("accounts/index")
    public List<AccountsDto> index(@RequestParam Long userId,
                                   @RequestParam Integer offset,
                                   @RequestParam Integer limit) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);
        List<LoymaxUserBalanceInfoDto> loymaxAccounts = loymaxService.getDetailedBalance(loymaxUser);
        var fetchedFromLoymax = accountsService.selectFromLoymaxAndSave(userId, loymaxAccounts);
        return fetchedFromLoymax.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    private AccountsDto mapEntityToDto(AccountFull entity) {
        var accDto = new AccountsDto();
        accDto.setId(entity.getId());
        accDto.setAmount(entity.getAmount());

        var currDto = CurrencyMapper.INSTANCE.entityToDto(entity.getCurrency());

        accDto.setCurrency(currDto);
        accDto.setUserId(entity.getUserId());
        accDto.setNotActivatedAmount(entity.getNotActivatedAmount());

        var accLifeTimesPeriod = entity.getAccountLifeTimeByPeriod().values()
                .stream().map(AccountLifeTimeByPeriodMapper.INSTANCE::entityToDto)
                .collect(Collectors.toList());

        accDto.setAccountsLifeTimesByPeriod(accLifeTimesPeriod);

        var accLifeTimesByTime = entity.getAccountLifeTimeByTime().values()
                .stream().map(AccountLifeTimeByTimeMapper.INSTANCE::entityToDto)
                .collect(Collectors.toList());

        accDto.setAccountsLifeTimesByTime(accLifeTimesByTime);
        return accDto;
    }
}