package ru.sparural.engine.loymax;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Vorobyev Vyacheslav
 */
@Component
@Getter
public final class LoymaxConstants {

    // HTTP
    public static final String twoFACodeHeader = "X-Loymax-2FA";
    public static final String LOYMAX_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String PATH_VARIABLE_PATTERN = "\\{.+}";
    public static final int LOYMAX_DEAFULT_START_DATE = 1596240039;
    // LOYMAX DATA-RESULT RESPONSE STATES
    public static final String RESULT_RESPONSE_NO_STATE = "No state";

    // ------------------------------
    //       LOYMAX CONSTANTS
    //-------------------------------
    public static final String RESULT_RESPONSE_STATE_SUCCESS = "Success";
    public static final String RESULT_RESPONSE_STATE_ERROR = "Error";
    public static String LOYMAX_BASE_URL_DEFAULT = "https://sparch.loymax.tech";
    public String refreshToken;
    public String publicApiUrl;
    public String systemApiUrl;
    public String userInfoUrl;
    public String promotionsUrl;
    public String registrationBegin;
    public String registrationConfirm;
    public String userActions;
    public String acceptTenderOffer;
    public String registrationSetPassword;
    public String validName;
    public String userInfo;
    public String registrationUserUpdate;
    public String registrationSendConfirmCode;
    public String userQuestions;
    public String registrationTryToFinish;
    public String recoveryPassword;
    public String resetPasswordConfirm;
    public String changePassword;
    public String loymaxOffers;
    public String loymaxOfferById;
    public String loymaxFiles;
    public String deregistrationSendCode;
    public String deregistrationConfirm;
    public String updateEmial;
    public String updateEmailConfirm;
    public String updateEmailSendConfirmCode;
    public String bonusCounter;
    public String cancelEmailProcessing;
    public String updatePhone;
    public String cancelPhoneUpdating;
    public String updatePhoneConfirm;
    public String updatePhoneSendConfirmCode;
    public String userBalanceInfo;
    public String userFavoriteCategories;
    public String userChecks;
    public String userChecksAdmin;
    public String selectUserStatus;
    public String getCoupons;
    public String getAttribute;
    public String getPersonalOffer;
    public String acceptGoods;
    public String selectFavoriteCategory;
    public String getFavoriteGoods;
    public String selectCards;
    public String emitVirtual;
    public String setMobile;
    public String counterValue;
    // CARD
    public String cardAttachConfirm;
    public String cardCheckAttachStatus;
    public String attachSendConfirmCode;
    public String cardReplace;
    public String cardChangeBlockState;
    public String userCardSet;
    public String getQrCode;
    public String userSetSocialPattern;
    public String userRemoveSocialPattern;
    // LOYMAX-SOCIALS
    public String vkLoginUrl;
    public String facebookLoginUrl;
    // LOYMAX-SOCIALS-LOGIN
    public String odnoklassnikiLoginUrl;
    public String appleLoginUrl;
    public String userCardAttach;
    public String vkSetUrl;
    public String facebookSetUrl;
    // LOYMAX-SOCIALS-SET
    public String odnoklassnikiSetUrl;
    public String appleSetUrl;
    public String userLogins;
    public String tokenExchangeUrl;
    // ------------------------------
    //          LOYMAX API
    //-------------------------------
    @Value("${sparural.loymax.rest.base-url}")
    private String loymaxBaseUrl;
    private String getAttributeValue;
    @Value("${sparural.loymax.social.redirect-url.login}")
    private String sparRedirectUrlPattern;
    @Value("${sparural.loymax.social.redirect-url.set}")
    private String sparRedirectSetUrlPattern;

    public static String append(String root, String url) {
        return String.format("%s%s", root, url);
    }


    // ------------------------------
    //             UTILS
    //-------------------------------

