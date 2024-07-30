package ru.sparural.engine.loymax.services.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.engine.api.dto.*;
import ru.sparural.engine.api.dto.Currency;
import ru.sparural.engine.api.dto.cards.CardPasswordDto;
import ru.sparural.engine.api.dto.cards.UserCardDto;
import ru.sparural.engine.api.dto.check.CheckDto;
import ru.sparural.engine.api.dto.main.CategoryDto;
import ru.sparural.engine.api.dto.promotions.PromotionsDto;
import ru.sparural.engine.api.dto.registration.ConfirmRegistrationRequest;
import ru.sparural.engine.api.dto.registration.RegistrationSetPasswordRequest;
import ru.sparural.engine.api.dto.registration.UserProfileUpdateRequest;
import ru.sparural.engine.api.dto.socials.SocialSetDto;
import ru.sparural.engine.api.dto.user.ChangePasswordRequestDto;
import ru.sparural.engine.api.dto.user.account.UserAccounts;
import ru.sparural.engine.api.enums.SocialName;
import ru.sparural.engine.entity.CardQr;
import ru.sparural.engine.entity.LoymaxUser;
import ru.sparural.engine.entity.Social;
import ru.sparural.engine.loymax.LoymaxConstants;
import ru.sparural.engine.loymax.exceptions.LoymaxException;
import ru.sparural.engine.loymax.rest.LoymaxRestClient;
import ru.sparural.engine.loymax.rest.dto.*;
import ru.sparural.engine.loymax.rest.dto.account.LoymaxUserBalanceInfoDto;
import ru.sparural.engine.loymax.rest.dto.attribute.LoymaxUserAttributeDto;
import ru.sparural.engine.loymax.rest.dto.cards.LoymaxUserCardDto;
import ru.sparural.engine.loymax.rest.dto.cards.LoymaxUserCardRequestDto;
import ru.sparural.engine.loymax.rest.dto.cards.LoymaxUserVirtualCardEmitInfo;
import ru.sparural.engine.loymax.rest.dto.categories.LoymaxUserFavoriteCategories;
import ru.sparural.engine.loymax.rest.dto.check.LoymaxCheck;
import ru.sparural.engine.loymax.rest.dto.check.LoymaxCheckItem;
import ru.sparural.engine.loymax.rest.dto.counter.LoymaxCounterResponse;
import ru.sparural.engine.loymax.rest.dto.currency.LoymaxCurrency;
import ru.sparural.engine.loymax.rest.dto.currency.LoymaxNameCases;
import ru.sparural.engine.loymax.rest.dto.goods.LoymaxAcceptGoodsRequest;
import ru.sparural.engine.loymax.rest.dto.goods.LoymaxPersonalGoodsResponseDto;
import ru.sparural.engine.loymax.rest.dto.offer.LoymaxOffer;
import ru.sparural.engine.loymax.rest.dto.password.LoymaxResetPasswordRequest;
import ru.sparural.engine.loymax.rest.dto.registration.DeregistrationConfirmDto;
import ru.sparural.engine.loymax.rest.dto.status.LoymaxUserStatus;
import ru.sparural.engine.loymax.rest.dto.user.LoymaxUserAction;
import ru.sparural.engine.loymax.rest.dto.user.LoymaxUserInfo;
import ru.sparural.engine.loymax.rest.dto.user.LoymaxUserInfoSystem;
import ru.sparural.engine.loymax.rest.dto.user.LoymaxUserQuestionDto;
import ru.sparural.engine.loymax.rest.dto.user.login.LoginTwoFAResponse;
import ru.sparural.engine.loymax.rest.dto.user.login.SocialIdentifiers;
import ru.sparural.engine.loymax.rest.dto.user.login.UserSocialIdentifier;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.loymax.services.LoymaxSettingsService;
import ru.sparural.engine.loymax.socials.LoymaxSocialsPropertyFactory;
import ru.sparural.engine.loymax.utils.LoymaxTimeToSparTimeAdapter;
import ru.sparural.engine.loymax.utils.PathVariableUtils;
import ru.sparural.engine.loymax.utils.mappers.LoymaxUserMapper;
import ru.sparural.engine.repositories.impl.LoymaxUsersRepository;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.UserNotFoundException;
import ru.sparural.engine.services.exception.ValidationException;
import ru.sparural.engine.services.impl.LoymaxUsersSocialsServiceImpl;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.utils.ReflectUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static ru.sparural.engine.loymax.cache.LoymaxCacheConstantsBean.*;

/**
 * @author Vorobyev Vyacheslav
 */
@Service(value = "loymaxServiceImpl")
@RequiredArgsConstructor
@Slf4j
public class LoymaxServiceImpl implements LoymaxService {

    private final LoymaxSettingsService loymaxSettingsService;
    private final LoymaxRestClient loymaxRestClient;
    private final LoymaxUsersRepository loymaxUsersRepository;
    private final LoymaxSocialsPropertyFactory loymaxSocials;
    private final LoymaxConstants loymaxConstants;
    private final LoymaxUsersSocialsServiceImpl loymaxUsersSocialsService;

    private static String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    @Override
    public TokenExchangeResponse exchangeForToken(String userData, String secret) {
        return loymaxRestClient.exchangeForToken(userData, encodeValue(secret));
    }

