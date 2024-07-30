package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.engine.api.dto.account.AccountsDto;
import ru.sparural.engine.api.dto.account.AccountsTypeDto;
import ru.sparural.engine.entity.*;
import ru.sparural.engine.loymax.rest.dto.account.LoymaxUserBalanceInfoDto;
import ru.sparural.engine.repositories.AccountsRepository;
import ru.sparural.engine.services.AccountsLifeTimesByPeriodService;
import ru.sparural.engine.services.AccountsLifeTimesByTimeService;
import ru.sparural.engine.services.AccountsService;
import ru.sparural.engine.services.CurrencyService;
import ru.sparural.engine.services.exception.ServiceException;
import ru.sparural.engine.utils.DtoMapperUtils;
import ru.sparural.engine.utils.mappers.AccountLifeTimeByPeriodMapper;
import ru.sparural.engine.utils.mappers.AccountLifeTimeByTimeMapper;
import ru.sparural.engine.utils.mappers.AccountsMapper;
import ru.sparural.engine.utils.mappers.CurrencyMapper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountsServiceImpl implements AccountsService {

    private final DtoMapperUtils dtoMapperUtils;
    private final AccountsRepository accountsRepository;
    private final CurrencyService currencyService;
    private final AccountsLifeTimesByPeriodService accountsLifeTimesByPeriodService;
    private final AccountsLifeTimesByTimeService accountsLifeTimesByTimeService;

    @Override
    public List<AccountsDto> getByUserId(Long userId) {
        List<Account> list = accountsRepository.getListByUserId(userId);
        if (!list.isEmpty()) {
            return createDTOListFromEntity(list);
        }
        return null;
    }

    @Override
    public AccountsDto save(AccountsDto dto) {
        return createDTOFromEntity(accountsRepository.save(createEntityFromDTO(dto))
                .orElseThrow(() -> new ServiceException("Failed to create account")));
    }

    @Override
    public Account createEntityFromDTO(AccountsDto dto) {
        return dtoMapperUtils.convert(dto, Account.class);
    }

    @Override
    public AccountsDto createDTOFromEntity(Account entity) {
        return dtoMapperUtils.convert(entity, AccountsDto.class);
    }

    @Override
    public AccountsDto createDTOFromEntity(AccountFull entity) {
        var dto = new AccountsDto();
        dto.setAmount(entity.getAmount());
        dto.setUserId(entity.getUserId());
        return dto;
    }

    @Override
    public List<AccountsDto> createDTOListFromEntity(List<Account> list) {
        return dtoMapperUtils.convertList(AccountsDto.class, list);
    }

    @Override
    public List<Account> searchAccountsByFilter(Long currencyId, Integer burningTime, boolean triggerFired) {
        return accountsRepository.searchByFilter(currencyId, burningTime, triggerFired);
    }

    @Override
    public void setTriggerFired(List<Long> userIds, boolean triggerFiredState) {
        accountsRepository.batchUpdateTriggerFired(userIds, triggerFiredState);
    }

    @Override
    public List<AccountsTypeDto> accountTypesList(Integer offset, Integer limit) {
        return accountsRepository.fetchAccountTypes(offset, limit);
    }

    // LTBT - lifetime by time
    // LTBP - lifetime by period
    @Override
    @Transactional
    public List<AccountFull> selectFromLoymaxAndSave(Long userId, List<LoymaxUserBalanceInfoDto> loymaxAccounts) {

        Map<String, LoymaxUserBalanceInfoDto> curExtIdBalanceInfo = loymaxAccounts.stream()
                .collect(Collectors.toMap(balance -> balance.getCurrency().getUid(), Function.identity()));

        Map<String, AccountFull> existingAccounts = fetchByUserIdAndExtCurrencyId(
                userId, curExtIdBalanceInfo.keySet()).stream()
                .collect(Collectors.toMap(acc -> acc.getCurrency().getExternalId(), Function.identity()));

        updateAccountsIfNeeded(existingAccounts, curExtIdBalanceInfo);

        Set<String> accountsToSaveIds = new HashSet<>(curExtIdBalanceInfo.keySet());
        accountsToSaveIds.removeAll(existingAccounts.keySet());

        Map<String, CurrencyEntity> mergedCurrenciesFromLoymaxAndDb = updateCurrencies(curExtIdBalanceInfo);

        // save 'new' accounts which not exist in database
        Map<Long, Account> currIdSavedAccList = saveAccountsFromLoymax(userId, mergedCurrenciesFromLoymaxAndDb,
                accountsToSaveIds, curExtIdBalanceInfo);

        Map<Long, String> curIdExtId = mergedCurrenciesFromLoymaxAndDb.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getValue().getId(), e -> e.getValue().getExternalId()));

        Map<String, Long> allAccountsIds = existingAccounts.values().stream()
                .collect(Collectors.toMap(acc -> acc.getCurrency().getExternalId(), AccountFull::getId));

        var savedAccsIds = currIdSavedAccList.entrySet().stream()
                .collect(Collectors.toMap(e -> curIdExtId.get(e.getKey()), e -> e.getValue().getId()));

        allAccountsIds.putAll(savedAccsIds);

        deleteExistingLifeTimeByTime(existingAccounts);
        batchSaveAllLifeTimeByTime(loymaxAccounts, allAccountsIds);

        deleteExsitingLifeTimeByPeriod(existingAccounts);
        batchSaveAllLifeTimeByPeriod(loymaxAccounts, allAccountsIds);

        return fetchByUserIdAndCurrenciesExtIds(userId, curExtIdBalanceInfo.keySet());
    }

    private void updateAccountsIfNeeded(final Map<String, AccountFull> existingAccounts,
                                        final Map<String,LoymaxUserBalanceInfoDto> loymaxAccounts) {
        var accountsToUpdate = new ArrayList<Account>();
        existingAccounts.entrySet().stream()
                .filter(currExtIdEntity -> loymaxAccounts.containsKey(currExtIdEntity.getKey()))
                .forEach(entry -> {
                    var loymaxAccount = loymaxAccounts.get(entry.getKey());
                    var accPojo = AccountsMapper.INSTANCE.loymaxDtoToPojoEntity(loymaxAccount);
                    var existingAccount = entry.getValue();
                    if (!accPojo.getAmount().equals(existingAccount.getAmount())
                            || !accPojo.getNotActivatedAmount().equals(existingAccount.getNotActivatedAmount())) {
                        accPojo.setUserId(existingAccount.getUserId());
                        accPojo.setId(existingAccount.getId());
                        accPojo.setCurrencyId(existingAccount.getCurrency().getId());
                        accountsToUpdate.add(accPojo);
                    }
                });
        batchUpdateAccountsPojos(accountsToUpdate);
    }

    @Override
    public void batchUpdateAccountsPojos(ArrayList<Account> accountsToUpdate) {
        accountsRepository.batchUpdateAccountsPojos(accountsToUpdate);
    }

    // NOTE: currency external id is bind and equal to account id
    private Map<Long, Account> saveAccountsFromLoymax(Long userId,
                                                      Map<String, CurrencyEntity> mergedCurrenciesFromLoymaxAndDb,
                                                      Set<String> accountsToSaveIds,
                                                      Map<String, LoymaxUserBalanceInfoDto> curExtIdBalanceInfo) {
        var accountsToSave = accountsToSaveIds.stream()
                .map(currExtId -> {
                    var loymaxUserBalanceInfo = curExtIdBalanceInfo.get(currExtId);
                    var accPojo = AccountsMapper.INSTANCE.loymaxDtoToPojoEntity(loymaxUserBalanceInfo);
                    accPojo.setUserId(userId);

                    var currencyId = mergedCurrenciesFromLoymaxAndDb.get(currExtId).getId();
                    accPojo.setCurrencyId(currencyId);
                    return accPojo;
                })
                .collect(Collectors.toList());

        return accountsRepository.batchSave(accountsToSave).stream()
                .collect(Collectors.toMap(Account::getCurrencyId, Function.identity()));
    }

    private Map<String, CurrencyEntity> updateCurrencies(Map<String, LoymaxUserBalanceInfoDto> curExtIdBalanceInfo) {
        Map<String, CurrencyEntity> extIdCurrenciesFromLoymaxAccs = curExtIdBalanceInfo.entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey,
                        la -> CurrencyMapper.INSTANCE.loymaxEntityToModel(la.getValue().getCurrency())));

        Map<String, CurrencyEntity> existingCurrencies = currencyService
                .fetchByExternalIds(extIdCurrenciesFromLoymaxAccs.keySet())

                .stream().collect(Collectors.toMap(CurrencyEntity::getExternalId, Function.identity()));
        var currenciesToSave = extIdCurrenciesFromLoymaxAccs.values().stream()
                .filter(curr -> !existingCurrencies.containsKey(curr.getExternalId()))
                .collect(Collectors.toList());

        Map<String, CurrencyEntity> savedCurrencies = currencyService.batchSave(currenciesToSave).stream()
                .collect(Collectors.toMap(CurrencyEntity::getExternalId, Function.identity()));

        Map<String, CurrencyEntity> mergedCurrenciesFromLoymaxAndDb = new HashMap<>(existingCurrencies);
        mergedCurrenciesFromLoymaxAndDb.putAll(savedCurrencies);
        return mergedCurrenciesFromLoymaxAndDb;
    }

    private void deleteExistingLifeTimeByTime(Map<String, AccountFull> existingAccounts) {
        List<Long> ltbtToDelete = existingAccounts.values().stream().flatMap(acc -> acc.getAccountLifeTimeByTime().values()
                .stream().map(AccountsLifeTimesByTime::getId)).collect(Collectors.toList());
       if (!ltbtToDelete.isEmpty())
            accountsLifeTimesByTimeService.deleteByIds(ltbtToDelete);
    }

    private void deleteExsitingLifeTimeByPeriod(Map<String, AccountFull> existingAccounts) {
        List<Long> ltbpToDelete = existingAccounts.values().stream().flatMap(acc -> acc.getAccountLifeTimeByPeriod().values()
                .stream().map(AccountsLifeTimesByPeriod::getId)).collect(Collectors.toList());
        if (!ltbpToDelete.isEmpty())
            accountsLifeTimesByPeriodService.deleteByAccountIds(ltbpToDelete);
    }

    private void batchSaveAllLifeTimeByPeriod(List<LoymaxUserBalanceInfoDto> loymaxAccounts,
                                              Map<String, Long> extIdAccId) {
        List<AccountsLifeTimesByPeriod> currExtIdAllLTBPToSave = loymaxAccounts.stream().flatMap(
                la -> la.getLifeTimesByPeriod().stream().map(lltbp -> {
                    var entity = AccountLifeTimeByPeriodMapper.INSTANCE.loymaxDtoToEntity(lltbp);
                    entity.setAccountId(extIdAccId.get(la.getCurrency().getUid()));
                    return entity;
                })).collect(Collectors.toList());
        if (!currExtIdAllLTBPToSave.isEmpty())
            accountsLifeTimesByPeriodService.batchSave(currExtIdAllLTBPToSave);
    }

    private void batchSaveAllLifeTimeByTime(List<LoymaxUserBalanceInfoDto> loymaxAccounts,
                                            Map<String, Long> extIdAccId) {
        List<AccountsLifeTimesByTime> currExtIdAllLTBTToSave = loymaxAccounts.stream()
                .flatMap(la -> la.getLifeTimesByTime().stream().map(lltbt -> {
                    var entity = AccountLifeTimeByTimeMapper.INSTANCE.loymaxDtoToEntity(lltbt);
                    entity.setAccountId(extIdAccId.get(la.getCurrency().getUid()));
                    return entity;
                })).collect(Collectors.toList());
        if (!currExtIdAllLTBTToSave.isEmpty())
            accountsLifeTimesByTimeService.batchSave(currExtIdAllLTBTToSave);
    }

    private List<AccountFull> fetchByUserIdAndCurrenciesExtIds(Long userId, Set<String> extCurrIds) {
        return accountsRepository.fetchByUserIdAndExtCurrencyId(extCurrIds, userId);
    }

    private List<AccountFull> fetchByUserIdAndExtCurrencyId(Long userId, Set<String> currIds) {
        return accountsRepository.fetchByUserIdAndExtCurrencyId(currIds, userId);
    }

    @Override
    public List<AccountFull> list(Long userId, Integer offset, Integer limit) {
        return accountsRepository.getListByUserId(userId, offset, limit);
    }

}