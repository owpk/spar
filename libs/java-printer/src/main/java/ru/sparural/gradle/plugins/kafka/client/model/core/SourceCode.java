package ru.sparural.gradle.plugins.kafka.client.model.core;

public class SourceCode {
    public static String code =
            "package ru.sparural.engine.controllers;\n" +
            "\n" +
            "import lombok.RequiredArgsConstructor;\n" +
            "import lombok.extern.slf4j.Slf4j;\n" +
            "import org.springframework.web.bind.annotation.RequestBody;\n" +
            "import ru.sparural.engine.api.dto.AccountsLifeTimesByPeriodDto;\n" +
            "import ru.sparural.engine.api.dto.AccountsLifeTimesByTimeDTO;\n" +
            "import ru.sparural.engine.api.dto.Currency;\n" +
            "import ru.sparural.engine.api.dto.NameCases;\n" +
            "import ru.sparural.engine.api.dto.account.AccountsDto;\n" +
            "import ru.sparural.engine.api.dto.account.AccountsTypeDto;\n" +
            "import ru.sparural.engine.api.dto.user.UserFilterDto;\n" +
            "import ru.sparural.engine.entity.Account;\n" +
            "import ru.sparural.engine.loymax.rest.dto.account.LoymaxLifeTimeByPeriod;\n" +
            "import ru.sparural.engine.loymax.rest.dto.account.LoymaxLifeTimesByTime;\n" +
            "import ru.sparural.engine.loymax.rest.dto.account.LoymaxUserBalanceInfoDto;\n" +
            "import ru.sparural.engine.loymax.rest.dto.currency.LoymaxCurrency;\n" +
            "import ru.sparural.engine.loymax.rest.dto.currency.LoymaxNameCases;\n" +
            "import ru.sparural.engine.loymax.services.LoymaxService;\n" +
            "import ru.sparural.engine.loymax.utils.LoymaxTimeToSparTimeAdapter;\n" +
            "import ru.sparural.engine.services.*;\n" +
            "import ru.sparural.kafka.annotation.KafkaSparuralController;\n" +
            "import ru.sparural.kafka.annotation.KafkaSparuralMapping;\n" +
            "import ru.sparural.kafka.annotation.RequestParam;\n" +
            "\n" +
            "import java.util.ArrayList;\n" +
            "import java.util.List;\n" +
            "import java.util.stream.Collectors;\n" +
            "\n" +
            "@KafkaSparuralController(topic = \"${sparural.kafka.request-topics.engine}\")\n" +
            "@RequiredArgsConstructor\n" +
            "@Slf4j\n" +
            "public class AccountsController {\n" +
            "\n" +
            "    private final LoymaxService loymaxService;\n" +
            "    private final CurrencyService currencyService;\n" +
            "    private final AccountsTypeService accountsTypeService;\n" +
            "    private final AccountsService accountsService;\n" +
            "    private final AccountsLifeTimesByTimeService accountsLifeTimesByTimeService;\n" +
            "    private final AccountsLifeTimesByPeriodService accountsLifeTimesByPeriodService;\n" +
            "    private final AccountUserService accountUserService;\n" +
            "\n" +
            "    /*\n" +
            "     * returns account user ids by specific filter\n" +
            "     */\n" +
            "    @KafkaSparuralMapping(\"accounts/lifespan-trigger-info\")\n" +
            "    public List<Long> lifespanTriggerInfo() throws BlaBla {\n" +
            "        return accountsService\n" +
            "                .searchAccountsByFilter(accountTypeId, burningTime, false)\n" +
            "                .stream()\n" +
            "                .map(Account::getUserId)\n" +
            "                .collect(Collectors.toList());\n" +
            "    }\n" +
            "\n" +
            "    @KafkaSparuralMapping(\"accounts/set-trigger-fired\")\n" +
            "    public void setTriggerFired(@RequestBody UserFilterDto userFilterDto) {\n" +
            "        accountsService.setTriggerFired(userFilterDto.getUserIds(), true);\n" +
            "    }\n" +
            "\n" +
            "    @KafkaSparuralMapping(\"accounts/types\")\n" +
            "    public List<AccountsTypeDto> accountsTypes(@RequestParam Integer offset,\n" +
            "                                               @RequestParam Integer limit) {\n" +
            "        return accountsService.accountTypesList(offset, limit);\n" +
            "    }\n" +
            "\n" +
            "    @KafkaSparuralMapping(\"accounts/index\")\n" +
            "    public List<AccountsDto> index(@RequestParam Long userId,\n" +
            "                                   @RequestParam Integer offset,\n" +
            "                                   @RequestParam Integer limit) {\n" +
            "        double scale = Math.pow(10, 2);\n" +
            "        var loymaxUser = loymaxService.getByLocalUserId(userId);\n" +
            "        loymaxService.refreshTokenIfNeeded(loymaxUser);\n" +
            "\n" +
            "        List<LoymaxUserBalanceInfoDto> loymaxUserBalanceList =\n" +
            "                loymaxService.getDetailedBalance(loymaxUser);\n" +
            "        if (loymaxUserBalanceList == null) {\n" +
            "            return new ArrayList<>();\n" +
            "        }\n" +
            "\n" +
            "        List<AccountsDto> accountsList = accountsService.getByUserId(userId);\n" +
            "\n" +
            "        if (accountsList != null) {\n" +
            "            for (AccountsDto account : accountsList) {\n" +
            "                accountsLifeTimesByTimeService.deleteByAccountId(account.getId());\n" +
            "                accountsLifeTimesByPeriodService.deleteByAccountId(account.getId());\n" +
            "            }\n" +
            "        }\n" +
            "\n" +
            "        accountsList = new ArrayList<>();\n" +
            "\n" +
            "        for (LoymaxUserBalanceInfoDto balanceInfo : loymaxUserBalanceList) {\n" +
            "            LoymaxNameCases loymaxNameCases = balanceInfo.getCurrency().getNameCases();\n" +
            "            LoymaxCurrency loymaxCurrency = balanceInfo.getCurrency();\n" +
            "            NameCases nameCases = new NameCases();\n" +
            "            nameCases.setAbbreviation(loymaxNameCases.getAbbreviation());\n" +
            "            nameCases.setGenitive(loymaxNameCases.getGenitive());\n" +
            "            nameCases.setPlural(loymaxNameCases.getPlural());\n" +
            "            nameCases.setNominative(loymaxNameCases.getNominative());\n" +
            "\n" +
            "            Currency currency = currencyService.getByExternalId(balanceInfo.getCurrency().getExternalId());\n" +
            "\n" +
            "            if (currency == null) {\n" +
            "                currency = new Currency();\n" +
            "                currency.setDescription(loymaxCurrency.getDescription());\n" +
            "                currency.setExternalId(loymaxCurrency.getExternalId());\n" +
            "                currency.setName(loymaxCurrency.getName());\n" +
            "                currency.setIsDeleted(loymaxCurrency.getIsDeleted());\n" +
            "                currency.setNameCases(nameCases);\n" +
            "                currency = currencyService.save(currency);\n" +
            "            } else {\n" +
            "                currency.setDescription(loymaxCurrency.getDescription());\n" +
            "                currency.setExternalId(loymaxCurrency.getExternalId());\n" +
            "                currency.setName(loymaxCurrency.getName());\n" +
            "                currency.setIsDeleted(loymaxCurrency.getIsDeleted());\n" +
            "                currency.setNameCases(nameCases);\n" +
            "                currency = currencyService.updateByExternalId(currency);\n" +
            "            }\n" +
            "\n" +
            "            currency.setNameCases(nameCases);\n" +
            "\n" +
            "            var accountsTypeDto =\n" +
            "                    accountsTypeService.getByCurrencyId(currency.getId());\n" +
            "\n" +
            "            if (accountsTypeDto == null) {\n" +
            "                accountsTypeDto = new AccountsTypeDto();\n" +
            "                accountsTypeDto.setCurrenciesId(currency.getId());\n" +
            "                accountsTypeDto.setName(currency.getName());\n" +
            "                accountsTypeDto.setOrder(0);\n" +
            "                accountsTypeDto = accountsTypeService.save(accountsTypeDto);\n" +
            "                accountsTypeDto.setCurrency(currency);\n" +
            "            } else {\n" +
            "                accountsTypeDto.setCurrenciesId(currency.getId());\n" +
            "                accountsTypeDto.setName(currency.getName());\n" +
            "                accountsTypeDto.setOrder(0);\n" +
            "                accountsTypeDto = accountsTypeService.update(accountsTypeDto);\n" +
            "                accountsTypeDto.setCurrency(currency);\n" +
            "            }\n" +
            "\n" +
            "            AccountsDto accountsDto = new AccountsDto();\n" +
            "            accountsDto.setUserId(userId);\n" +
            "            accountsDto.setAmount(Math.ceil(balanceInfo.getAmount() * scale) / scale);\n" +
            "            accountsDto.setAccountTypeIdField(accountsTypeDto.getId());\n" +
            "            accountsDto.setNotActivatedAmount(Math.ceil(balanceInfo.getNotActivatedAmount() * scale) / scale);\n" +
            "            accountsDto = accountsService.save(accountsDto);\n" +
            "            accountsDto.setAccountType(accountsTypeDto);\n" +
            "\n" +
            "            List<AccountsLifeTimesByPeriodDto> accountsLifeTimesByPeriodList = new ArrayList<>();\n" +
            "            for (LoymaxLifeTimeByPeriod x : balanceInfo.getLifeTimesByPeriod()) {\n" +
            "                AccountsLifeTimesByPeriodDto timesByPeriodDto = new AccountsLifeTimesByPeriodDto();\n" +
            "                timesByPeriodDto.setPeriod(x.getPeriod());\n" +
            "                timesByPeriodDto.setAccountId(accountsDto.getId());\n" +
            "                timesByPeriodDto.setActivationAmount(x.getActivationAmount());\n" +
            "                timesByPeriodDto.setExpirationAmount(x.getExpirationAmount());\n" +
            "                accountsLifeTimesByPeriodList.add(accountsLifeTimesByPeriodService.save(timesByPeriodDto));\n" +
            "            }\n" +
            "\n" +
            "            List<AccountsLifeTimesByTimeDTO> accountsLifeTimesByTimeList = new ArrayList<>();\n" +
            "            for (LoymaxLifeTimesByTime y : balanceInfo.getLifeTimesByTime()) {\n" +
            "                AccountsLifeTimesByTimeDTO timeByTime = new AccountsLifeTimesByTimeDTO();\n" +
            "                timeByTime.setAccountId(accountsDto.getId());\n" +
            "                timeByTime.setAmount(Math.abs(y.getAmount()));\n" +
            "                timeByTime.setDate(LoymaxTimeToSparTimeAdapter.convertToEpochSeconds(y.getDate()));\n" +
            "                accountsLifeTimesByTimeList.add(accountsLifeTimesByTimeService.save(timeByTime));\n" +
            "            }\n" +
            "\n" +
            "            accountsDto.setAccountsLifeTimesByTime(accountsLifeTimesByTimeList);\n" +
            "            accountsDto.setAccountsLifeTimesByPeriod(accountsLifeTimesByPeriodList);\n" +
            "            accountsList.add(accountsDto);\n" +
            "        }\n" +
            "\n" +
            "        List<AccountsDto> result = new ArrayList<>();\n" +
            "        var ids = accountsList.stream()\n" +
            "                .limit(limit)\n" +
            "                .map(AccountsDto::getId).collect(Collectors.toList());\n" +
            "        accountUserService.batchSaveAsync(userId, ids);\n" +
            "        for (AccountsDto x : accountsList) {\n" +
            "            if (x.getId() > offset && !x.getAccountType()\n" +
            "                    .getCurrency().getIsDeleted()) {\n" +
            "                result.add(x);\n" +
            "            }\n" +
            "            if (result.size() > limit) {\n" +
            "                break;\n" +
            "            }\n" +
            "        }\n" +
            "        return result;\n" +
            "    }\n" +
            "\n" +
            "}";
}