    @Override
    public LoymaxUser updateLoymaxUser(LoymaxUser loymaxUser) {
        return loymaxUsersRepository.update(loymaxUser)
                .orElseThrow(() -> new LoymaxException("Cannot update loymax user"));
    }

    @Override
    public LoymaxUser saveOrUpdate(LoymaxUser loymaxUser) {
        return loymaxUsersRepository.saveOrUpdate(loymaxUser)
                .orElseThrow(() -> new LoymaxException("Cannot insert or update loymax user"));
    }

    @Override
    public LoymaxUser findByPersonUid(String personUid) {
        return loymaxUsersRepository.getByPersonUid(personUid)
                .orElseThrow(() -> new UserNotFoundException("User with given person id not found: " + personUid));
    }

    @Override
    @RefreshToken
    public LoymaxUserInfo getUserInfo(LoymaxUser loymaxUser) {
        return loymaxRestClient.getUserInfo(loymaxUser.getToken());
    }

    public LoymaxUserInfo getUserInfo(String token) {
        return loymaxRestClient.getUserInfo(token);
    }

    @Override
    public TokenExchangeResponse loginViaSocial(String secret, String socialName) {
        var loymaxSocialsPropertyHolder = loymaxSocials.getSocialProperties(socialName);
        return loymaxRestClient.loginViaSocial(loymaxSocialsPropertyHolder, secret);
    }

    @Override
    public LoginTwoFAResponse exchangeForOneTimePassword(String login) {
        return loymaxRestClient.exchangeForLoginOneTimeCode(login);
    }

    @Override
    public TokenExchangeResponse loginViaOneTimeCode(String token, String code) {
        return loymaxRestClient.loginViaOneTimeCode(token, code);
    }

    @Override
    public void cancelUpdatingPhoneNumber(LoymaxUser loymaxUser) {
        loymaxRestClient.cancelUserPhoneUpdating(loymaxUser.getToken());
    }

    @Override
    public void confirmLoymaxRegistration(Long userId, ConfirmRegistrationRequest confirmRegistrationRequest) {
        var loymaxUser = getByLocalUserId(userId);
        refreshTokenIfNeeded(loymaxUser);
        var accessToken = loymaxRestClient.createLoymaxQuestionRequest(loymaxConstants.registrationConfirm,
                loymaxUser.getToken(), confirmRegistrationRequest);
        loymaxUser.setToken(accessToken);
        setMobile(loymaxUser);
        updateLoymaxUser(loymaxUser);
    }