    @PostConstruct
    private void init() {
        counterValue = appendToSystemApiUrl("/v1.2/counters/{counterId}/values");
        refreshToken = appendToBaseUrl("/authorizationservice/token");
        publicApiUrl = appendToBaseUrl("/publicapi");
        systemApiUrl = appendToBaseUrl("/systemapi");
        bonusCounter = appendToSystemApiUrl("/v1.2/counters/{counterId}/values");
        userInfoUrl = appendToPublicApiUrl("/v1.2/User");
        promotionsUrl = appendToPublicApiUrl("/v1.2/Offer");
        registrationBegin = appendToPublicApiUrl("/v1/Registration/BeginRegistration");
        registrationConfirm = appendToPublicApiUrl("/v1/User/PhoneNumber/Confirm");
        userActions = appendToPublicApiUrl("/v1/User/Actions");
        acceptTenderOffer = appendToPublicApiUrl("/v1/User/AcceptTenderOffer");
        registrationSetPassword = appendToPublicApiUrl("/v1/User/Password/Set");
        validName = appendToPublicApiUrl("/v1/User/PhoneNumber/");
        registrationUserUpdate = appendToPublicApiUrl("/v1.2/User/Answers");
        registrationSendConfirmCode = appendToPublicApiUrl("/v1.2/User/PhoneNumber/SendConfirmCode");
        userQuestions = appendToPublicApiUrl("/v1.2/User/Questions");
        registrationTryToFinish = appendToPublicApiUrl("/v1/Registration/TryFinishRegistration");
        recoveryPassword = appendToPublicApiUrl("/v1.2/ResetPassword/Start");
        resetPasswordConfirm = appendToPublicApiUrl("/v1.2/ResetPassword/Confirm");
        changePassword = appendToPublicApiUrl("/v1.2/User/Password/Change");
        loymaxOffers = appendToPublicApiUrl("/v1.2/Offer");
        loymaxOfferById = appendToPublicApiUrl("/v1.2/Offer/{id}");
        loymaxFiles = appendToPublicApiUrl("/v1.2/Files");
        deregistrationSendCode = appendToPublicApiUrl("/v1.2/User/Deregistration/SendConfirmCode");
        deregistrationConfirm = appendToPublicApiUrl("/v1.2/User/Deregistration/Confirm");
        updateEmial = appendToPublicApiUrl("/v1.2/User/Email");
        updateEmailConfirm = appendToPublicApiUrl("/v1.2/User/Email/Confirm");
        updateEmailSendConfirmCode = appendToPublicApiUrl("/v1.2/User/Email/SendConfirmCode");
        cancelEmailProcessing = appendToPublicApiUrl("/v1.2/User/Email/CancelChange");
        updatePhone = appendToPublicApiUrl("/v1.2/User/PhoneNumber");
        updatePhoneConfirm = appendToPublicApiUrl("/v1.2/User/PhoneNumber/Confirm");
        updatePhoneSendConfirmCode = appendToPublicApiUrl("/v1.2/User/PhoneNumber/SendConfirmCode");
        cancelPhoneUpdating = appendToPublicApiUrl("/v.2/User/PhoneNumber/CancelChange");
        userBalanceInfo = appendToPublicApiUrl("/v1.2/User/DetailedBalance");
        userFavoriteCategories = appendToPublicApiUrl("/v1.2/User/Attributes/Common/Values/FavoriteCategory");
        userChecks = appendToPublicApiUrl("/v1.2/History");
        userChecksAdmin = appendToSystemApiUrl("/api/Users/{personId}/History");
        selectUserStatus = appendToPublicApiUrl("/v1.2/User/Status");
        getCoupons = appendToPublicApiUrl("/v1.2/Coupons/");
        getAttribute = appendToPublicApiUrl("/v1.2/User/Attributes");
        getAttributeValue = appendToPublicApiUrl("/v1.2/User/Attributes/{userId}");
        getPersonalOffer = appendToPublicApiUrl("/v1.2/User/Attributes/Common/Values/{logicalName}");
        acceptGoods = appendToPublicApiUrl("/v1.2/User/Attributes/Common/Values/PersonalOffersGoods");
        selectFavoriteCategory = appendToPublicApiUrl("/v1/user/personalOffer/FavoriteCategory/accept");
        getFavoriteGoods = appendToPublicApiUrl("/v1.2/user/favoriteGoods");
        selectCards = appendToPublicApiUrl("/v1.2/Cards");
        emitVirtual = appendToPublicApiUrl("/v1.2/Cards/EmitVirtual");
        setMobile = appendToPublicApiUrl("/v1.2/User/Attributes/Common/Values/MobileApplicationInstalled");
        cardAttachConfirm = appendToPublicApiUrl("/v1.2/Cards/Attach/Confirm");
        cardCheckAttachStatus = appendToPublicApiUrl("/v1.2/Cards/Attach");
        attachSendConfirmCode = appendToPublicApiUrl("/v1.2/Cards/Attach/SendConfirmCode");
        cardReplace = appendToPublicApiUrl("/v1.2/Cards/{cardId}/Replace");
        cardChangeBlockState = appendToPublicApiUrl("/v1.2/Cards/{cardId}/ChangeBlockState");
        userCardSet = appendToPublicApiUrl("/v1.2/Cards/Set");
        getQrCode = appendToPublicApiUrl("/v1.2/Cards/{cardId}/QrCode");
        userSetSocialPattern = appendToPublicApiUrl("/v1.2/User/%s/Set");
        userRemoveSocialPattern = appendToPublicApiUrl("/v1.2/User/%s/Remove");
        vkLoginUrl = appendToPublicApiUrl("/v1.2/User/VKontakte/Login/");
        facebookLoginUrl = appendToPublicApiUrl("/v1.2/User/Facebook/Login");
        odnoklassnikiLoginUrl = appendToPublicApiUrl("/v1.2/User/Odnoklassniki/Login");
        appleLoginUrl = appendToPublicApiUrl("/v1.2/User/Apple/Login");
        userCardAttach = appendToPublicApiUrl("/v1.2/Cards/Attach");
        vkSetUrl = appendToPublicApiUrl("/v1.2/User/Vkontakte/Set/");
        facebookSetUrl = appendToPublicApiUrl("/v1.2/User/Facebook/Set");
        odnoklassnikiSetUrl = appendToPublicApiUrl("/v1.2/User/Odnoklassniki/Set");
        appleSetUrl = appendToPublicApiUrl("/v1.2/User/Apple/Set");
        userLogins = appendToPublicApiUrl("/v1.2/User/Logins");
        userInfo = appendToSystemApiUrl("/v1.2/users");
        tokenExchangeUrl = appendToBaseUrl("/authorizationService/token");
    }

    public String appendToBaseUrl(String path) {
        return append(loymaxBaseUrl(), path);
    }

    public String appendToPublicApiUrl(String path) {
        return append(publicApiUrl, path);
    }

    public String appendToSystemApiUrl(String path) {
        return append(systemApiUrl, path);
    }

    private String loymaxBaseUrl() {
        return loymaxBaseUrl == null ? LOYMAX_BASE_URL_DEFAULT : loymaxBaseUrl;
    }
}