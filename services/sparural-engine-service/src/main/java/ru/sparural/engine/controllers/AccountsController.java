package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.sparural.engine.api.dto.AccountsLifeTimesByPeriodDto;
import ru.sparural.engine.api.dto.AccountsLifeTimesByTimeDTO;
import ru.sparural.engine.api.dto.Currency;
import ru.sparural.engine.api.dto.NameCases;
import ru.sparural.engine.api.dto.account.AccountsDto;
import ru.sparural.engine.api.dto.account.AccountsTypeDto;
import ru.sparural.engine.api.dto.user.UserFilterDto;
import ru.sparural.engine.entity.Account;
import ru.sparural.engine.loymax.rest.dto.account.LoymaxLifeTimeByPeriod;
import ru.sparural.engine.loymax.rest.dto.account.LoymaxLifeTimesByTime;
import ru.sparural.engine.loymax.rest.dto.account.LoymaxUserBalanceInfoDto;
import ru.sparural.engine.loymax.rest.dto.currency.LoymaxCurrency;
import ru.sparural.engine.loymax.rest.dto.currency.LoymaxNameCases;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.loymax.utils.LoymaxTimeToSparTimeAdapter;
import ru.sparural.engine.services.AccountUserService;
import ru.sparural.engine.services.AccountsLifeTimesByPeriodService;
import ru.sparural.engine.services.AccountsLifeTimesByTimeService;
import ru.sparural.engine.services.AccountsService;
import ru.sparural.engine.services.AccountsTypeService;
import ru.sparural.engine.services.CurrencyService;
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
    public List<Long> lifespanTriggerInfo(@RequestParam Long accountTypeId,
                                          @RequestParam Integer burningTime) {
        return accountsService
                .searchAccountsByFilter(accountTypeId, burningTime, false)
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
        double scale = Math.pow(10, 2);
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);

        List<LoymaxUserBalanceInfoDto> loymaxUserBalanceList =
                loymaxService.getDetailedBalance(loymaxUser);
        if (loymaxUserBalanceList == null) {
            return new ArrayList<>();
        }

        List<AccountsDto> accountsList = accountsService.getByUserId(userId);

        if (accountsList != null) {
            for (AccountsDto account : accountsList) {
                accountsLifeTimesByTimeService.deleteByAccountId(account.getId());
                accountsLifeTimesByPeriodService.deleteByAccountId(account.getId());
            }
        }

        accountsList = new ArrayList<>();

        for (LoymaxUserBalanceInfoDto balanceInfo : loymaxUserBalanceList) {
            LoymaxNameCases loymaxNameCases = balanceInfo.getCurrency().getNameCases();
            LoymaxCurrency loymaxCurrency = balanceInfo.getCurrency();
            NameCases nameCases = new NameCases();
            nameCases.setAbbreviation(loymaxNameCases.getAbbreviation());
            nameCases.setGenitive(loymaxNameCases.getGenitive());
            nameCases.setPlural(loymaxNameCases.getPlural());
            nameCases.setNominative(loymaxNameCases.getNominative());

            Currency currency = currencyService.getByExternalId(balanceInfo.getCurrency().getExternalId());

            if (currency == null) {
                currency = new Currency();
                currency.setDescription(loymaxCurrency.getDescription());
                currency.setExternalId(loymaxCurrency.getExternalId());
                currency.setName(loymaxCurrency.getName());
                currency.setIsDeleted(loymaxCurrency.getIsDeleted());
                currency.setNameCases(nameCases);
                currency = currencyService.save(currency);
            } else {
                currency.setDescription(loymaxCurrency.getDescription());
                currency.setExternalId(loymaxCurrency.getExternalId());
                currency.setName(loymaxCurrency.getName());
                currency.setIsDeleted(loymaxCurrency.getIsDeleted());
                currency.setNameCases(nameCases);
                currency = currencyService.updateByExternalId(currency);
            }

            currency.setNameCases(nameCases);

            var accountsTypeDto =
                    accountsTypeService.getByCurrencyId(currency.getId());

            if (accountsTypeDto == null) {
                accountsTypeDto = new AccountsTypeDto();
                accountsTypeDto.setCurrenciesId(currency.getId());
                accountsTypeDto.setName(currency.getName());
                accountsTypeDto.setOrder(0);
                accountsTypeDto = accountsTypeService.save(accountsTypeDto);
                accountsTypeDto.setCurrency(currency);
            } else {
                accountsTypeDto.setCurrenciesId(currency.getId());
                accountsTypeDto.setName(currency.getName());
                accountsTypeDto.setOrder(0);
                accountsTypeDto = accountsTypeService.update(accountsTypeDto);
                accountsTypeDto.setCurrency(currency);
            }

            AccountsDto accountsDto = new AccountsDto();
            accountsDto.setUserId(userId);
            accountsDto.setAmount(Math.ceil(balanceInfo.getAmount() * scale) / scale);
            accountsDto.setAccountTypeIdField(accountsTypeDto.getId());
            accountsDto.setNotActivatedAmount(Math.ceil(balanceInfo.getNotActivatedAmount() * scale) / scale);
            accountsDto = accountsService.save(accountsDto);
            accountsDto.setAccountType(accountsTypeDto);

            List<AccountsLifeTimesByPeriodDto> accountsLifeTimesByPeriodList = new ArrayList<>();
            for (LoymaxLifeTimeByPeriod x : balanceInfo.getLifeTimesByPeriod()) {
                AccountsLifeTimesByPeriodDto timesByPeriodDto = new AccountsLifeTimesByPeriodDto();
                timesByPeriodDto.setPeriod(x.getPeriod());
                timesByPeriodDto.setAccountId(accountsDto.getId());
                timesByPeriodDto.setActivationAmount(x.getActivationAmount());
                timesByPeriodDto.setExpirationAmount(x.getExpirationAmount());
                accountsLifeTimesByPeriodList.add(accountsLifeTimesByPeriodService.save(timesByPeriodDto));
            }

            List<AccountsLifeTimesByTimeDTO> accountsLifeTimesByTimeList = new ArrayList<>();
            for (LoymaxLifeTimesByTime y : balanceInfo.getLifeTimesByTime()) {
                AccountsLifeTimesByTimeDTO timeByTime = new AccountsLifeTimesByTimeDTO();
                timeByTime.setAccountId(accountsDto.getId());
                timeByTime.setAmount(Math.abs(y.getAmount()));
                timeByTime.setDate(LoymaxTimeToSparTimeAdapter.convertToEpochSeconds(y.getDate()));
                accountsLifeTimesByTimeList.add(accountsLifeTimesByTimeService.save(timeByTime));
            }

            accountsDto.setAccountsLifeTimesByTime(accountsLifeTimesByTimeList);
            accountsDto.setAccountsLifeTimesByPeriod(accountsLifeTimesByPeriodList);
            accountsList.add(accountsDto);
        }

        List<AccountsDto> result = new ArrayList<>();
        var ids = accountsList.stream()
                .limit(limit)
                .map(AccountsDto::getId).collect(Collectors.toList());
        accountUserService.batchSaveAsync(userId, ids);
        for (AccountsDto x : accountsList) {
            if (x.getId() > offset && !x.getAccountType()
                    .getCurrency().getIsDeleted()) {
                result.add(x);
            }
            if (result.size() > limit) {
                break;
            }
        }
        return result;
    }

}