    @Override
    public LoymaxUser getByLocalUserId(Long localUserId) throws ResourceNotFoundException {
        return loymaxUsersRepository
                .getByUserId(localUserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "loymax user not found", 401));
    }

    @Override
    @Transactional
    public LoymaxUser createLoymaxUser(Long userId, LoymaxUserInfo loymaxUserInfo, TokenExchangeResponse response) {
        var loymaxUser = LoymaxUserMapper.INSTANCE.mapLoymaxUserWithIdAndToken(userId, loymaxUserInfo, response);
        loymaxUser.setSetMobileApplicationInstalled(true);
        setMobile(loymaxUser);
        return saveOrUpdate(loymaxUser);
    }

    @Override
    @Transactional
    public LoymaxUser createLoymaxUser(Long userId, LoymaxUserInfo loymaxUserInfo) {
        var loymaxUser = LoymaxUserMapper.INSTANCE.mapLoymaxUserWithId(userId, loymaxUserInfo);
        loymaxUser.setSetMobileApplicationInstalled(true);
        return saveOrUpdate(loymaxUser);
    }

    /**
     * @param phoneNumber - user phone number
     * @param userId      - local user id
     */
    @Override
    public void beginLoymaxRegistration(String phoneNumber, Long userId) {
        var loymaxToken = loymaxRestClient.beginRegistration(phoneNumber);
        var userInfo = getUserInfo(loymaxToken.getAccessToken());
        var u = createLoymaxUser(userId, userInfo, loymaxToken);
        saveOrUpdate(u);
        String accessToken = loymaxToken.getAccessToken();
        // make sure registration is started
        var actions = loymaxRestClient.getLoymaxUserActions(accessToken);
        loymaxRestClient.acceptTenderOffer(accessToken);
        actions = loymaxRestClient.getLoymaxUserActions(accessToken);
        actions.stream().filter(x -> x.getUserActionType().equals("AcceptTenderOffer")
                        && x.getIsDone().equals(true)).findFirst()
                .orElseThrow(() -> new LoymaxException("Exception with registration, tender offer not accepted"));
    }

    @Override
    @RefreshToken
    public void resendRegistrationConfirmCode(LoymaxUser loymaxUser) {
        loymaxRestClient.sendConfirmCode(loymaxUser.getToken());
    }

    /**
     * @param userId - local user id
     */
    @Override
    public void registrationSetPasswordLoymax(RegistrationSetPasswordRequest setPasswordRequest, Long userId) {
        var loymaxUser = getByLocalUserId(userId);
        refreshTokenIfNeeded(loymaxUser);
        var accessToken = loymaxRestClient.createLoymaxQuestionRequest(
                loymaxConstants.registrationSetPassword,
                loymaxUser.getToken(), setPasswordRequest);
        loymaxUser.setToken(accessToken);
        updateLoymaxUser(loymaxUser);
    }

    @Override
    public List<LoymaxCounterResponse> counterInfo(Long counterId, Long personId) {
        var loymaxSettings = loymaxSettingsService.get();
        var token = exchangeForTokenAdmin(loymaxSettings.getUsername(), loymaxSettings.getPassword());
        return loymaxRestClient.counterHandler(counterId, personId, token.getAccessToken());
    }

    @Override
    public List<LoymaxCheckItem> loadChecksForUser(Long userId) {
        var loymaxUser = getByLocalUserId(userId);
        var loymaxSettings = loymaxSettingsService.get();
        var token = exchangeForTokenAdmin(loymaxSettings.getUsername(), loymaxSettings.getPassword());
        return getChecksListAdmin(token.getAccessToken(), loymaxUser.getLoymaxUserId());
    }

    @Override
    @RefreshToken
    public LoymaxUser sendUserUpdateRequest(UserProfileUpdateRequest userDto, LoymaxUser loymaxUser, String move) {
        String formattedBD = null;

        if (userDto.getBirthday() != null) {
            formattedBD = new java.text.SimpleDateFormat(LoymaxConstants.LOYMAX_TIME_PATTERN)
                    .format(new java.util.Date(userDto.getBirthday() * 1000));
        }
        long genderId = 0L;
        if (userDto.getGender().startsWith("male"))
            genderId = 2L;
        else if (userDto.getGender().startsWith("female"))
            genderId = 1L;

        var loymaxQuestionDtoList = new ArrayList<>(List.of(
                LoymaxQuestionDto.builder()
                        .questionId(4)
                        .questionGroupId(1)
                        .value(userDto.getFirstName()).build(),

                LoymaxQuestionDto.builder()
                        .questionId(5)
                        .questionGroupId(1)
                        .value(userDto.getLastName()).build(),

                LoymaxQuestionDto.builder()
                        .questionId(14)
                        .questionGroupId(1)
                        .fixedAnswerIds(List.of(genderId))
                        .build()
        ));

        if (move.equals("registration")) {
            loymaxQuestionDtoList.add(LoymaxQuestionDto.builder()
                    .questionId(13)
                    .questionGroupId(1)
                    .value(formattedBD).build());

            if (userDto.getEmail() != null && !userDto.getEmail().isEmpty())
                loymaxRestClient.updateUserEmail(loymaxUser.getToken(), new EmailDto(userDto.getEmail()));
        } else if (move.equals("update")) {
            if (userDto.getBirthday() != null) {
                List<LoymaxQuestionDto> dateRequests = new ArrayList<>();
                dateRequests.add(LoymaxQuestionDto.builder()
                        .questionId(13)
                        .questionGroupId(1)
                        .value(formattedBD).build());
                try {
                    loymaxRestClient.createLoymaxQuestionRequest(loymaxConstants.registrationUserUpdate,
                            loymaxUser.getToken(), dateRequests);
                } catch (LoymaxException e) {
                    throw new LoymaxException("Изменение даты рождения недоступно");
                }
            }
            if (userDto.getEmail() != null && !userDto.getEmail().isEmpty())
                loymaxRestClient.updateUserEmail(loymaxUser.getToken(), new EmailDto(userDto.getEmail()));
            // here loymax returns null token, care !!
        }

        var accessToken = loymaxRestClient.createLoymaxQuestionRequest(loymaxConstants.registrationUserUpdate,
                loymaxUser.getToken(), loymaxQuestionDtoList);

        var userActions = loymaxRestClient.getLoymaxUserActions(loymaxUser.getToken());
        // ensure if profile is saved
        userActions.stream()
                .filter(x -> x.getUserActionType()
                        .equals("AcceptTenderOffer") && x.getIsDone())
                .findFirst()
                .orElseThrow(() -> new LoymaxException("Tender offer not accepted"));
        return loymaxUser;
    }

    /**
     * @param userDto - user profile update request
     * @param userId  - local user id
     */
    @Override
    public void sendRegistrationUserUpdateRequest(UserProfileUpdateRequest userDto, Long userId) {
        var currentLoymaxUser = getByLocalUserId(userId);
        var loymaxUser = sendUserUpdateRequest(userDto, currentLoymaxUser, "registration");
        tryToFinishRegistration(loymaxUser);
    }

    private void tryToFinishRegistration(LoymaxUser loymaxUser) {
        var tokenExchange = loymaxRestClient.tryToFinishRegistration(loymaxUser.getToken());
        addTokensForUserAndDoUpdate(loymaxUser, tokenExchange);
    }

    @Override
    public void recoverPassword(RecoveryPasswordRequestDto recoveryPasswordRequestDto) {
        loymaxRestClient.recoverPassword(recoveryPasswordRequestDto);
    }

    @Override
    public TokenExchangeResponse resetPassword(LoymaxResetPasswordRequest resetPasswordRequest) {
        return loymaxRestClient.resetPassword(resetPasswordRequest);
    }

    @Override
    @RefreshToken
    public void changePassword(LoymaxUser loymaxUser, ChangePasswordRequestDto changePasswordRequestDto) {
        var resp = loymaxRestClient.changePassword(loymaxUser.getToken(), changePasswordRequestDto);
        addTokensForUserAndDoUpdate(loymaxUser, resp);
    }

    @Override
    public List<LoymaxOffer> getOffers(String token, String[] params) {
        return loymaxRestClient.getOffers(token, params);
    }

    @Override
    public List<LoymaxOffer> getOffers(String[] params) {
        return loymaxRestClient.getOffers(params);
    }

    @Override
    @RefreshToken
    public void deregistrationBegin(LoymaxUser loymaxUser) {
        loymaxRestClient.deregistrationBegin(loymaxUser.getToken());
    }

    @Override
    @RefreshToken
    public void deregistrationConfirm(LoymaxUser loymaxUser, DeregistrationConfirmDto dto) {
        loymaxRestClient.deregistrationConfirm(loymaxUser.getToken(), dto);
    }

    @Override
    @RefreshToken
    public void userSetSocial(String socialName, LoymaxUser loymaxUser, SocialSetDto socialSetDto) {
        var props = loymaxSocials.getSocialProperties(socialName);
        loymaxRestClient.userSetSocial(props, loymaxUser.getToken(), socialSetDto);
    }

    @Override
    @RefreshToken
    public void userRemoveSocial(LoymaxUser loymaxUser, String socialName) {
        var props = loymaxSocials.getSocialProperties(socialName);
        loymaxRestClient.userRemoveSocial(props, loymaxUser.getToken());
    }

    @Override
    public List<PromotionsDto> getPromotions(Integer offset, Integer limit) {
        return loymaxRestClient.getPromotions(offset, limit);
    }


    @Override
    public void refreshTokenIfNeeded(LoymaxUser loymaxUser) {
        if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - 2 >= loymaxUser.getExpiresAt()) {
            log.info("Loymax token expired. Send refresh token request");
            forceRefreshToken(loymaxUser);
        }
    }

    @Override
    public void forceRefreshToken(LoymaxUser loymaxUser) {
        var tokenExchangeResponse = loymaxRestClient.refreshToken(
                loymaxUser.getRefreshToken(),
                loymaxUser.getToken());
        addTokensForUserAndDoUpdate(loymaxUser, tokenExchangeResponse);
    }

    @Override
    public void addTokensForUserAndDoUpdate(LoymaxUser loymaxUser, TokenExchangeResponse resp) {
        loymaxUser.setToken(resp.getAccessToken());
        loymaxUser.setExpiresAt(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + resp.getExpiresIn());
        loymaxUser.setRefreshToken(resp.getRefreshToken());
        loymaxUser.setSetMobileApplicationInstalled(true);
        saveOrUpdate(loymaxUser);
    }

    @Override
    @RefreshToken
    public void updateUserEmail(EmailDto emailDto, LoymaxUser loymaxUser) {
        loymaxRestClient.updateUserEmail(loymaxUser.getToken(), emailDto);
    }

    @Override
    @RefreshToken
    public void confirmUserEmail(LoymaxUser loymaxUser, LoymaxConfirmCodeDto codeDto) {
        loymaxRestClient.confirmUserEmail(loymaxUser.getToken(), codeDto);
    }

    @Override
    @RefreshToken
    public void sendEmailConfirmCode(LoymaxUser loymaxUser) {
        loymaxRestClient.sendEmailConfirmCode(loymaxUser.getToken());
    }

    @Override
    @RefreshToken
    public void cancelEmailProcessing(LoymaxUser loymaxUser) {
        loymaxRestClient.cancelEmailProcessing(loymaxUser.getToken());
    }

    @Override
    @RefreshToken
    public void updateUserPhone(LoymaxUser loymaxUser, PhoneDto phoneDto) {
        loymaxRestClient.updateUserPhone(phoneDto, loymaxUser.getToken());
    }

    @Override
    @RefreshToken
    public void confirmUserPhone(LoymaxUser loymaxUser, LoymaxConfirmCodeDto codeDto) {
        TokenExchangeResponse tokenExchangeResponse = loymaxRestClient.confirmUserPhone(loymaxUser.getToken(), codeDto);
        loymaxUser.setToken(tokenExchangeResponse.getAccessToken());
        loymaxUser.setRefreshToken(tokenExchangeResponse.getRefreshToken());
        loymaxUser.setExpiresAt(tokenExchangeResponse.getExpiresIn());
        updateLoymaxUser(loymaxUser);
    }

    @Override
    @RefreshToken
    public void sendPhoneConfirmCode(LoymaxUser loymaxUser) {
        loymaxRestClient.sendPhoneConfirmCode(loymaxUser.getToken());
    }

    @Override
    @RefreshToken
    public List<Social> getSocialsBindings(LoymaxUser loymaxUser) {
        var socialsBindings = loymaxRestClient
                .getSocialBindings(loymaxUser.getToken());
        for (SocialIdentifiers x : socialsBindings.getSocialIdentifiers()) {
            switch (x.getProvider()) {
                case "VKontakte":
                    x.setProvider("vk");
                    break;
                case "Odnoklassniki":
                    x.setProvider("odnoklassniki");
                    break;
                case "Apple":
                    x.setProvider("apple");
                    break;
            }
        }

        socialsBindings.getSocialIdentifiers().forEach(x ->
                loymaxUsersSocialsService.bindLoymaxSocialToUser(loymaxUser.getUserId(),
                        x.getUserId(), SocialName.of(x.getProvider())
                                .orElseThrow()));

        return socialsBindings
                .getSocialIdentifiers()
                .stream()
                .map(x -> x.getProvider().toLowerCase(Locale.ROOT))
                .map(x -> loymaxSocials.getSocialProperties(x).getSocialEntity())
                .collect(Collectors.toList());
    }

    @Override
    @RefreshToken
    public List<LoymaxUserAction> getUserActions(LoymaxUser loymaxUser) {
        return loymaxRestClient.getLoymaxUserActions(loymaxUser.getToken());
    }

    @Override
    @RefreshToken
    @CacheEvict(cacheNames = CARDS_CACHE, key = "#loymaxUser.getUserId()")
    public void attachUserCard(LoymaxUser loymaxUser, LoymaxUserCardRequestDto userCardDto) {
        loymaxRestClient.attachUserCard(loymaxUser.getToken(), userCardDto);
    }

    @Override
    @RefreshToken
    @CacheEvict(cacheNames = CARDS_CACHE, key = "#loymaxUser.getUserId()")
    public UserCardDto createUserCard(LoymaxUser loymaxUser, LoymaxUserCardRequestDto cardNumberRequestDto) {
        var status = loymaxRestClient.getCreateLoymaxUserCard(
                loymaxUser.getToken(), cardNumberRequestDto);
        if (!status.getIsCardSetAllowed())
            throw new LoymaxException("Card creation not allowed");
        loymaxRestClient.createLoymaxUserCard(
                loymaxUser.getToken(), cardNumberRequestDto);
        var cards = loymaxRestClient.selectCards(loymaxUser.getToken());
        var createdCard = cards.stream()
                .filter(x -> x.getNumber().equals(cardNumberRequestDto.getCardNumber()))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("Card did not created (no cards present after selecting)"));
        return createUserCardFromLoymaxCard(createdCard, loymaxUser.getPersonUid());
    }

    @Override
    @RefreshToken
    @CacheEvict(cacheNames = CARDS_CACHE, key = "#loymaxUser.getUserId()")
    public UserCardDto emitVirtualCard(LoymaxUser loymaxUser) {
        var token = loymaxUser.getToken();
        LoymaxUserVirtualCardEmitInfo emitInfo = loymaxRestClient.getEmitInfo(token);
        if (!emitInfo.getIsVirtualCardEmissionAllowed()) {
            throw new LoymaxException("Вы уже имеете виртуальную карту");
        }
        loymaxRestClient.emitVirtual(token);
        List<LoymaxUserCardDto> userCards = loymaxRestClient.selectCards(token);
        var loymaxVirtualCard = userCards.stream()
                .filter(x -> x.getCardCategory().getId().equals(1L))
                .findFirst();
        if (loymaxVirtualCard.isPresent())
            return createUserCardFromLoymaxCard(loymaxVirtualCard.get(), loymaxUser.getPersonUid());
        throw new LoymaxException("Loymax did not respond with virtual card data");
    }

    @Override
    @RefreshToken
    @CacheEvict(cacheNames = CARDS_CACHE, key = "#loymaxUser.getUserId()")
    public UserCardDto attachConfirm(LoymaxUser loymaxUser, CodeDto codeDto) {
        var response = loymaxRestClient.checkCurrentCardStatus(loymaxUser.getToken());
        loymaxRestClient.confirmAttachUserCard(loymaxUser.getToken(), codeDto.getCode());
        var cards = loymaxRestClient.selectCards(loymaxUser.getToken());
        var loymaxCard = cards.stream().filter(x -> x.getNumber().equals(response.getCardNumber())).findFirst()
                .orElseThrow(() -> new LoymaxException("No attached card present"));
        return createUserCardFromLoymaxCard(loymaxCard, loymaxUser.getPersonUid());
    }

    public UserAccounts mapLoymaxUserAccToSparUserAcc(LoymaxUserBalanceInfoDto x) {
        double scale = Math.pow(10, 2);
        var userAcc = new UserAccounts();
        userAcc.setAmount(Math.ceil(x.getAmount() * scale) / scale);
        Currency currency = new Currency();
        LoymaxCurrency loymaxCurr = x.getCurrency();
        currency.setDescription(loymaxCurr.getDescription());
        currency.setIsDeleted(currency.getIsDeleted());
        currency.setId(loymaxCurr.getId());
        currency.setName(loymaxCurr.getName());
        currency.setIsDeleted(loymaxCurr.getIsDeleted());
        currency.setExternalId(loymaxCurr.getUid());
        NameCases nameCases = new NameCases();
        LoymaxNameCases loymaxNameCases = loymaxCurr.getNameCases();
        nameCases.setAbbreviation(loymaxNameCases.getAbbreviation());
        nameCases.setGenitive(loymaxNameCases.getGenitive());
        nameCases.setNominative(loymaxNameCases.getNominative());
        nameCases.setPlural(loymaxNameCases.getPlural());
        currency.setNameCases(nameCases);
        userAcc.setCurrency(currency);
        var lifTimes = x.getLifeTimesByTime()
                .stream().map(i -> {
                    AccountsLifeTimesByTimeDTO dto = new AccountsLifeTimesByTimeDTO();
                    dto.setAmount(i.getAmount());
                    dto.setDate(LoymaxTimeToSparTimeAdapter.convertToEpochSeconds(i.getDate()));
                    return dto;
                }).collect(Collectors.toList());
        userAcc.setAccountLifeTimesByTime(lifTimes);
        return userAcc;
    }

    @Override
    @RefreshToken
    public List<UserAccounts> getDetailedBalanceInfo(LoymaxUser loymaxUser) {
        var result = loymaxRestClient.getDetailedBalanceInfo(loymaxUser.getToken());
        return result.stream().map(this::mapLoymaxUserAccToSparUserAcc).collect(Collectors.toList());
    }


    @Override
    @RefreshToken
    public LoymaxUserFavoriteCategories selectUserFavoriteCategories(LoymaxUser loymaxUser) {
        return loymaxRestClient
                .selectFavoriteCategories(loymaxUser.getToken());
    }

    @Override
    public LoymaxUserFavoriteCategories getUserFavoriteCategories(LoymaxUser loymaxUser) {
        return selectUserFavoriteCategories(loymaxUser);
    }

    @Override
    @RefreshToken
    public List<CategoryDto> selectFavoriteCategories(LoymaxUser loymaxUser) {
        LoymaxUserFavoriteCategories loymaxUserFavoriteCategories = getUserFavoriteCategories(loymaxUser);
        if (loymaxUserFavoriteCategories == null) {
            return null;
        }
        return loymaxUserFavoriteCategories
                .getCategories()
                .stream().map(x -> {
                    var categoryDto = new CategoryDto();
                    categoryDto.setId(Long.parseLong(x.getId()));
                    categoryDto.setName(x.getName());
                    categoryDto.setAccepted(x.getAccepted());
                    categoryDto.setPreferenceType(x.getPreferenceType());
                    categoryDto.setGoodsGroupUID(x.getGoodsGroupUID());
                    categoryDto.setPreferenceValue(x.getPreferenceValue());
                    categoryDto.setStartActiveDate(LoymaxTimeToSparTimeAdapter
                            .convertToEpochSeconds(x.getStartActiveDate()));
                    categoryDto.setEndActiveDate(LoymaxTimeToSparTimeAdapter
                            .convertToEpochSeconds(x.getEndActiveDate()));
                    categoryDto.setIsPublic(false);
                    // TODO file service
                    var fileDto = new FileDto();
                    return categoryDto;
                }).collect(Collectors.toList());
    }

    @Override
    public List<CheckDto> selectUserChecks(LoymaxUser loymaxUser) {
        LoymaxCheck check = loymaxRestClient.selectUserChecks(loymaxUser.getToken());
        return List.of();
    }

    @Override
    @Cacheable(cacheNames = STATUS_CACHE, key = "#loymaxUser.getUserId()")
    public LoymaxUserStatus selectStatus(LoymaxUser loymaxUser) {
        var loymaxStatus = loymaxRestClient.selectUserStatus(loymaxUser.getToken());
        if (loymaxStatus != null && loymaxStatus.isEmpty())
            throw new LoymaxException("No user status present");
        return loymaxStatus.get(0);
    }

    @Override
    @RefreshToken
    @CacheEvict(cacheNames = CARDS_CACHE, key = "#loymaxUser.getUserId()")
    public UserCardDto replaceUserCard(LoymaxUser loymaxUser,
                                       LoymaxUserCardRequestDto loymaxCardRequest,
                                       Long loymaxCardId) {
        loymaxRestClient.replaceUserCard(loymaxUser.getToken(), loymaxCardRequest, String.valueOf(loymaxCardId));
        var cards = loymaxRestClient.selectCards(loymaxUser.getToken());
        log.info(cards.toString());
        var replacedCard = cards.stream()
                .filter(x -> x.getNumber().equals(loymaxCardRequest.getCardNumber()))
                .findFirst()
                .orElseThrow(() -> new ValidationException("No card present after replacing"));
        return createUserCardFromLoymaxCard(replacedCard, loymaxUser.getPersonUid());
    }

    @Override
    @RefreshToken
    @CacheEvict(cacheNames = CARDS_CACHE, key = "#loymaxUser.getUserId()")
    public UserCardDto changeCardBlockState(LoymaxUser loymaxUser,
                                            CardPasswordDto cardPasswordDto,
                                            Long loymaxCardId) {
        loymaxRestClient.changeBlockState(loymaxUser.getToken(), String.valueOf(loymaxCardId), cardPasswordDto);
        var cards = loymaxRestClient.selectCards(loymaxUser.getToken());
        var replacedCard = cards.stream()
                .filter(x -> x.getId().equals(loymaxCardId))
                .findFirst()
                .orElseThrow(() -> new ValidationException("No card present after replacing"));
        return createUserCardFromLoymaxCard(replacedCard, loymaxUser.getPersonUid());
    }

    @Override
    @RefreshToken
    public void attachSendConfirmCode(LoymaxUser loymaxUser) {
        loymaxRestClient.attachSendConfirmCode(loymaxUser.getToken());
    }

    @Override
    public UserCardDto createUserCardFromLoymaxCard(LoymaxUserCardDto loymaxUserCardDto,
                                                    String loymaxPersonUid) {
        var userCardDto = new UserCardDto();
        ReflectUtils.updateAllFields(loymaxUserCardDto, userCardDto);
        userCardDto.setImOwner(Objects.equals(loymaxPersonUid,
                loymaxUserCardDto.getCardOwnerInfo().getPersonUid()));
        userCardDto.setCardType(loymaxUserCardDto.getCardCategory().getLogicalName());
        return userCardDto;
    }

    @Override
    @RefreshToken
    @Cacheable(cacheNames = CARDS_CACHE, key = "#loymaxUser.getUserId()")
    public List<UserCardDto> selectCards(LoymaxUser loymaxUser) {
        var list = loymaxRestClient.selectCards(loymaxUser.getToken());
        return list.stream()
                .map(x -> createUserCardFromLoymaxCard(
                        x, loymaxUser.getPersonUid()))
                .collect(Collectors.toList());
    }

    @Override
    public CardQr getQrCode(LoymaxUser loymaxUser, Long cardId) {
        var qr = loymaxRestClient.getQrCode(loymaxUser.getToken(), String.valueOf(cardId));
        var cardQr = new CardQr();
        cardQr.setCode(qr.getCode());
        cardQr.setCodeGeneratedDate(LoymaxTimeToSparTimeAdapter.convertToEpochSeconds(qr.getCodeGeneratedDate()));
        cardQr.setLifeTime(Long.valueOf(qr.getLifeTime()));
        return cardQr;
    }

    @Override
    public List<LoymaxCouponsDto> getCouponsList(LoymaxUser loymaxUser) {
        return loymaxRestClient.getCouponsList(loymaxUser.getToken());
    }

    @Override
    @RefreshToken
    @Cacheable(cacheNames = ACCOUNT_CACHE, key = "#loymaxUser.getUserId()")
    public List<LoymaxUserBalanceInfoDto> getDetailedBalance(LoymaxUser loymaxUser) {
        return loymaxRestClient.getDetailedBalanceInfo(loymaxUser.getToken());
    }

    @Override
    public List<String> getLogicalName(LoymaxUser loymaxUser) {
        return loymaxRestClient.getAttributeList(loymaxUser.getToken())
                .stream()
                .map(x -> x.getInfo().getLogicalName())
                .collect(Collectors.toList());
    }

    @Override
    public ObjectNode getDateFromAttribute(LoymaxUser loymaxUser, String logicalName) {
        return loymaxRestClient.getClientPersonalOffers(loymaxUser.getToken(), logicalName);
    }

    @Override
    @RefreshToken
    @Cacheable(cacheNames = PERSONAL_GOODS_CACHE, key = "#loymaxUser.getUserId() + #logicalName")
    public LoymaxPersonalGoodsResponseDto getPersonalGoods(LoymaxUser loymaxUser, String logicalName) {
        var loymaxGoods = loymaxRestClient.getClientPersonalGoods(loymaxUser.getToken(), logicalName);
        if (loymaxGoods == null)
            throw new RuntimeException("No personal goods response from loymax");
        return loymaxGoods;
    }

    @Override
    @CacheEvict(cacheNames = PERSONAL_GOODS_CACHE, allEntries = true)
    public void acceptToGoods(LoymaxUser loymaxUser, String brandId, String goodsId) {
        LoymaxAcceptGoodsRequest loymaxAcceptGoodsRequest = new LoymaxAcceptGoodsRequest();
        loymaxAcceptGoodsRequest.setValue(true);
        loymaxAcceptGoodsRequest.setXPath("Preferences[?(@.BrandId == '" + brandId + "')].Items[?(@.GoodsId == '" + goodsId + "')].Accepted");
        loymaxRestClient.acceptedToGoods(loymaxUser.getToken(), loymaxAcceptGoodsRequest);
    }

    @Override
    @RefreshToken
    @Cacheable(cacheNames = CHECK_CACHE, key = "{#loymaxUser.getUserId(), #cardId, #offset, #limit, #dateStart, #dateEnd}")
    public List<LoymaxCheckItem> getChecksList(LoymaxUser loymaxUser, Long cardId, Integer offset,
                                               Integer limit, Long dateStart, Long dateEnd) {

        String request = loymaxConstants.userChecks + "?filter.historyItemType=Purchase"
                + PathVariableUtils.replacePathVariable("&filter.from={offset}", String.valueOf(offset))
                + PathVariableUtils.replacePathVariable("&filter.count={limit}", String.valueOf(limit));

        if (dateStart != 0L && dateEnd != 0L) {
            var start = LoymaxTimeToSparTimeAdapter.convertToUTC(dateStart);
            var end = LoymaxTimeToSparTimeAdapter.convertToUTC(dateEnd);
            request += PathVariableUtils.replacePathVariable("&filter.fromDate={start}", start)
                    + PathVariableUtils.replacePathVariable("&filter.toDate={end}", end);
        } else {
            request += PathVariableUtils.replacePathVariable("&filter.fromDate={start}",
                    LoymaxTimeToSparTimeAdapter.convertToUTC(LoymaxConstants.LOYMAX_DEAFULT_START_DATE));
        }

        if (cardId != null && cardId != 0) {
            request += PathVariableUtils.replacePathVariable("&filter.cardId={cardId}", String.valueOf(cardId));
        }

        return loymaxRestClient.selectUserChecksWithProperty(loymaxUser.getToken(), request).getRows();
    }

    @Override
    public void acceptToFavoriteCategory(LoymaxUser loymaxUser, List<String> idsList) {
        var ids = new LoymaxAcceptCategoryRequest();
        ids.setIds(idsList);
        loymaxRestClient.selectFavoriteGoods(loymaxUser.getToken(), ids);
    }

    @Override
    public void changeRejectPaperChecks(Boolean change, LoymaxUser loymaxUser) {
        List<LoymaxQuestionDto> loymaxQuestionDtoList = List.of(
                LoymaxQuestionDto.builder()
                        .questionId(64)
                        .questionGroupId(2)
                        .value(change.toString()).build());
        loymaxRestClient.createLoymaxQuestionRequest(loymaxConstants.registrationUserUpdate,
                loymaxUser.getToken(), loymaxQuestionDtoList);
    }

    @Override
    public String getPhoneNumber(String token) {
        var loymaxInfo = loymaxRestClient.getSocialBindings(token);
        List<String> phonelList = new ArrayList<>();
        for (UserSocialIdentifier x : loymaxInfo.getIdentifiers()) {
            if (x.getValue().chars().allMatch(Character::isDigit) && x.getValue().length() == 11) {
                phonelList.add(x.getValue());
            }
        }
        if (!phonelList.isEmpty()) return phonelList.get(phonelList.size() - 1);
        return null;
    }

    @Override
    public void setMobile(LoymaxUser loymaxUser) {
        LoymaxAcceptGoodsRequest loymaxAcceptGoodsRequest = new LoymaxAcceptGoodsRequest();
        loymaxAcceptGoodsRequest.setValue(true);
        loymaxAcceptGoodsRequest.setXPath("");
        loymaxRestClient.mobileApplicationInstalled(loymaxUser.getToken(), loymaxAcceptGoodsRequest);
    }

    @Override
    @Cacheable(cacheNames = ADMIN_TOKEN, key = "{#userData, #secret}")
    public TokenExchangeResponse exchangeForTokenAdmin(String userData, String secret) {
        return loymaxRestClient.exchangeForTokenAdmin(userData, secret);
    }

    @Override
    @Cacheable(cacheNames = ADMIN_TOKEN)
    public TokenExchangeResponse exchangeForTokenAdmin() {
        var settings = loymaxSettingsService.get();
        return loymaxRestClient.exchangeForTokenAdmin(settings.getUsername(), settings.getPassword());
    }

    @Override
    public List<LoymaxCheckItem> getChecksListAdmin(String token, Long personId) {
        String request = PathVariableUtils.replacePathVariable(
                loymaxConstants.userChecksAdmin, String.valueOf(personId)) + "?filter.historyItemType=Purchase";
        var start = LoymaxTimeToSparTimeAdapter.convertToUTC(1640998902);
        var end = LoymaxTimeToSparTimeAdapter.convertToUTC(TimeHelper.currentTime());
        request += PathVariableUtils.replacePathVariable("&filter.fromDate={start}", start)
                + PathVariableUtils.replacePathVariable("&filter.toDate={end}", end);
        Long countChecks = loymaxRestClient.selectUserChecksFromAdmin(token, request).getAllCount();
        if (countChecks > 0) {
            request += PathVariableUtils.replacePathVariable("&filter.count={countChecks}", String.valueOf(countChecks));
            return loymaxRestClient.selectUserChecksFromAdmin(token, request).getRows();
        }
        return new ArrayList<>();
    }

    @Override
    public List<LoymaxUser> fetchAllUsers() {
        return loymaxUsersRepository.fetchAllUsers();
    }

    @Override
    public List<LoymaxUserAttributeDto<String>> importUserAttribute(Long loymaxUserId, String attributeName) {
        return importUserAttribute(exchangeForTokenAdmin().getAccessToken(), loymaxUserId, attributeName);
    }

    @Override
    public List<LoymaxUserAttributeDto<String>> importUserAttribute(String token, Long loymaxUserId, String attributeName) {
        return loymaxRestClient.importUserAttribute(token, loymaxUserId, attributeName);
    }

    @Override
    public List<LoymaxOffer> getOfferById(Long loymaxOfferId) {
        return loymaxRestClient.getOfferById(loymaxOfferId);
    }

    @Override
    public boolean checkIfNumberIsTaken(String phoneNumber) {
        var loymaxUser = getUserInfoSystem(phoneNumber).stream().findAny()
                .orElseThrow(() -> new UserNotFoundException("No loymax user found in loymax: " + phoneNumber));
        return loymaxUser.getPhone() != null &&
                (loymaxUser.getPhone().getCurrentValue() != null);
    }

    @Override
    public List<LoymaxUserInfoSystem> getUserInfoSystem(String phoneNumber) {
        var loymaxSettings = loymaxSettingsService.get();
        var token = exchangeForTokenAdmin(loymaxSettings.getUsername(), loymaxSettings.getPassword());
        return loymaxRestClient.getUserInfo(token.getAccessToken(), phoneNumber);
    }

    @Override
    public LoymaxUserInfoSystem getUserInfoSystemByLoymaxUserId(String loymaxUserId) {
        var loymaxSettings = loymaxSettingsService.get();
        var token = exchangeForTokenAdmin(loymaxSettings.getUsername(), loymaxSettings.getPassword());
        return loymaxRestClient.getUserInfoByUserId(token.getAccessToken(), loymaxUserId);
    }

    @Override
    public List<LoymaxUserQuestionDto> getUserQuestionsSystem(Long id) {
        var loymaxSettings = loymaxSettingsService.get();
        var token = exchangeForTokenAdmin(loymaxSettings.getUsername(), loymaxSettings.getPassword());
        return loymaxRestClient.getLoymaxUserRegistrationActionsSystem(token.getAccessToken(), id.toString());
    }


}