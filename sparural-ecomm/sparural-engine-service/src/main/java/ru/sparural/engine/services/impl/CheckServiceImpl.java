package ru.sparural.engine.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.Currency;
import ru.sparural.engine.api.dto.NameCases;
import ru.sparural.engine.api.dto.check.CheckDto;
import ru.sparural.engine.api.dto.merchant.Merchants;
import ru.sparural.engine.api.dto.user.UserFilterDto;
import ru.sparural.engine.entity.*;
import ru.sparural.engine.loymax.rest.dto.check.LoymaxCheckItem;
import ru.sparural.engine.loymax.rest.dto.check.LoymaxCheckItemPosition;
import ru.sparural.engine.loymax.rest.dto.check.LoymaxLocation;
import ru.sparural.engine.loymax.rest.dto.currency.LoymaxCurrency;
import ru.sparural.engine.loymax.rest.dto.reward.LoymaxReward;
import ru.sparural.engine.loymax.rest.dto.withdraw.LoymaxWithdraw;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.loymax.utils.LoymaxTimeToSparTimeAdapter;
import ru.sparural.engine.repositories.CheckRepository;
import ru.sparural.engine.services.*;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.ServiceException;
import ru.sparural.engine.utils.DtoMapperUtils;
import ru.sparural.tables.pojos.Checks;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckServiceImpl implements CheckService {
    private final CheckRepository repository;
    private final DtoMapperUtils dtoMapperUtils;
    private final MerchantService merchantService;
    private final CurrencyService currencyService;
    private final CardsService cardsService;
    private final ItemService itemService;
    private final WithdrawService withdrawService;
    private final RewardService rewardService;
    private final LoymaxService loymaxService;

    private final boolean MERCHANT_FORMATS_DRAFT_HARDCODE = false;
    private final String MERCHANTS_WORKING_HOURS_FROM_HARDCODE = "10:00";
    private final String MERCHANTS_WORKING_HOURS_TO_HARDCODE = "22:00";
    private final boolean MERCHANTS_IS_PUBLIC_HARDCODE = true;

    private final ObjectMapper mapper = new ObjectMapper();

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Override
    public CheckDto get(Long id, Long userId) {
        var cardId = cardsService.findCardIdByUserId(userId);
        var check = repository.get(id, cardId)
                .orElseThrow(() -> new ResourceNotFoundException("This check not found"));
        var checkDto = createDto(check);
        checkDto.setMerchant(merchantService.getForChecks(check.getMerchantId(), userId));
        checkDto.setCurrency(currencyService.get(check.getCurrencyId()));
        return checkDto;
    }

    @Override
    public CheckEntity createEntity(CheckDto dto) {
        return dtoMapperUtils.convert(dto, CheckEntity.class);
    }

    @Override
    public List<CheckEntity> loadChecksForUser(Long userId) {
        cardsService.selectAndBindUserCards(userId);
        var loymaxChecks = loymaxService.loadChecksForUser(userId);
        return convertFromLoymaxCheckAndSave(loymaxChecks, userId);
    }

    @Override
    public CheckDto saveOrUpdate(CheckDto dto) {
        return createDto(repository.saveOrUpdate(createEntity(dto))
                .orElseThrow(() -> new ServiceException("Failed to create check")));
    }

    @Override
    public Long getMerchantIdOfLastCheck(Long userId) {
        return repository.getMerchantIdOfLastCheck(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User has no checks"));
    }

    @Override
    public Optional<Checks> getByLoymaxId(String id) {
        return Optional.empty();
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
    public List<CheckEntity> processingSaveOrUpdateChecks(
            List<LoymaxCheckItem> loymaxChecks, Long userId) {
        var loymaxIds = loymaxChecks.stream()
                .map(LoymaxCheckItem::getId)
                .collect(Collectors.toList());

        var existingLoymaxChecks = loadExistingLoymaxChecks(loymaxIds);

        var existingLoymaxChecksIds = existingLoymaxChecks.stream()
                .map(CheckEntity::getExternalPurchaseId)
                .collect(Collectors.toSet());
        var checksToSave = loymaxChecks.stream()
                .filter(x -> !existingLoymaxChecksIds.contains(x.getId()))
                .collect(Collectors.toList());

        var result = convertFromLoymaxCheckAndSave(checksToSave, userId);
        result.addAll(existingLoymaxChecks);
        result.sort(Comparator.comparingLong(CheckEntity::getDateTime));
        return result;
    }

    private List<CheckEntity> loadExistingLoymaxChecks(List<String> loymaxIds) {
        Map<Long, CurrencyEntity> currencies = loadExistingCurrencies()
                .stream()
                .collect(Collectors.toMap(CurrencyEntity::getId, Function.identity()));
        return repository.fetchAllByLoymaxIds(loymaxIds, currencies);
    }

    // TODO jooq cacheable
    private List<CurrencyEntity> loadExistingCurrencies() {
        return currencyService.fetchAll();
    }

    /**
     * Save all loymax objects and its nested objects by batch
     */
//    @Transactional // TODO - add deferrable values to foreign key constraints
    public List<CheckEntity> convertFromLoymaxCheckAndSave(
            List<LoymaxCheckItem> loymaxChecksList, Long userId) {
        if (loymaxChecksList.isEmpty())
            return new ArrayList<>();

        // 1. batch save currencies values from all checks {
        // group currencies by loymax check id
        Map<String, LoymaxCurrency> checkCurrencies = loymaxChecksList.stream()
                .collect(Collectors.toMap(LoymaxCheckItem::getId,
                        x -> x.getData().getAmount().getCurrencyInfo()));

        Map<String, String> checkIdCurrId = loymaxChecksList.stream()
                .collect(Collectors.toMap(LoymaxCheckItem::getId,
                        x -> x.getData().getAmount().getCurrencyInfo().getUid()));

        // group saved checks by check external id
        Map<String, CurrencyEntity> savedChecksCurrencies = collectListToMap(
                currencyService.batchSave(checkCurrencies.values().stream()
                        .map(this::mapToCurrencyPojo)
                        .collect(Collectors.toList())).stream(),
                CurrencyEntity::getExternalId);
        // }

        // 2. find cards ids by checks identity {
        var identities = loymaxChecksList.stream().map(LoymaxCheckItem::getIdentity)
                .collect(Collectors.toSet());

        Map<String, Long> identityCardId = cardsService.findAllByNumbers(identities, userId)
                .stream().collect(Collectors.toMap(
                        CheckIdentityCardId::getIdentity, CheckIdentityCardId::getCardId));
        // }

        // 3. save merchants {
        // saved merchant formats grouped by name
        Map<String, MerchantFormat> savedMerchantFormats = collectListToMap(
                merchantService.batchSaveMerchantFormat(loymaxChecksList.stream()
                        .map(x -> mapToMerchantFormatPojo(x.getBrand().getName()))
                        .collect(Collectors.toList())).stream(), MerchantFormat::getName);

        Map<String, String> checkIdMerchantLocId = loymaxChecksList.stream()
                .collect(Collectors.toMap(LoymaxCheckItem::getId,
                        x -> x.getLocation().getLocationId()));

        // group saved merchants by check id
        Map<String, Merchant> savedMerchants = collectListToMap(
                merchantService.batchSave(loymaxChecksList.stream()
                        .map(x -> mapToMerchantPojo(x.getLocation(),
                                savedMerchantFormats.get(x.getBrand().getName()).getId(),
                                x.getDescription())).collect(Collectors.toList())).stream(),
                Merchant::getLoymaxLocationId);
        // }

        // 4. save check pojo object without all bindings {
        List<CheckEntity> savedChecksEntities = batchSaveOrUpdate(loymaxChecksList.stream()
                .map(x -> mapToCheck(x,
                        savedMerchants.get(checkIdMerchantLocId.get(x.getId())),
                        savedChecksCurrencies.get(checkIdCurrId.get(x.getId())),
                        userId, identityCardId.get(x.getIdentity()))).collect(Collectors.toList())).stream()
                .peek(savedEntity -> {
                    savedEntity.setCurrency(savedChecksCurrencies.get(checkIdCurrId.get(savedEntity.getExternalPurchaseId())));
                    savedEntity.setMerchant(savedMerchants.get(checkIdMerchantLocId.get(savedEntity.getExternalPurchaseId())));
                })
                .collect(Collectors.toList());

        Map<String, CheckEntity> externalIdCheckEntity = collectListToMap(
                savedChecksEntities.stream(), CheckEntity::getExternalPurchaseId);

        Map<Long, CheckEntity> checkIdCheckEntity = collectListToMap(
                savedChecksEntities.stream(), CheckEntity::getId);
        // }

        // 5. save checksItems {
        var saveCheckTimesTask = executorService.submit(() -> {
            Map<String, List<LoymaxCheckItemPosition>> checkCheckItem = loymaxChecksList.stream()
                    .collect(Collectors.toMap(
                            LoymaxCheckItem::getId, x -> x.getData().getChequeItems()));

            List<ru.sparural.engine.entity.Item> itemsToSave = checkCheckItem.entrySet().stream().flatMap(e -> e.getValue().stream()
                            .map(x -> mapToCheckItemPojo(x, externalIdCheckEntity.get(e.getKey()).getId())))
                    .collect(Collectors.toList());

            var savedItems = itemService.batchSave(itemsToSave)
                    .stream().collect(Collectors.groupingBy(Item::getCheckId));

            checkIdCheckEntity.forEach((k, v) -> v.setItems(
                    Optional.ofNullable(savedItems.get(k)).orElse(new ArrayList<>())
                            .stream().collect(Collectors.toMap(i -> i.getId(), Function.identity())))
            );
        });
        // }

        // 6. save rewards
        var saveRewardsTask = executorService.submit(() -> {
            // 6.1 save reward's currencies {
            Set<LoymaxReward> loymaxRewards = loymaxChecksList.stream()
                    .flatMap(x -> x.getData().getRewards().stream())
                    .collect(Collectors.toSet());

            Map<String, CurrencyEntity> savedRewardCurrencies = collectListToMap(currencyService
                            .batchSave(loymaxRewards.stream()
                                    .map(i -> i.getAmount().getCurrencyInfo())
                                    .map(this::mapToCurrencyPojo)
                                    .collect(Collectors.toList())).stream(),
                    CurrencyEntity::getExternalId);
            // }

            // 6.2 batch save rewards {
            Map<String, List<LoymaxReward>> checksCheckRewards = loymaxChecksList.stream()
                    .collect(Collectors.toMap(
                            LoymaxCheckItem::getId, x -> x.getData().getRewards()));

            List<ru.sparural.engine.entity.Reward> rewardsToSave = checksCheckRewards.entrySet()
                    .stream()
                    .flatMap(e -> e.getValue().stream()
                            .map(loymaxReward -> mapToReward(
                                    loymaxReward,
                                    savedRewardCurrencies.get(
                                            loymaxReward.getAmount().getCurrencyInfo().getUid()),
                                    externalIdCheckEntity.get(e.getKey()).getId())))
                    .collect(Collectors.toList());
            var savedRewCurMap = collectListToMap(savedRewardCurrencies.values().stream(), x -> x.getId());
            var rewards = rewardService.batchSave(rewardsToSave)
                    .stream().peek(r -> r.setCurrency(savedRewCurMap.get(r.getCurrencyId()))).collect(Collectors.groupingBy(Reward::getCheckId));

            checkIdCheckEntity.forEach((k, v) -> v.setRewards(
                    Optional.ofNullable(rewards.get(k)).orElse(new ArrayList<>())
                    .stream().collect(Collectors.toMap(r -> r.getId(), Function.identity())))
            );
            // }
        });
        // }

        // 7. save withdraws
        var saveWithdrawsTask = executorService.submit(() -> {

            // 7.1 save withdraws currencies {
            Set<LoymaxWithdraw> loymaxWithdraws = loymaxChecksList.stream()
                    .flatMap(x -> x.getData().getWithdraws().stream())
                    .collect(Collectors.toSet());

            Map<String, CurrencyEntity> savedWithdrawsCurrencies = collectListToMap(currencyService
                            .batchSave(loymaxWithdraws.stream()
                                    .map(lw -> lw.getAmount().getCurrencyInfo())
                                    .map(this::mapToCurrencyPojo)
                                    .collect(Collectors.toList())).stream(),
                    CurrencyEntity::getExternalId);
            // }

            // 7.2 batch save withdraws {
            Map<String, List<LoymaxWithdraw>> checksCheckWithdraws = loymaxChecksList.stream()
                    .collect(Collectors.toMap(
                            LoymaxCheckItem::getId, x -> x.getData().getWithdraws()));

            List<ru.sparural.engine.entity.Withdraw> withdrawsToSave = checksCheckWithdraws.entrySet()
                    .stream()
                    .flatMap(e -> e.getValue().stream()
                            .map(loymaxWithdraw -> mapToWithDraw(
                                    loymaxWithdraw,
                                    savedWithdrawsCurrencies.get(
                                            loymaxWithdraw.getAmount().getCurrencyInfo().getUid()),
                                    externalIdCheckEntity.get(e.getKey()).getId())))
                    .collect(Collectors.toList());
            var savedWithCurMap = collectListToMap(savedWithdrawsCurrencies.values().stream(), x -> x.getId());
            var withdraws = withdrawService.batchSave(withdrawsToSave)
                    .stream().peek(w -> w.setCurrency(savedWithCurMap.get(w.getCurrencyId())))
                    .collect(Collectors.groupingBy(Withdraw::getCheckId));

            checkIdCheckEntity.forEach((k, v) -> v.setWithdraws(
                    Optional.ofNullable(withdraws.get(k)).orElse(new ArrayList<>())
                    .stream().collect(Collectors.toMap(w -> w.getId(), Function.identity())))
            );
            // }
        });
        // }

        try {
            saveCheckTimesTask.get();
            saveWithdrawsTask.get();
            saveRewardsTask.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return savedChecksEntities;
    }

    public List<CheckEntity> batchSaveOrUpdate(List<CheckEntity> checkEntityList) {
        return repository.batchSaveOrUpdate(checkEntityList);
    }

    private <K, V> Map<K, V> collectListToMap(Stream<V> values, Function<V, K> extractKey) {
        return values.collect(Collectors.toMap(extractKey, Function.identity()));
    }

    private ru.sparural.engine.entity.Reward mapToReward(LoymaxReward loymaxReward,
                                                         CurrencyEntity currency,
                                                         Long checkId) {
        var entity = new Reward();
        entity.setCheckId(checkId);
        entity.setCurrencyId(currency.getId());
        entity.setCurrency(currency);
        entity.setAmount(loymaxReward.getAmount().getAmount());
        entity.setDescription(loymaxReward.getDescription());
        entity.setRewardType(loymaxReward.getRewardType());
        return entity;
    }

    private ru.sparural.engine.entity.Withdraw mapToWithDraw(LoymaxWithdraw loymaxWithdraw,
                                                             CurrencyEntity currency,
                                                             Long checkId) {
        var entity = new Withdraw();
        entity.setCheckId(checkId);
        entity.setCurrencyId(currency.getId());
        entity.setCurrency(currency);
        entity.setAmount(loymaxWithdraw.getAmount().getAmount());
        entity.setDescription(loymaxWithdraw.getDescription());
        entity.setWithdrawType(loymaxWithdraw.getWithdrawType());
        return entity;
    }

    private ru.sparural.engine.entity.CurrencyEntity mapToCurrencyPojo(LoymaxCurrency loymaxCurrency) {
        var loymaxNameCases = loymaxCurrency.getNameCases();
        var nameCases = new NameCasesEntity();
        nameCases.setAbbreviation(loymaxNameCases.getAbbreviation());
        nameCases.setGenitive(loymaxNameCases.getGenitive());
        nameCases.setNominative(loymaxNameCases.getNominative());
        nameCases.setPlural(loymaxNameCases.getPlural());

        var currency = new ru.sparural.engine.entity.CurrencyEntity();
        currency.setNameCases(nameCases);
        currency.setName(loymaxCurrency.getName());
        currency.setDescription(loymaxCurrency.getDescription());
        currency.setIsDeleted(loymaxCurrency.getIsDeleted());
        currency.setExternalId(loymaxCurrency.getUid());
        return currency;
    }

    private CheckEntity mapToCheck(LoymaxCheckItem checkItem,
                                   Merchant merchant,
                                   CurrencyEntity currency,
                                   Long userId,
                                   Long cardId) {
        var entity = new CheckEntity();
        entity.setMerchantId(merchant.getId());
        entity.setMerchant(merchant);
        entity.setDateTime(LoymaxTimeToSparTimeAdapter.convertToEpochSeconds(checkItem.getDateTime()) - 18000);
        entity.setIsRefund(checkItem.getData().getIsRefund());
        entity.setCheckNumber(checkItem.getData().getChequeNumber());
        entity.setAmount(checkItem.getData().getAmount().getAmount());
        entity.setExternalPurchaseId(checkItem.getId());
        entity.setUserId(userId);
        entity.setCurrencyId(currency.getId());
        entity.setCurrency(currency);
        entity.setCardId(cardId);
        return entity;
    }

    private ru.sparural.engine.entity.Item mapToCheckItemPojo(LoymaxCheckItemPosition loymaxCheckItem, Long checkId) {
        var entity = new Item();
        entity.setCheckId(checkId);
        entity.setExternalId(loymaxCheckItem.getItemId());
        entity.setAmount(loymaxCheckItem.getAmount());
        entity.setUnit(loymaxCheckItem.getUnit());
        entity.setDescription(loymaxCheckItem.getDescription());
        entity.setCount(loymaxCheckItem.getCount());
        entity.setPositionId(loymaxCheckItem.getPositionId());
        return entity;
    }

    private Merchant mapToMerchantPojo(LoymaxLocation loymaxLocation,
                                       Long formatId, String description) {
        var merchant = new Merchant();
        merchant.setLoymaxLocationId(loymaxLocation.getLocationId());
        merchant.setAddress(loymaxLocation.getDescription());
        merchant.setIsPublic(MERCHANTS_IS_PUBLIC_HARDCODE);
        merchant.setLatitude(loymaxLocation.getLatitude());
        merchant.setLongitude(loymaxLocation.getLongitude());
        merchant.setWorkingHoursFrom(MERCHANTS_WORKING_HOURS_FROM_HARDCODE);
        merchant.setWorkingHoursTo(MERCHANTS_WORKING_HOURS_TO_HARDCODE);
        merchant.setFormatId(formatId);
        merchant.setTitle(description);
        return merchant;
    }

    private MerchantFormat mapToMerchantFormatPojo(String name) {
        var merchFormat = new MerchantFormat();
        merchFormat.setName(name);
        merchFormat.setDraft(MERCHANT_FORMATS_DRAFT_HARDCODE);
        return merchFormat;
    }

    public CheckDto createDto(CheckEntity entity) {
        var checkDto = new CheckDto();

        checkDto.setWithdraws(Optional.ofNullable(entity.getWithdraws()).orElse(new HashMap<>())
                .values()
                .stream().map(this::mapToWithdrawDto).collect(Collectors.toList()));

        checkDto.setRewards(Optional.ofNullable(entity.getRewards()).orElse(new HashMap<>())
                .values()
                .stream().map(this::mapToRewardDto).collect(Collectors.toList()));

        checkDto.setItems(Optional.ofNullable(entity.getItems()).orElse(new HashMap<>())
                .values()
                .stream().map(this::mapToItemDto).collect(Collectors.toList()));

        if (entity.getMerchant() != null)
            checkDto.setMerchant(mapToMerchant(entity.getMerchant()));

        if (entity.getCurrency() != null)
            checkDto.setCurrency(mapToCurrencyDto(entity.getCurrency()));

        checkDto.setAmount(entity.getAmount());
        checkDto.setCheckNumber(entity.getCheckNumber());
        checkDto.setCardId(entity.getCardId());
        checkDto.setMerchantsId(entity.getMerchantId());
        checkDto.setIsRefund(entity.getIsRefund());
        checkDto.setUserId(entity.getUserId());
        checkDto.setExternalPurchaseId(entity.getExternalPurchaseId());
        checkDto.setDateTime(entity.getDateTime());
        checkDto.setId(entity.getId());
        return checkDto;
    }

    private Merchants mapToMerchant(Merchant merchant) {
        var dto = new Merchants();
        dto.setId(merchant.getId());
        dto.setAddress(merchant.getAddress());
        dto.setLatitude(merchant.getLatitude());
        dto.setLongitude(merchant.getLongitude());
        dto.setWorkingHoursFrom(merchant.getWorkingHoursFrom());
        dto.setWorkingHoursTo(merchant.getWorkingHoursTo());
        dto.setIsPublic(merchant.getIsPublic());
        dto.setTitle(merchant.getTitle());
        return dto;
    }

    private ru.sparural.engine.api.dto.check.Reward mapToRewardDto(ru.sparural.engine.entity.Reward entity) {
        var dto = new ru.sparural.engine.api.dto.check.Reward();
        dto.setAmount(entity.getAmount());
        dto.setCheckId(entity.getCheckId());
        dto.setCurrency(mapToCurrencyDto(entity.getCurrency()));
        dto.setId(entity.getId());
        dto.setRewardType(entity.getRewardType());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    private ru.sparural.engine.api.dto.check.Withdraw mapToWithdrawDto(ru.sparural.engine.entity.Withdraw entity) {
        var dto = new ru.sparural.engine.api.dto.check.Withdraw();
        dto.setAmount(entity.getAmount());
        dto.setCheckId(entity.getCheckId());
        dto.setCurrency(mapToCurrencyDto(entity.getCurrency()));
        dto.setId(entity.getId());
        dto.setWithdrawType(entity.getWithdrawType());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    private ru.sparural.engine.api.dto.check.Item mapToItemDto(ru.sparural.engine.entity.Item entity) {
        if (entity == null)
            return null;
        return dtoMapperUtils.convert(entity, ru.sparural.engine.api.dto.check.Item.class);
    }

    private Currency mapToCurrencyDto(CurrencyEntity entity) {
        if (entity == null)
            return null;
        var curr = new Currency();
        curr.setDescription(entity.getDescription());
        curr.setId(entity.getId());
        var namesCases = entity.getNameCases();
        var str = mapper.convertValue(namesCases, String.class);
        NameCases cases = null;
        try {
            cases = mapper.readValue(str, NameCases.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        curr.setNameCases(cases);
        return curr;
    }


}
