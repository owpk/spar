package ru.sparural.engine.loymax.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
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
import ru.sparural.engine.services.exception.ResourceNotFoundException;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
public interface LoymaxService {
    TokenExchangeResponse exchangeForToken(String userData, String secret);

    LoymaxUser updateLoymaxUser(LoymaxUser loymaxUser);

    LoymaxUser saveOrUpdate(LoymaxUser loymaxUser);

    LoymaxUser findByPersonUid(String personUid);

    LoymaxUserInfo getUserInfo(LoymaxUser loymaxUser);

    TokenExchangeResponse loginViaSocial(String secret, String socialName);

    LoymaxUser getByLocalUserId(Long localUserId) throws ResourceNotFoundException;

    LoymaxUser createLoymaxUser(Long userId, String loymaxPersonUid, TokenExchangeResponse response);

    void beginRegistration(String phoneNumber, Registrations reg, Long userId);

    void resendRegistrationConfirmCode(LoymaxUser loymaxUser);

    void confirmRegistration(ConfirmRegistrationRequest confirmRegistrationRequest, Long userId);

    void registrationSetPassword(RegistrationSetPasswordRequest setPasswordRequest, Long userId);

    List<LoymaxCounterResponse> counterInfo(Long counterId, Long personId);

    List<LoymaxCheckItem> loadChecksForUser(Long userId);

    LoymaxUser sendUserUpdateRequest(UserProfileUpdateRequest userDto, LoymaxUser loymaxUser, String move);

    void sendRegistrationUserUpdateRequest(UserProfileUpdateRequest userDto, Long userId);

    void recoverPassword(RecoveryPasswordRequestDto recoveryPasswordRequestDto);

    TokenExchangeResponse resetPassword(LoymaxResetPasswordRequest resetPasswordRequest);

    void changePassword(LoymaxUser loymaxUser, ChangePasswordRequestDto changePasswordRequestDto);

    List<LoymaxOffer> getOffers(String token, String[] params);

    List<LoymaxOffer> getOffers(String[] params);

    void deregistrationBegin(LoymaxUser loymaxUser);

    void deregistrationConfirm(LoymaxUser loymaxUser, DeregistrationConfirmDto dto);

    void userSetSocial(String socialName, LoymaxUser loymaxUser, SocialSetDto socialSetDto);

    void userRemoveSocial(LoymaxUser loymaxUser, String socialName);

    List<PromotionsDto> getPromotions(Integer offset, Integer limit);

    void refreshTokenIfNeeded(LoymaxUser loymaxUser);

    void addTokensForUserAndDoUpdate(LoymaxUser loymaxUser, TokenExchangeResponse resp);

    void updateUserEmail(EmailDto emailDto, LoymaxUser loymaxUser);

    void confirmUserEmail(LoymaxUser loymaxUser, LoymaxConfirmCodeDto codeDto);

    void sendEmailConfirmCode(LoymaxUser loymaxUser);

    void cancelEmailProcessing(LoymaxUser loymaxUser);

    void updateUserPhone(LoymaxUser loymaxUser, PhoneDto phoneDto);

    void confirmUserPhone(LoymaxUser loymaxUser, LoymaxConfirmCodeDto codeDto);

    void sendPhoneConfirmCode(LoymaxUser loymaxUser);

    List<Social> getSocialsBindings(LoymaxUser loymaxUser);

    List<LoymaxUserAction> getUserActions(LoymaxUser loymaxUser);

    void attachSendConfirmCode(LoymaxUser loymaxUser);

    UserCardDto createUserCardFromLoymaxCard(LoymaxUserCardDto loymaxUserCardDto, String loymaxUserId);

    List<UserCardDto> selectCards(LoymaxUser loymaxUser);

    void attachUserCard(LoymaxUser loymaxUser, LoymaxUserCardRequestDto userCardDto);

    UserCardDto createUserCard(LoymaxUser loymaxUser, LoymaxUserCardRequestDto cardNumberRequestDto);

    UserCardDto emitVirtualCard(LoymaxUser loymaxUser);

    UserCardDto attachConfirm(LoymaxUser loymaxUser, CodeDto codeDto);

    List<UserAccounts> getDetailedBalanceInfo(LoymaxUser loymaxUser);

    LoymaxUserFavoriteCategories selectUserFavoriteCategories(LoymaxUser loymaxUser);

    LoymaxUserFavoriteCategories getUserFavoriteCategories(LoymaxUser loymaxUser);

    List<CategoryDto> selectFavoriteCategories(LoymaxUser loymaxUser);

    List<CheckDto> selectUserChecks(LoymaxUser loymaxUser);

    LoymaxUserStatus selectStatus(LoymaxUser loymaxUser);

    UserCardDto replaceUserCard(LoymaxUser loymaxUser,
                                LoymaxUserCardRequestDto loymaxCardRequest,
                                Long loymaxCardId);

    UserCardDto changeCardBlockState(LoymaxUser loymaxUser, CardPasswordDto cardPasswordDto, Long loymaxCardId);

    CardQrDto getQrCode(LoymaxUser loymaxUser, Long cardId);

    List<LoymaxCouponsDto> getCouponsList(LoymaxUser loymaxUser);

    List<LoymaxUserBalanceInfoDto> getDetailedBalance(LoymaxUser loymaxUser);

    List<String> getLogicalName(LoymaxUser loymaxUser);

    ObjectNode getDateFromAttribute(LoymaxUser loymaxUser, String logicalName);

    List<PersonalGoodsDto> getPersonalGoods(LoymaxUser loymaxUser, String logicalName);

    void acceptToGoods(LoymaxUser loymaxUser, String brandId, String goodsId);

    List<LoymaxCheckItem> getChecksList(LoymaxUser loymaxUser, LoymaxCard loymaxCard, Integer offset, Integer limit, Long dateStart, Long dateEnd);

    void acceptToFavoriteCategory(LoymaxUser loymaxUser, List<String> ids);

    void changeRejectPaperChecks(Boolean change, LoymaxUser loymaxUser);

    String getPhoneNumber(String token);

    void setMobile(LoymaxUser loymaxUser);

    TokenExchangeResponse exchangeForTokenAdmin(String userData, String secret);

    List<LoymaxCheckItem> getChecksListAdmin(String token, Long personId);

    void forceRefreshToken(LoymaxUser loymaxUser);

    LoginTwoFAResponse exchangeForOneTimePassword(String login);

    TokenExchangeResponse loginViaOneTimeCode(String token, String code);

    void cancelUpdatingPhoneNumber(LoymaxUser loymaxUser);

    List<LoymaxUser> fetchAllUsers();

    LoymaxAttributesDto<String> importUserAttribute(Long loymaxUserId, String attributeName);

    List<LoymaxOffer> getOfferById(Long loymaxOfferId);

    boolean checkIfNumberIsTaken(String phoneNumber);
}
