package ru.sparural.engine.loymax.services.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.CodeDto;
import ru.sparural.engine.api.dto.EmailDto;
import ru.sparural.engine.api.dto.PhoneDto;
import ru.sparural.engine.api.dto.RecoveryPasswordRequestDto;
import ru.sparural.engine.api.dto.cards.CardPasswordDto;
import ru.sparural.engine.api.dto.cards.CardQrDto;
import ru.sparural.engine.api.dto.cards.UserCardDto;
import ru.sparural.engine.api.dto.check.CheckDto;
import ru.sparural.engine.api.dto.goods.PersonalGoodsDto;
import ru.sparural.engine.api.dto.main.CategoryDto;
import ru.sparural.engine.api.dto.promotions.PromotionsDto;
import ru.sparural.engine.api.dto.registration.ConfirmRegistrationRequest;
import ru.sparural.engine.api.dto.registration.RegistrationSetPasswordRequest;
import ru.sparural.engine.api.dto.registration.UserProfileUpdateRequest;
import ru.sparural.engine.api.dto.socials.SocialSetDto;
import ru.sparural.engine.api.dto.user.ChangePasswordRequestDto;
import ru.sparural.engine.api.dto.user.account.UserAccounts;
import ru.sparural.engine.entity.LoymaxCard;
import ru.sparural.engine.entity.LoymaxUser;
import ru.sparural.engine.entity.Registrations;
import ru.sparural.engine.entity.Social;
import ru.sparural.engine.loymax.exceptions.LoymaxUnauthorizedException;
import ru.sparural.engine.loymax.rest.dto.LoymaxConfirmCodeDto;
import ru.sparural.engine.loymax.rest.dto.LoymaxCouponsDto;
import ru.sparural.engine.loymax.rest.dto.TokenExchangeResponse;
import ru.sparural.engine.loymax.rest.dto.account.LoymaxUserBalanceInfoDto;
import ru.sparural.engine.loymax.rest.dto.attribute.LoymaxAttributesDto;
import ru.sparural.engine.loymax.rest.dto.cards.LoymaxUserCardDto;
import ru.sparural.engine.loymax.rest.dto.cards.LoymaxUserCardRequestDto;
import ru.sparural.engine.loymax.rest.dto.categories.LoymaxUserFavoriteCategories;
import ru.sparural.engine.loymax.rest.dto.check.LoymaxCheckItem;
import ru.sparural.engine.loymax.rest.dto.counter.LoymaxCounterResponse;
import ru.sparural.engine.loymax.rest.dto.offer.LoymaxOffer;
import ru.sparural.engine.loymax.rest.dto.password.LoymaxResetPasswordRequest;
import ru.sparural.engine.loymax.rest.dto.registration.DeregistrationConfirmDto;
import ru.sparural.engine.loymax.rest.dto.status.LoymaxUserStatus;
import ru.sparural.engine.loymax.rest.dto.user.LoymaxUserAction;
import ru.sparural.engine.loymax.rest.dto.user.LoymaxUserInfo;
import ru.sparural.engine.loymax.rest.dto.user.login.LoginTwoFAResponse;
import ru.sparural.engine.loymax.services.LoymaxService;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@Primary
@Slf4j
public class RetryingLoymaxServiceWrapper implements LoymaxService {

    private LoymaxService loymaxService;

    @Autowired
    @Qualifier("loymaxServiceImpl")
    public void setLoymaxService(LoymaxService loymaxService) {
        this.loymaxService = loymaxService;
    }

    // retrying with refresh
    private Object retryIfTokenExpired(LoymaxUser loymaxUser, ResultRunnable runnable) {
        try {
            return runnable.run();
        } catch (LoymaxUnauthorizedException loymaxBadCredentialException) {
            logRetrying(loymaxBadCredentialException);
            loymaxService.forceRefreshToken(loymaxUser);
            return runnable.run();
        }
    }

    private void retryIfTokenExpired(LoymaxUser loymaxUser, Runnable runnable) {
        try {
            runnable.run();
        } catch (LoymaxUnauthorizedException loymaxBadCredentialException) {
            logRetrying(loymaxBadCredentialException);
            loymaxService.forceRefreshToken(loymaxUser);
            runnable.run();
        }
    }

