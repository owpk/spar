package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.Currency;
import ru.sparural.engine.api.dto.MerchantDto;
import ru.sparural.engine.api.dto.NameCases;
import ru.sparural.engine.api.dto.cards.UserCardDto;
import ru.sparural.engine.api.dto.check.CheckDto;
import ru.sparural.engine.api.dto.check.Item;
import ru.sparural.engine.api.dto.check.Reward;
import ru.sparural.engine.api.dto.check.Withdraw;
import ru.sparural.engine.api.dto.merchant.Format;
import ru.sparural.engine.api.dto.merchant.Merchants;
import ru.sparural.engine.api.dto.user.UserFilterDto;
import ru.sparural.engine.entity.CheckDBEntity;
import ru.sparural.engine.entity.CheckEntity;
import ru.sparural.engine.entity.LoymaxChecksItem;
import ru.sparural.engine.entity.LoymaxUser;
import ru.sparural.engine.loymax.rest.dto.check.LoymaxCheckItem;
import ru.sparural.engine.loymax.rest.dto.check.LoymaxCheckItemPosition;
import ru.sparural.engine.loymax.rest.dto.reward.LoymaxReward;
import ru.sparural.engine.loymax.rest.dto.withdraw.LoymaxWithdraw;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.loymax.utils.LoymaxTimeToSparTimeAdapter;
import ru.sparural.engine.repositories.CardRepository;
import ru.sparural.engine.repositories.CheckRepository;
import ru.sparural.engine.services.CardsService;
import ru.sparural.engine.services.CheckService;
import ru.sparural.engine.services.CurrencyService;
import ru.sparural.engine.services.ItemService;
import ru.sparural.engine.services.MerchantFormatService;
import ru.sparural.engine.services.MerchantService;
import ru.sparural.engine.services.RewardService;
import ru.sparural.engine.services.WithdrawService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.ServiceException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckServiceImpl implements CheckService {
    private final CheckRepository repository;
    private final CardRepository cardRepository;
    private final DtoMapperUtils dtoMapperUtils;
    private final MerchantService merchantService;
    private final CurrencyService currencyService;
    private final CardsService cardsService;
    private final ItemService itemService;
    private final WithdrawService withdrawService;
    private final RewardService rewardService;
    private final MerchantFormatService merchantFormatService;
    private final LoymaxService loymaxService;

    @Override
    public CheckDto get(Long id, Long userId) {
        var cardId = findCardIdByUserid(userId);
        var check = repository.get(id, cardId)
                .orElseThrow(() -> new ResourceNotFoundException("This check not found"));
        var checkDto = createDto(check);
        checkDto.setMerchant(merchantService.getForChecks(check.getMerchantsId(), userId));
        //checkDto.setCurrency(currencyService.get(check.getCurrencyId()));
        return checkDto;
    }

    @Override
    public CheckDto createDto(CheckEntity entity) {
        return dtoMapperUtils.convert(CheckDto.class, () -> entity);
    }

    @Override
    public CheckEntity createEntity(CheckDto dto) {
        return dtoMapperUtils.convert(dto, CheckEntity.class);
    }

    @Override
    public Long findCardIdByUserid(Long userId) {
        return cardRepository.findByCardIdByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("This card not found"));
    }

    @Override
    public List<CheckEntity> loadChecksForUser(Long userId) {
        cardsService.selectAndBindUserCards(userId);
        var loymaxChecks = loymaxService.loadChecksForUser(userId);
        var checksToSave = loymaxChecks.stream()
                .map(lc -> {
                    var checkDto = new CheckEntity();
                    var card = new UserCardDto();
                    var merchant = new Merchants();
                    var merchantFormat = new Format();
                    var merchantDto = new MerchantDto();
                    var nameCases = new NameCases();
                    var currency = new Currency();
                    card = cardsService.findByNumber(lc.getIdentity(), userId);
                    checkDto.setDateTime(LoymaxTimeToSparTimeAdapter.convertToEpochSeconds(lc.getDateTime()) - 18000);
                    checkDto.setIsRefund(lc.getData().getIsRefund());
                    checkDto.setCheckNumber(lc.getData().getChequeNumber());
                    checkDto.setAmount(lc.getData().getAmount().getAmount());
                    checkDto.setExternalPurchaseId(lc.getId());
                    checkDto.setUserId(userId);
                    BeanUtils.copyProperties(lc.getData().getAmount().getCurrencyInfo().getNameCases(), nameCases);
                    BeanUtils.copyProperties(lc.getData().getAmount().getCurrencyInfo(), currency);
                    currency.setNameCases(nameCases);

                    checkDto.setCurrenciesId(currencyService.save(currency).getId());
                    if (card != null)
                        checkDto.setCardId(card.getId());
                    if (lc.getLocation() != null) {
                        merchant = merchantService.getFromLoymaxMerchant(lc.getLocation().getLocationId());
                        if (merchant != null) {
                            checkDto.setMerchantsId(merchant.getId());
                        } else {
                            log.warn("Can't find merchant with location: " + lc.getLocation().getLocationId() + " : creating new record...");
                            merchant = new Merchants();
                            merchantDto = new MerchantDto();
                            merchantFormat = merchantFormatService.checkIfExist(lc.getBrand().getName());
                            BeanUtils.copyProperties(lc.getLocation(), merchant);
                            merchant.setLoymaxLocationId(lc.getLocation().getLocationId());
                            merchant.setAddress(lc.getLocation().getDescription());
                            merchant.setIsPublic(true);
                            merchant.setStatus("Open");
                            merchant.setWorkingHoursFrom("10:00");
                            merchant.setWorkingHoursTo("22:00");
                            merchant.setFormat(merchantFormat);
                            merchant.setTitle(lc.getDescription());
                            BeanUtils.copyProperties(merchant, merchantDto);
                            merchantDto.setLoymaxLocationId(lc.getLocation().getLocationId());
                            merchantDto.setFormatId(merchantFormat.getId());
                            merchantDto.setWorkingStatus(merchant.getStatus());
                            log.warn("Merchant saved:" + merchant.getId());
                            checkDto.setMerchantsId(merchantService.saveOrUpdate(merchantDto).getId());
                        }
                    }

                    Map<Integer, String> loymaxItemMap = new HashMap<>();
                    var itemList = new ArrayList<ru.sparural.engine.entity.Item>();
                    for (LoymaxCheckItemPosition loymaxItem : lc.getData().getChequeItems()) {
                        var item = new ru.sparural.engine.entity.Item();
                        BeanUtils.copyProperties(loymaxItem, item);
                        double scale = Math.pow(10, 2);
                        item.setCount(Math.ceil(item.getCount() * scale) / scale);
                        item.setCheckId(checkDto.getId());
                        itemList.add(item);
                        loymaxItemMap.put(loymaxItem.getPositionId(), loymaxItem.getItemId());
                    }
                    var saved = itemService.batchSave(itemList);
                    var loymaxChecksToSave = saved.stream()
                            .map(x -> new LoymaxChecksItem(0L, x.getId(), loymaxItemMap.get(x.getPositionId())))
                            .collect(Collectors.toList());
                    itemService.batchLoymaxSave(loymaxChecksToSave);

                    List<ru.sparural.engine.entity.Withdraw> withdrawList = new ArrayList<>();
                    List<ru.sparural.engine.entity.Reward> rewardList = new ArrayList<>();

                    for (LoymaxWithdraw loymaxWithdraw : lc.getData().getWithdraws()) {
                        var wNameCases = new NameCases();
                        var wCurrency = new Currency();
                        var withdraw = new ru.sparural.engine.entity.Withdraw();
                        BeanUtils.copyProperties(loymaxWithdraw.getAmount().getCurrencyInfo().getNameCases(), wNameCases);
                        BeanUtils.copyProperties(loymaxWithdraw.getAmount().getCurrencyInfo(), wCurrency);
                        wCurrency.setNameCases(wNameCases);
                        wCurrency = currencyService.save(wCurrency);

                        BeanUtils.copyProperties(loymaxWithdraw, withdraw);
                        withdraw.setAmount(loymaxWithdraw.getAmount().getAmount());
                        withdraw.setCurrenciesId(wCurrency.getId());
                        withdraw.setCheckId(checkDto.getId());
                        withdrawList.add(withdraw);
                    }

                    for (LoymaxReward loymaxReward : lc.getData().getRewards()) {
                        var rNameCases = new NameCases();
                        var rCurrency = new Currency();
                        var reward = new ru.sparural.engine.entity.Reward();
                        BeanUtils.copyProperties(loymaxReward.getAmount().getCurrencyInfo().getNameCases(), rNameCases);
                        BeanUtils.copyProperties(loymaxReward.getAmount().getCurrencyInfo(), rCurrency);
                        rCurrency.setNameCases(rNameCases);
                        rCurrency = currencyService.save(rCurrency);

                        BeanUtils.copyProperties(loymaxReward, reward);
                        reward.setCurrenciesId(rCurrency.getId());
                        reward.setCheckId(checkDto.getId());
                        reward.setAmount(loymaxReward.getAmount().getAmount());
                        rewardList.add(reward);
                    }
                    withdrawService.batchSave(withdrawList);
                    rewardService.batchSave(rewardList);
                    return checkDto;
                })
                .collect(Collectors.toList());
        return batchSaveOrUpdate(checksToSave);
    }

    @Override
    public CheckDto save(CheckDto dto) {
        return createDto(repository.save(createEntity(dto))
                .orElseThrow(() -> new ServiceException("Failed to create check")));
    }

    @Override
    public void saveLoymaxChecks(Long checkId, String historyId) {
        repository.saveLoymaxChecks(checkId, historyId);
    }

    @Override
    public List<LoymaxUser> getAllUserId() {
        return repository.getAllUserId();
    }

    @Override
    public Long getMerchantIdOfLastCheck(Long userId) {
        return repository.getMerchantIdOfLastCheck(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User has no checks"));
    }

    @Override
    public CheckDBEntity getByLoymaxId(String id) {
        Long checkId = repository.getCheckIdByLoymaxId(id)
                .orElse(null);
        if (checkId != null) {
            return repository.getById(checkId)
                    .orElse(null);
        }
        return null;
    }

    @Override
    public List<CheckEntity> getLastCheck(UserFilterDto filter, Long startTime) {
        return repository.getLastCheck(filter, startTime);
    }

    @Override
    public void saveIsNotifCheck(List<Long> checkIds) {
        repository.saveIsNotifCheck(checkIds);
    }

    @Override
    public List<CheckEntity> getAllChecksByUserId(Long x) {
        return repository.getAllChecksByUserId(x);
    }

    @Override
    public List<CheckDto> processingSaveOrUpdateChecks(List<LoymaxCheckItem> loymaxChecks, Long userId) {
        var withdraw = new Withdraw();
        var nameCases = new NameCases();
        var currency = new Currency();
        var item = new Item();
        var reward = new Reward();
        var checkDto = new CheckDto();
        var card = new UserCardDto();
        var merchant = new Merchants();
        var checkFromDB = new CheckDBEntity();
        var merchantFormat = new Format();
        var merchantDto = new MerchantDto();

        List<CheckDto> result = new ArrayList<>();
        List<Item> itemList;
        List<Withdraw> withdrawList;
        List<Reward> rewardList;
        Double writtenBonuses;

        for (LoymaxCheckItem x : loymaxChecks) {
            checkDto = new CheckDto();
            card = cardsService.findByNumber(x.getIdentity(), userId);

            checkFromDB = getByLoymaxId(x.getId());
            if (checkFromDB != null) {
                if (card != null) checkDto.setCardId(card.getId());
                BeanUtils.copyProperties(checkFromDB, checkDto);
                checkDto.setRewards(rewardService.getListByCheckId(checkDto.getId()));
                itemList = itemService.getListByCheckId(checkDto.getId());
                itemList.sort((p1, p2) -> p1.getPositionId().compareTo(p2.getPositionId()));
                checkDto.setItems(itemList);
                checkDto.setWithdraws(withdrawService.getListByCheckId(checkDto.getId()));
                checkDto.setCurrency(currencyService.get(checkFromDB.getCurrencyId()));
                checkDto.setMerchant(merchantService.get(checkFromDB.getMerchantId(), userId));
                result.add(checkDto);
            } else {
                if (card != null) checkDto.setCardId(card.getId());
                checkDto.setDateTime(LoymaxTimeToSparTimeAdapter.convertToEpochSeconds(x.getDateTime()) - 18000);
                checkDto.setIsRefund(x.getData().getIsRefund());
                checkDto.setCheckNumber(x.getData().getChequeNumber());
                checkDto.setAmount(x.getData().getAmount().getAmount());
                checkDto.setExternalPurchaseId(x.getId());
                checkDto.setUserId(userId);
                BeanUtils.copyProperties(x.getData().getAmount().getCurrencyInfo().getNameCases(), nameCases);
                BeanUtils.copyProperties(x.getData().getAmount().getCurrencyInfo(), currency);
                currency.setNameCases(nameCases);
                currency = currencyService.save(currency);

                checkDto.setCurrenciesId(currency.getId());

                if (x.getLocation() != null) {
                    merchant = merchantService.getFromLoymaxMerchant(x.getLocation().getLocationId());
                    if (merchant != null) {
                        checkDto.setMerchantsId(merchant.getId());
                    } else {
                        log.warn("Мы не нашли магазин: " + x.getLocation().getLocationId() + " поэтому создаем его");
                        merchant = new Merchants();
                        merchantDto = new MerchantDto();
                        merchantFormat = merchantFormatService.checkIfExist(x.getBrand().getName());

                        BeanUtils.copyProperties(x.getLocation(), merchant);
                        merchant.setLoymaxLocationId(x.getLocation().getLocationId());
                        merchant.setAddress(x.getLocation().getDescription());
                        merchant.setIsPublic(true);
                        merchant.setStatus("Open");
                        merchant.setWorkingHoursFrom("10:00");
                        merchant.setWorkingHoursTo("22:00");
                        merchant.setFormat(merchantFormat);
                        merchant.setTitle(x.getDescription());
                        BeanUtils.copyProperties(merchant, merchantDto);
                        merchantDto.setLoymaxLocationId(x.getLocation().getLocationId());
                        merchantDto.setFormatId(merchantFormat.getId());
                        merchantDto.setWorkingStatus(merchant.getStatus());

                        log.warn("Cформированный магазин:" + merchant);
                        checkDto.setMerchantsId(merchantService.saveOrUpdate(merchantDto).getId());
                    }
                }

                checkDto = save(checkDto);
                saveLoymaxChecks(checkDto.getId(), x.getId());

                checkDto.setCurrency(currency);
                checkDto.setMerchant(merchant);

                itemList = itemService.getListByCheckId(checkDto.getId());
                if (itemList == null) {
                    itemList = new ArrayList<>();
                    for (LoymaxCheckItemPosition loymaxItem : x.getData().getChequeItems()) {
                        item = new Item();
                        BeanUtils.copyProperties(loymaxItem, item);
                        double scale = Math.pow(10, 2);
                        item.setCount(Math.ceil(item.getCount() * scale) / scale);
                        item.setCheckId(checkDto.getId());
                        item = itemService.save(item);
                        itemService.saveLoymaxItem(item.getId(), loymaxItem.getItemId());
                        itemList.sort(Comparator.comparing(Item::getPositionId));
                        itemList.add(item);
                    }
                    checkDto.setItems(itemList);
                } else {
                    itemList.sort(Comparator.comparing(Item::getPositionId));
                    checkDto.setItems(itemList);
                }

                withdrawList = withdrawService.getListByCheckId(checkDto.getId());
                if (withdrawList == null) {
                    withdrawList = new ArrayList<>();
                    for (LoymaxWithdraw loymaxWithdraw : x.getData().getWithdraws()) {
                        BeanUtils.copyProperties(loymaxWithdraw.getAmount().getCurrencyInfo().getNameCases(), nameCases);
                        BeanUtils.copyProperties(loymaxWithdraw.getAmount().getCurrencyInfo(), currency);
                        currency.setNameCases(nameCases);
                        currency = currencyService.save(currency);

                        BeanUtils.copyProperties(loymaxWithdraw, withdraw);
                        withdraw.setAmount(loymaxWithdraw.getAmount().getAmount());
                        withdraw.setCurrenciesId(currency.getId());
                        withdraw.setCheckId(checkDto.getId());
                        withdraw = withdrawService.saveOrUpdate(withdraw);
                        withdraw.setCurrency(currency);
                        withdrawList.add(withdraw);
                    }
                    checkDto.setWithdraws(withdrawList);
                } else {
                    checkDto.setWithdraws(withdrawList);
                }

                writtenBonuses = 0D;
                for (Withdraw bonus : withdrawList) {
                    writtenBonuses += bonus.getAmount();
                }

                checkDto.setAmount(checkDto.getAmount() + (writtenBonuses / 10));
                double scale = Math.pow(10, 2);
                checkDto.setAmount(Math.ceil(checkDto.getAmount() * scale) / scale);

                if (checkDto.getAmount() < 0)
                    checkDto.setAmount(0D);

                rewardList = rewardService.getListByCheckId(checkDto.getId());
                if (rewardList == null) {
                    rewardList = new ArrayList<>();
                    for (LoymaxReward loymaxReward : x.getData().getRewards()) {
                        BeanUtils.copyProperties(loymaxReward.getAmount().getCurrencyInfo().getNameCases(), nameCases);
                        BeanUtils.copyProperties(loymaxReward.getAmount().getCurrencyInfo(), currency);
                        currency.setNameCases(nameCases);
                        currency = currencyService.save(currency);
                        BeanUtils.copyProperties(loymaxReward, reward);
                        reward.setCurrenciesId(currency.getId());
                        reward.setCheckId(checkDto.getId());
                        reward.setAmount(loymaxReward.getAmount().getAmount());
                        reward = rewardService.saveOrUpdate(reward);
                        reward.setCurrency(currency);
                        rewardList.add(reward);
                    }
                    checkDto.setRewards(rewardList);
                } else {
                    checkDto.setRewards(rewardList);
                }
                result.add(checkDto);
            }
        }

        return result;
    }

    @Override
    public List<CheckEntity> batchSaveOrUpdate(List<CheckEntity> checkEntityList) {
        return repository.batchSaveOrUpdate(checkEntityList);
    }
}