    private void logRetrying(Exception e) {
        log.info("Retry loymax request because loymax respond with unauthorized exception, stack trace: " +
                (e.getStackTrace().length != 0 ? e.getStackTrace()[0] : ""));
    }

    @Override
    public TokenExchangeResponse exchangeForToken(String userData, String secret) {
        return loymaxService.exchangeForToken(userData, secret);
    }

    @Override
    public LoymaxUser updateLoymaxUser(LoymaxUser loymaxUser) {
        return loymaxService.updateLoymaxUser(loymaxUser);
    }

    @Override
    public LoymaxUser saveOrUpdate(LoymaxUser loymaxUser) {
        return loymaxService.saveOrUpdate(loymaxUser);
    }

    @Override
    public LoymaxUser findByPersonUid(String personUid) {
        return loymaxService.findByPersonUid(personUid);
    }

    @Override
    public LoymaxUserInfo getUserInfo(LoymaxUser loymaxUser) {
        return (LoymaxUserInfo) retryIfTokenExpired(
                loymaxUser, () -> loymaxService.getUserInfo(loymaxUser));
    }

    @Override
    public TokenExchangeResponse loginViaSocial(String secret, String socialName) {
        return loymaxService.loginViaSocial(secret, socialName);
    }

    @Override
    public LoymaxUser getByLocalUserId(Long localUserId) {
        return loymaxService.getByLocalUserId(localUserId);
    }

    @Override
    public LoymaxUser createLoymaxUser(Long userId, String loymaxPersonUid, TokenExchangeResponse response) {
        return loymaxService.createLoymaxUser(userId, loymaxPersonUid, response);
    }

    @Override
    public void beginRegistration(String phoneNumber, Registrations reg, Long userId) {
        loymaxService.beginRegistration(phoneNumber, reg, userId);
    }

    @Override
    public void resendRegistrationConfirmCode(LoymaxUser loymaxUser) {
        loymaxService.resendRegistrationConfirmCode(loymaxUser);
    }

    @Override
    public void confirmRegistration(ConfirmRegistrationRequest confirmRegistrationRequest, Long userId) {
        loymaxService.confirmRegistration(confirmRegistrationRequest, userId);
    }

    @Override
    public void registrationSetPassword(RegistrationSetPasswordRequest setPasswordRequest, Long userId) {
        loymaxService.registrationSetPassword(setPasswordRequest, userId);
    }

    @Override
    public List<LoymaxCounterResponse> counterInfo(Long counterId, Long personId) {
        return loymaxService.counterInfo(counterId, personId);
    }

    @Override
    public List<LoymaxCheckItem> loadChecksForUser(Long userId) {
        return loymaxService.loadChecksForUser(userId);
    }

    @Override
    public LoymaxUser sendUserUpdateRequest(UserProfileUpdateRequest userDto, LoymaxUser loymaxUser, String move) {
        return loymaxService.sendUserUpdateRequest(userDto, loymaxUser, move);
    }

    @Override
    public void sendRegistrationUserUpdateRequest(UserProfileUpdateRequest userDto, Long userId) {
        loymaxService.sendRegistrationUserUpdateRequest(userDto, userId);
    }

    @Override
    public void recoverPassword(RecoveryPasswordRequestDto recoveryPasswordRequestDto) {
        loymaxService.recoverPassword(recoveryPasswordRequestDto);
    }

    @Override
    public TokenExchangeResponse resetPassword(LoymaxResetPasswordRequest resetPasswordRequest) {
        return loymaxService.resetPassword(resetPasswordRequest);
    }

    @Override
    public void changePassword(LoymaxUser loymaxUser, ChangePasswordRequestDto changePasswordRequestDto) {
        retryIfTokenExpired(loymaxUser,
                () -> loymaxService.changePassword(loymaxUser, changePasswordRequestDto));
    }

    @Override
    public List<LoymaxOffer> getOffers(String token, String[] params) {
        return loymaxService.getOffers(token, params);
    }

    @Override
    public List<LoymaxOffer> getOffers(String[] params) {
        return loymaxService.getOffers(params);
    }

    @Override
    public void deregistrationBegin(LoymaxUser loymaxUser) {
        retryIfTokenExpired(loymaxUser,
                () -> loymaxService.deregistrationBegin(loymaxUser));
    }

    @Override
    public void deregistrationConfirm(LoymaxUser loymaxUser, DeregistrationConfirmDto dto) {
        retryIfTokenExpired(loymaxUser,
                () -> loymaxService.deregistrationConfirm(loymaxUser, dto));
    }

    @Override
    public void userSetSocial(String socialName, LoymaxUser loymaxUser, SocialSetDto socialSetDto) {
        retryIfTokenExpired(loymaxUser,
                () -> loymaxService.userSetSocial(socialName, loymaxUser, socialSetDto));
    }

    @Override
    public void userRemoveSocial(LoymaxUser loymaxUser, String socialName) {
        retryIfTokenExpired(loymaxUser,
                () -> loymaxService.userRemoveSocial(loymaxUser, socialName));
    }

    @Override
    public List<PromotionsDto> getPromotions(Integer offset, Integer limit) {
        return loymaxService.getPromotions(offset, limit);
    }

    @Override
    public void refreshTokenIfNeeded(LoymaxUser loymaxUser) {
        loymaxService.refreshTokenIfNeeded(loymaxUser);
    }

    @Override
    public void forceRefreshToken(LoymaxUser loymaxUser) {
        loymaxService.forceRefreshToken(loymaxUser);
    }

    @Override
    public LoginTwoFAResponse exchangeForOneTimePassword(String login) {
        return loymaxService.exchangeForOneTimePassword(login);
    }

    @Override
    public TokenExchangeResponse loginViaOneTimeCode(String token, String code) {
        return loymaxService.loginViaOneTimeCode(token, code);
    }

    @Override
    public void cancelUpdatingPhoneNumber(LoymaxUser loymaxUser) {
        retryIfTokenExpired(loymaxUser,
                () -> loymaxService.cancelUpdatingPhoneNumber(loymaxUser));
    }

    @Override
    public List<LoymaxUser> fetchAllUsers() {
        return loymaxService.fetchAllUsers();
    }

    @Override
    public LoymaxAttributesDto<String> importUserAttribute(Long loymaxUserId, String attributeName) {
        return loymaxService.importUserAttribute(loymaxUserId, attributeName);
    }

    @Override
    public List<LoymaxOffer> getOfferById(Long loymaxOfferId) {
        return loymaxService.getOfferById(loymaxOfferId);
    }

    @Override
    public boolean checkIfNumberIsTaken(String phoneNumber) {
        return loymaxService.checkIfNumberIsTaken(phoneNumber);
    }

    @Override
    public void addTokensForUserAndDoUpdate(LoymaxUser loymaxUser, TokenExchangeResponse resp) {
        loymaxService.addTokensForUserAndDoUpdate(loymaxUser, resp);
    }

    @Override
    public void updateUserEmail(EmailDto emailDto, LoymaxUser loymaxUser) {
        loymaxService.updateUserEmail(emailDto, loymaxUser);
    }

    @Override
    public void confirmUserEmail(LoymaxUser loymaxUser, LoymaxConfirmCodeDto codeDto) {
        loymaxService.confirmUserEmail(loymaxUser, codeDto);
    }

    @Override
    public void sendEmailConfirmCode(LoymaxUser loymaxUser) {
        retryIfTokenExpired(loymaxUser,
                () -> loymaxService.sendEmailConfirmCode(loymaxUser));
    }

    @Override
    public void cancelEmailProcessing(LoymaxUser loymaxUser) {
        retryIfTokenExpired(loymaxUser,
                () -> loymaxService.cancelEmailProcessing(loymaxUser));
    }

    @Override
    public void updateUserPhone(LoymaxUser loymaxUser, PhoneDto phoneDto) {
        retryIfTokenExpired(loymaxUser,
                () -> loymaxService.updateUserPhone(loymaxUser, phoneDto));

    }

    @Override
    public void confirmUserPhone(LoymaxUser loymaxUser, LoymaxConfirmCodeDto codeDto) {
        retryIfTokenExpired(loymaxUser,
                () -> loymaxService.confirmUserPhone(loymaxUser, codeDto));

    }

    @Override
    public void sendPhoneConfirmCode(LoymaxUser loymaxUser) {
        retryIfTokenExpired(loymaxUser,
                () -> loymaxService.sendPhoneConfirmCode(loymaxUser));

    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Social> getSocialsBindings(LoymaxUser loymaxUser) {
        return (List<Social>) retryIfTokenExpired(loymaxUser,
                () -> loymaxService.getSocialsBindings(loymaxUser));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<LoymaxUserAction> getUserActions(LoymaxUser loymaxUser) {
        return (List<LoymaxUserAction>) retryIfTokenExpired(loymaxUser,
                () -> loymaxService.getUserActions(loymaxUser));
    }

    @Override
    public void attachSendConfirmCode(LoymaxUser loymaxUser) {
        retryIfTokenExpired(loymaxUser,
                () -> loymaxService.attachSendConfirmCode(loymaxUser));

    }

    @Override
    public UserCardDto createUserCardFromLoymaxCard(LoymaxUserCardDto loymaxUserCardDto, String loymaxUserId) {
        return loymaxService.createUserCardFromLoymaxCard(loymaxUserCardDto, loymaxUserId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UserCardDto> selectCards(LoymaxUser loymaxUser) {
        return (List<UserCardDto>) retryIfTokenExpired(loymaxUser,
                () -> loymaxService.selectCards(loymaxUser));
    }

    @Override
    public void attachUserCard(LoymaxUser loymaxUser, LoymaxUserCardRequestDto userCardDto) {
        retryIfTokenExpired(loymaxUser,
                () -> loymaxService.attachUserCard(loymaxUser, userCardDto));

    }

    @Override
    public UserCardDto createUserCard(LoymaxUser loymaxUser, LoymaxUserCardRequestDto cardNumberRequestDto) {
        return (UserCardDto) retryIfTokenExpired(loymaxUser,
                () -> loymaxService.createUserCard(loymaxUser, cardNumberRequestDto));
    }

    @Override
    public UserCardDto emitVirtualCard(LoymaxUser loymaxUser) {
        return (UserCardDto) retryIfTokenExpired(loymaxUser,
                () -> loymaxService.emitVirtualCard(loymaxUser));
    }

    @Override
    public UserCardDto attachConfirm(LoymaxUser loymaxUser, CodeDto codeDto) {
        return (UserCardDto) retryIfTokenExpired(loymaxUser,
                () -> loymaxService.attachConfirm(loymaxUser, codeDto));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UserAccounts> getDetailedBalanceInfo(LoymaxUser loymaxUser) {
        return (List<UserAccounts>) retryIfTokenExpired(loymaxUser,
                () -> loymaxService.getDetailedBalanceInfo(loymaxUser));
    }

    @Override
    public LoymaxUserFavoriteCategories selectUserFavoriteCategories(LoymaxUser loymaxUser) {
        return (LoymaxUserFavoriteCategories) retryIfTokenExpired(loymaxUser,
                () -> loymaxService.selectUserFavoriteCategories(loymaxUser));
    }

    @Override
    public LoymaxUserFavoriteCategories getUserFavoriteCategories(LoymaxUser loymaxUser) {
        return (LoymaxUserFavoriteCategories) retryIfTokenExpired(loymaxUser,
                () -> loymaxService.getUserFavoriteCategories(loymaxUser));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CategoryDto> selectFavoriteCategories(LoymaxUser loymaxUser) {
        return (List<CategoryDto>) retryIfTokenExpired(loymaxUser,
                () -> loymaxService.selectFavoriteCategories(loymaxUser));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CheckDto> selectUserChecks(LoymaxUser loymaxUser) {
        return (List<CheckDto>) retryIfTokenExpired(loymaxUser,
                () -> loymaxService.selectUserChecks(loymaxUser));
    }

    @Override
    public LoymaxUserStatus selectStatus(LoymaxUser loymaxUser) {
        return (LoymaxUserStatus) retryIfTokenExpired(loymaxUser,
                () -> loymaxService.selectStatus(loymaxUser));
    }

    @Override
    public UserCardDto replaceUserCard(LoymaxUser loymaxUser, LoymaxUserCardRequestDto loymaxCardRequest, Long loymaxCardId) {
        return (UserCardDto) retryIfTokenExpired(loymaxUser,
                () -> loymaxService.replaceUserCard(loymaxUser, loymaxCardRequest, loymaxCardId));
    }

    @Override
    public UserCardDto changeCardBlockState(LoymaxUser loymaxUser, CardPasswordDto cardPasswordDto, Long loymaxCardId) {
        return (UserCardDto) retryIfTokenExpired(loymaxUser,
                () -> loymaxService.changeCardBlockState(loymaxUser, cardPasswordDto, loymaxCardId));
    }

    @Override
    public CardQrDto getQrCode(LoymaxUser loymaxUser, Long cardId) {
        return (CardQrDto) retryIfTokenExpired(loymaxUser,
                () -> loymaxService.getQrCode(loymaxUser, cardId));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<LoymaxCouponsDto> getCouponsList(LoymaxUser loymaxUser) {
        return (List<LoymaxCouponsDto>) retryIfTokenExpired(loymaxUser,
                () -> loymaxService.getCouponsList(loymaxUser));
    }

    @Override
    public List<LoymaxUserBalanceInfoDto> getDetailedBalance(LoymaxUser loymaxUser) {
        return loymaxService.getDetailedBalance(loymaxUser);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getLogicalName(LoymaxUser loymaxUser) {
        return (List<String>) retryIfTokenExpired(loymaxUser,
                () -> loymaxService.getLogicalName(loymaxUser));
    }

    @Override
    public ObjectNode getDateFromAttribute(LoymaxUser loymaxUser, String logicalName) {
        return (ObjectNode) retryIfTokenExpired(loymaxUser,
                () -> loymaxService.getDateFromAttribute(loymaxUser, logicalName));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PersonalGoodsDto> getPersonalGoods(LoymaxUser loymaxUser, String logicalName) {
        return (List<PersonalGoodsDto>) retryIfTokenExpired(loymaxUser,
                () -> loymaxService.getPersonalGoods(loymaxUser, logicalName));
    }

    @Override
    public void acceptToGoods(LoymaxUser loymaxUser, String brandId, String goodsId) {
        retryIfTokenExpired(loymaxUser,
                () -> loymaxService.acceptToGoods(loymaxUser, brandId, goodsId));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<LoymaxCheckItem> getChecksList(LoymaxUser loymaxUser, LoymaxCard loymaxCard, Integer offset, Integer limit, Long dateStart, Long dateEnd) {
        return (List<LoymaxCheckItem>) retryIfTokenExpired(loymaxUser,
                () -> loymaxService.getChecksList(loymaxUser, loymaxCard,
                        offset, limit, dateStart, dateEnd));
    }

    @Override
    public void acceptToFavoriteCategory(LoymaxUser loymaxUser, List<String> ids) {
        retryIfTokenExpired(loymaxUser,
                () -> loymaxService.acceptToFavoriteCategory(loymaxUser, ids));

    }

    @Override
    public void changeRejectPaperChecks(Boolean change, LoymaxUser loymaxUser) {
        retryIfTokenExpired(loymaxUser,
                () -> loymaxService.changeRejectPaperChecks(change, loymaxUser));
    }

    @Override
    public String getPhoneNumber(String token) {
        return loymaxService.getPhoneNumber(token);
    }

    @Override
    public void setMobile(LoymaxUser loymaxUser) {
        retryIfTokenExpired(loymaxUser,
                () -> loymaxService.setMobile(loymaxUser));
    }

    @Override
    public TokenExchangeResponse exchangeForTokenAdmin(String userData, String secret) {
        return loymaxService.exchangeForTokenAdmin(userData, secret);
    }

    @Override
    public List<LoymaxCheckItem> getChecksListAdmin(String token, Long personId) {
        return loymaxService.getChecksListAdmin(token, personId);
    }

    private interface ResultRunnable {
        Object run() throws LoymaxUnauthorizedException;
    }
}