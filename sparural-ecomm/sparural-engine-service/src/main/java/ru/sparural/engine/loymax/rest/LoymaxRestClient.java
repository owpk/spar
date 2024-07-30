package ru.sparural.engine.loymax.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.sparural.engine.api.dto.EmailDto;
import ru.sparural.engine.api.dto.PhoneDto;
import ru.sparural.engine.api.dto.RecoveryPasswordRequestDto;
import ru.sparural.engine.api.dto.cards.CardPasswordDto;
import ru.sparural.engine.api.dto.promotions.PromotionsDto;
import ru.sparural.engine.api.dto.socials.SocialSetDto;
import ru.sparural.engine.api.dto.user.ChangePasswordRequestDto;
import ru.sparural.engine.api.enums.Genders;
import ru.sparural.engine.loymax.LoymaxConstants;
import ru.sparural.engine.loymax.exceptions.LoymaxException;
import ru.sparural.engine.loymax.exceptions.LoymaxUnauthorizedException;
import ru.sparural.engine.loymax.rest.dto.*;
import ru.sparural.engine.loymax.rest.dto.account.LoymaxUserBalanceInfoDto;
import ru.sparural.engine.loymax.rest.dto.account.LoymaxUserBalanceInfoItemsDto;
import ru.sparural.engine.loymax.rest.dto.attribute.LoymaxAttributesDto;
import ru.sparural.engine.loymax.rest.dto.attribute.LoymaxUserAttributeDto;
import ru.sparural.engine.loymax.rest.dto.attribute.LoymaxUserAttributeInfoDto;
import ru.sparural.engine.loymax.rest.dto.cards.*;
import ru.sparural.engine.loymax.rest.dto.categories.LoymaxUserFavoriteCategories;
import ru.sparural.engine.loymax.rest.dto.check.LoymaxCheck;
import ru.sparural.engine.loymax.rest.dto.counter.LoymaxCounterResponse;
import ru.sparural.engine.loymax.rest.dto.goods.LoymaxAcceptGoodsRequest;
import ru.sparural.engine.loymax.rest.dto.goods.LoymaxPersonalGoodsResponseDto;
import ru.sparural.engine.loymax.rest.dto.loginexception.LoymaxLoginException;
import ru.sparural.engine.loymax.rest.dto.offer.LoymaxOffer;
import ru.sparural.engine.loymax.rest.dto.password.LoymaxResetPasswordRequest;
import ru.sparural.engine.loymax.rest.dto.registration.*;
import ru.sparural.engine.loymax.rest.dto.status.LoymaxUserStatus;
import ru.sparural.engine.loymax.rest.dto.user.*;
import ru.sparural.engine.loymax.rest.dto.user.login.LoginTwoFAResponse;
import ru.sparural.engine.loymax.rest.dto.user.login.LoymaxUserLoginDto;
import ru.sparural.engine.loymax.socials.LoymaxSocial;
import ru.sparural.engine.loymax.utils.PathVariableUtils;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.UnauthorizedException;
import ru.sparural.utils.RestTemplateConstants;
import ru.sparural.utils.rest.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * @author Vorobyev Vyacheslav
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoymaxRestClient {
    private static final Integer LOYMAX_UNAUTHORIZED_EXCEPTION_CODE = 600;

    private final RestTemplate restTemplate;
    private final LoymaxConstants loymaxConstants;

    public LoginTwoFAResponse exchangeForLoginOneTimeCode(String userData) {
        var body = String.format("grant_type=password&username=%s", userData);
        var loginTwoFAResponse = new LoginTwoFAResponse();
        restTemplate.request()
                .withContentType(RestTemplateConstants.MediaTypes.APPLICATION_FORM_URLENCODED)
                .withMessageProcessor(new MessageProcessor() {
                    @Override
                    public void onSuccess(RestResponse response) throws RestTemplateException { /* no success response expected */ }

                    @Override
                    public void onFailure(RestResponse response) throws RestTemplateException {
                        if (response.getCode() == 400) {
                            var body = response.getBody();
                            var byteBody = new String(body, StandardCharsets.UTF_8);
                            var gson = new Gson();
                            var json = gson.fromJson(byteBody, LoymaxLoginException.class);
                            if (json.getError().equals("TwoFactorAuthenticationCodeRequired")) {
                                var twoFACode = response.getHeaders().get(LoymaxConstants.twoFACodeHeader);
                                if (twoFACode != null && !twoFACode.isEmpty()) {
                                    var code = twoFACode.get(0);
                                    loginTwoFAResponse.setCode(code);
                                    loginTwoFAResponse.setMessage(json.getError_description());
                                }
                            } else throw new LoymaxException(json.getError_description());
                        }
                    }
                }).post(loymaxConstants.getTokenExchangeUrl(), body);
        if (loginTwoFAResponse.getCode() == null)
            throw new LoymaxException("Two FA code is empty");
        return loginTwoFAResponse;
    }

    public TokenExchangeResponse loginViaOneTimeCode(String tempToken, String code) {
        var body = String.format("grant_type=password&password=%s", code);
        return restTemplate.request()
                .withContentType(RestTemplateConstants.MediaTypes.APPLICATION_FORM_URLENCODED)
                .withFailureCallback(this::handleLoginException)
                .addHeader(LoymaxConstants.twoFACodeHeader, tempToken)
                .setResponseType(TokenExchangeResponse.class)
                .withConvertException(this::handleLoymaxConvertException)
                .postForEntity(loymaxConstants.getTokenExchangeUrl(), body)
                .orElseThrow(() -> new UnauthorizedException("Wrong login or password", 401));
    }

    public TokenExchangeResponse exchangeForToken(String userData, String secret) {
        var body = String.format("grant_type=password&username=%s&password=%s", userData, secret);
        return baseExchangeForToken(body);
    }

    public TokenExchangeResponse exchangeForTokenAdmin(String userData, String secret) {
        var body = String.format("grant_type=password&username=%s&password=%s&area=users", userData, secret);
        return restTemplate.request()
                .withContentType(RestTemplateConstants.MediaTypes.APPLICATION_FORM_URLENCODED)
                .withFailureCallback(this::handleLoginException)
                .setResponseType(TokenExchangeResponse.class)
                .withConvertException(this::handleLoymaxConvertException)
                .postForEntity(loymaxConstants.getTokenExchangeUrl(), body)
                .orElseThrow(() -> new UnauthorizedException("Wrong login or password", 401));
    }

    public List<LoymaxCounterResponse> counterHandler(Long counterId, Long personId, String adminToken) {
        var url = loymaxConstants.bonusCounter.replace("{counterId}", String.valueOf(counterId))
                + "?filter.personId=" + personId;
        var response = restTemplate.request()
                .withAuthorizationHeader(AuthType.BEARER, adminToken)
                .withFailureCallback(this::handleLoymaxErrors)
                .withContentType(RestTemplateConstants.MediaTypes.APPLICATION_JSON)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<List<LoymaxCounterResponse>>>() {
                })
                .withConvertException(this::handleLoymaxConvertException)
                .getForEntity(url)
                .orElseThrow(() -> new RuntimeException("no response present"));
        validateResponseState(response);
        return response.getData();
    }

    private void handleLoginException(RestResponse restResponse) {
        var byteBody = new String(restResponse.getBody());
        var gson = new Gson();
        var json = gson.fromJson(byteBody, LoymaxLoginException.class);
        switch (json.getError()) {
            case "IncorrectLoginOrPassword":
                throw new UnauthorizedException("Логин или пароль указаны неверно", 401);
            case "Blocked":
                throw new UnauthorizedException(json.getError_description(), 401);
            default:
                throw new LoymaxException(json.getError_description());
        }
    }

    public LoymaxUserInfo getUserInfo(String token) {
        var loymaxUser = restTemplate.request()
                .withAuthorizationBearer(token)
                .withFailureCallback(this::handleLoymaxErrors)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<LoymaxUserInfo>>() {
                })
                .getForEntity(loymaxConstants.getUserInfoUrl())
                .orElseThrow(LoymaxException::new)
                .getData();
        try {
            loymaxUser.setGender(findLoymaxUserGender(token).getGender());
            loymaxUser.setRejectPaperChecks(findRejectPaper(token));
        } catch (ResourceNotFoundException ignore) {
        }
        return loymaxUser;
    }

    private Genders findLoymaxUserGender(String token) {
        var loymaxUserQuestions = restTemplate.request()
                .withAuthorizationBearer(token)
                .setResponseType(LoymaxQuestionDataRoot.class).getForEntity(loymaxConstants.userQuestions);
        try {
            var result = loymaxUserQuestions.get().getData().stream().findFirst().get()
                    .getQuestions()
                    .stream().filter(x -> x.getLogicalName().equals("Sex"))
                    .findFirst().get().getFixedAnswers()
                    .stream().filter(LoymaxFixedAnswer::getIsSelected).findFirst().get();
            var genderName = result.getName();
            if (genderName.startsWith("Муж"))
                return Genders.MALE;
            else if (genderName.startsWith("Жен"))
                return Genders.FEMALE;
            else return Genders.OTHER;
        } catch (NoSuchElementException | NullPointerException e) {
            log.error("Loymax user gender not found");
            throw new ResourceNotFoundException("Gender not present");
        }
    }

    public TokenExchangeResponse loginViaSocial(LoymaxSocial loymaxSocial, String code) {
        var url = loymaxSocial.buildLoginUrl(code);
        var dataResultResponse = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<TokenExchangeResponse>>() {
                })
                .withConvertException(this::handleLoymaxConvertException)
                .getForEntity(url)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cannot login with social", 401));
        validateResponseState(dataResultResponse);
        return dataResultResponse.getData();
    }

    /**
     * @param phoneNumber - user login data
     * @return TokenExchangeResponse
     */
    public TokenExchangeResponse beginRegistration(String phoneNumber) {
        var result = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<RegistrationBeginResponse>>() {
                })
                .withConvertException(this::handleLoymaxConvertException)
                .postForEntity(loymaxConstants.registrationBegin,
                        new BeginRegistration(phoneNumber, ""))
                .orElseThrow(LoymaxException::new);

        if (result.getData().getState().equals("RegistrationAlreadyCompleted")) {
            throw new LoymaxException("Пользователь уже существует");
        }
        validateResponseState(result);
        var authResult = result.getData().getAuthResult();
        var tokenExchangeResponse = new TokenExchangeResponse();
        tokenExchangeResponse.setAccessToken(authResult.getAccess_token());
        tokenExchangeResponse.setRefreshToken(authResult.getRefresh_token());
        tokenExchangeResponse.setExpiresIn(authResult.getExpires_in());
        return tokenExchangeResponse;
    }

    public void acceptTenderOffer(String token) {
        var resp = restTemplate.request()
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse>() {
                })
                .postForEntity(loymaxConstants.acceptTenderOffer)
                .orElseThrow(LoymaxException::new);
        validateResponseState(resp);
    }

    // TODO check response model
    public void sendConfirmCode(String token) {
        var resp = restTemplate.request()
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse>() {
                })
                .getForEntity(loymaxConstants.registrationSendConfirmCode)
                .orElseThrow(LoymaxException::new);
        validateResponseState(resp);
    }

    public <X> String createLoymaxQuestionRequest(String endpoint, String token, X requestBody) {
        LoymaxDataResultResponse<String> loymaxResultResponse =
                defaultLoymaxRequest(endpoint, requestBody, token, new TypeReference<>() {
                }, "Loymax registration error");
        validateResponseState(loymaxResultResponse);
        return loymaxResultResponse.getData();
    }

    public List<LoymaxUserQuestionDto> getLoymaxUserRegistrationActionsSystem(String systemToken, String personId) {
        var filter = "filter.onlyRequired=true";
        var url = loymaxConstants.getUserQuestionsSystem().replaceAll("\\{personId}", personId) + "?" + filter;
        var loymaxResponse = restTemplate.request()
                .withAuthorizationBearer(systemToken)
                .withFailureCallback(this::handleLoymaxErrors)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<List<LoymaxUserQuestionsListDto>>>() {
                })
                .withConvertException(x -> new LoymaxException(x.toString()))
                .getForEntity(url)
                .orElseThrow(LoymaxException::new);
        validateResponseState(loymaxResponse);
        return loymaxResponse.getData().stream()
                .flatMap(x -> x.getQuestions().stream())
                .collect(Collectors.toList());
    }

    public List<LoymaxUserAction> getLoymaxUserActions(String token) {
        var loymaxResponse = restTemplate.request()
                .withAuthorizationBearer(token)
                .withFailureCallback(this::handleLoymaxErrors)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<Actions>>() {
                })
                .withConvertException(x -> new LoymaxException(x.toString()))
                .getForEntity(loymaxConstants.userActions)
                .orElseThrow(LoymaxException::new);
        validateResponseState(loymaxResponse);
        return loymaxResponse.getData().getActions();
    }

    public TokenExchangeResponse tryToFinishRegistration(String token) {
        var loymaxResponse = restTemplate.request()
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<CompleteRegistrationResponse>>() {
                })
                .postForEntity(loymaxConstants.registrationTryToFinish)
                .orElseThrow(LoymaxException::new);
        validateResponseState(loymaxResponse);
        if (!loymaxResponse.getData().getRegistrationCompleted())
            throw new LoymaxException("registration is not completed. Details: " + loymaxResponse);
        var data = loymaxResponse.getData();
        var tokenExchangeResponse = new TokenExchangeResponse();
        tokenExchangeResponse.setAccessToken(data.getAccess_token());
        tokenExchangeResponse.setRefreshToken(data.getRefresh_token());
        tokenExchangeResponse.setExpiresIn(data.getExpires_in());
        return tokenExchangeResponse;
    }

    // TODO check response model
    public void recoverPassword(RecoveryPasswordRequestDto recoveryPasswordRequestDto) {
        var resp = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .setResponseType(new TypeReference<LoymaxDataResultResponse>() {
                })
                .withConvertException(x -> new UnauthorizedException(x.toString(), 403))
                .postForEntity(loymaxConstants.recoveryPassword,
                        new RecoveryPasswordRequestDto(recoveryPasswordRequestDto.getNotifierIdentity()))
                .orElseThrow(() -> new LoymaxException("Cannot process recover password request"));
        validateResponseState(resp);
    }

    public TokenExchangeResponse resetPassword(LoymaxResetPasswordRequest resetPasswordRequest) {
        var loymaxDataResultResponse = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<TokenExchangeResponse>>() {
                })
                .withConvertException(this::handleLoymaxConvertException)
                .postForEntity(loymaxConstants.resetPasswordConfirm, resetPasswordRequest)
                .orElseThrow();
        validateResponseState(loymaxDataResultResponse);
        return loymaxDataResultResponse.getData();
    }

    public TokenExchangeResponse changePassword(String token, ChangePasswordRequestDto changePasswordRequestDto) {
        var dataResult
                = defaultLoymaxRequest(loymaxConstants.changePassword, changePasswordRequestDto,
                token, new TypeReference<LoymaxDataResultResponse<TokenExchangeResponse>>() {
                });
        validateResponseState(dataResult);
        return dataResult.getData();
    }

    private String getOfferUrl(String[] params) {
        return loymaxConstants.loymaxOffers + (params != null ? "?" + String.join("&", params) : "");
    }

    public List<LoymaxOffer> getOffers(String[] params) {
        var url = getOfferUrl(params);
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<List<LoymaxOffer>>>() {
                })
                .withConvertException(this::handleLoymaxConvertException)
                .getForEntity(url)
                .orElseThrow(LoymaxException::new);
        validateResponseState(response);
        return response.getData();
    }

    public List<LoymaxOffer> getOffers(String token, String... params) {
        var url = getOfferUrl(params);
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationHeader(AuthType.BEARER, token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<List<LoymaxOffer>>>() {
                })
                .withConvertException(this::handleLoymaxConvertException)
                .getForEntity(url)
                .orElseThrow(LoymaxException::new);
        validateResponseState(response);
        return response.getData();
    }

    public List<LoymaxOffer> getOfferById(Long loymaxOfferId) {
        var url = loymaxConstants.getLoymaxOfferById().replace("{id}", String.valueOf(loymaxOfferId));
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<List<LoymaxOffer>>>() {
                })
                .withConvertException(this::handleLoymaxConvertException)
                .getForEntity(url)
                .orElseThrow(LoymaxException::new);
        validateResponseState(response);
        return response.getData();
    }

    /**
     * @param token - access token
     * @return code length
     */
    public int deregistrationBegin(String token) {
        var resp = defaultLoymaxRequest(
                loymaxConstants.deregistrationSendCode,
                null, token, new TypeReference<
                        LoymaxDataResultResponse<DeregistrationConfirmCodeLengthDto>>() {
                });
        validateResponseState(resp);
        return resp.getData().getConfirmCodeLength();
    }

    public void deregistrationConfirm(String token, DeregistrationConfirmDto dto) {
        var resp = defaultLoymaxRequest(
                loymaxConstants.deregistrationConfirm,
                dto, token, new TypeReference<LoymaxDataResultResponse>() {
                });
        validateResponseState(resp);
    }

    public void userSetSocial(LoymaxSocial loymaxSocial, String token, SocialSetDto socialSetDto) {
        restTemplate.request()
                .withMessageProcessor(new MessageProcessor() {
                    public void onSuccess(RestResponse response) throws RestTemplateException {
                    }

                    public void onFailure(RestResponse response) throws RestTemplateException {
                        throw new LoymaxException(new String(response.getBody(), StandardCharsets.UTF_8));
                    }
                })
                .withAuthorizationBearer(token)
                .withFailureCallback(this::handleLoymaxErrors)
                .get(loymaxSocial.getSetUrl() + "?code=" + socialSetDto.getCode()
                        + "&" + "redirect_uri=" + loymaxSocial.buildSetPageUrl());
    }

    public void userRemoveSocial(LoymaxSocial loymaxSocial, String token) {
        String url = loymaxSocial.getRemoveSocialUrl();
        if (loymaxSocial.getSocialName().equals("vk")) url = url.replace("vk", "Vkontakte");
        restTemplate.request()
                .withMessageProcessor(new MessageProcessor() {
                    public void onSuccess(RestResponse response) throws RestTemplateException {
                    }

                    public void onFailure(RestResponse response) throws RestTemplateException {
                        throw new LoymaxException(new String(response.getBody(), StandardCharsets.UTF_8));
                    }
                })
                .withAuthorizationBearer(token)
                .withFailureCallback(this::handleLoymaxErrors)
                .post(url);
    }

    public List<PromotionsDto> getPromotions(Integer offset, Integer limit) {
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .setResponseType(
                        new TypeReference<LoymaxDataResultResponse<List<PromotionsDto>>>() {
                        })
                .withConvertException(this::handleLoymaxConvertException)
                .getForEntity(loymaxConstants.getPromotionsUrl() + "?filter.type=Original" +
                        "&&" + "filter.from=" + offset + "&&" + "filter.count=" + limit)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No response response from loymax", 401));
        validateResponseState(response);
        return response.getData();
    }

    public TokenExchangeResponse refreshToken(String refreshToken, String bearerToken) {
        String body = "grant_type=refresh_token&refresh_token=" + refreshToken;
        return restTemplate.request()
                .withContentType(RestTemplateConstants.MediaTypes.APPLICATION_FORM_URLENCODED)
                .withAuthorizationHeader(AuthType.BEARER, bearerToken)
                .withFailureCallback(this::handleLoymaxErrors)
                .setResponseType(TokenExchangeResponse.class)
                .withConvertException(this::handleLoymaxConvertException)
                .postForEntity(loymaxConstants.refreshToken, body)
                .orElseThrow(() -> new UnauthorizedException("Cannot refresh token",
                        LOYMAX_UNAUTHORIZED_EXCEPTION_CODE));
    }

    public void updateUserEmail(String token, EmailDto emailDto) {
        var result = restTemplate.request()
                .withAuthorizationBearer(token)
                .withFailureCallback(this::handleLoymaxErrors)
                .setResponseType(new TypeReference<LoymaxDataResultResponse>() {
                })
                .withConvertException(this::handleLoymaxConvertException)
                .postForEntity(loymaxConstants.updateEmial, emailDto)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot update email"));
        validateResponseState(result);
    }

    public void confirmUserEmail(String token, LoymaxConfirmCodeDto codeDto) {
        var result = restTemplate.request()
                .withAuthorizationBearer(token)
                .withFailureCallback(this::handleLoymaxErrors)
                .setResponseType(new TypeReference<LoymaxDataResultResponse>() {
                })
                .withConvertException(this::handleLoymaxConvertException)
                .postForEntity(loymaxConstants.updateEmailConfirm, codeDto)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot confirm email"));
        validateResponseState(result);
    }

    public void sendEmailConfirmCode(String token) {
        var result = restTemplate.request()
                .withAuthorizationBearer(token)
                .withFailureCallback(this::handleLoymaxErrors)
                .setResponseType(new TypeReference<LoymaxDataResultResponse>() {
                })
                .withConvertException(this::handleLoymaxConvertException)
                .postForEntity(loymaxConstants.updateEmailSendConfirmCode)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot send confirm code"));
        validateResponseState(result);
    }

    public void cancelEmailProcessing(String token) {
        var result = restTemplate.request()
                .withAuthorizationBearer(token)
                .withFailureCallback(this::handleLoymaxErrors)
                .setResponseType(new TypeReference<LoymaxDataResultResponse>() {
                })
                .withConvertException(this::handleLoymaxConvertException)
                .postForEntity(loymaxConstants.cancelEmailProcessing)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot cancel email processing"));
        validateResponseState(result);
    }

    public void updateUserPhone(PhoneDto phoneDto, String token) {
        var result = restTemplate.request()
                .withAuthorizationBearer(token)
                .withFailureCallback(this::handleLoymaxErrors)
                .setResponseType(new TypeReference<LoymaxDataResultResponse>() {
                })
                .withConvertException(this::handleLoymaxConvertException)
                .postForEntity(loymaxConstants.updatePhone, phoneDto)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot update phone number"));
        validateResponseState(result);
    }

    public void cancelUserPhoneUpdating(String token) {
        var result = restTemplate.request()
                .withAuthorizationHeader(AuthType.BEARER, token)
                .withFailureCallback(this::handleLoymaxErrors)
                .setResponseType(new TypeReference<LoymaxDataResultResponse>() {
                })
                .withConvertException(this::handleLoymaxConvertException)
                .postForEntity(loymaxConstants.cancelPhoneUpdating)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot cancel phone number updating"));
        validateResponseState(result);
    }

    public TokenExchangeResponse confirmUserPhone(String token, LoymaxConfirmCodeDto codeDto) {
        var result = restTemplate.request()
                .withAuthorizationBearer(token)
                .withFailureCallback(this::handleLoymaxErrors)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<TokenExchangeResponse>>() {
                })
                .withConvertException(this::handleLoymaxConvertException)
                .postForEntity(loymaxConstants.updatePhoneConfirm, codeDto)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot confirm phone number"));
        validateResponseState(result);
        return result.getData();
    }

    public void sendPhoneConfirmCode(String token) {
        var result = restTemplate.request()
                .withAuthorizationBearer(token)
                .withFailureCallback(this::handleLoymaxErrors)
                .setResponseType(new TypeReference<LoymaxDataResultResponse>() {
                })
                .withConvertException(this::handleLoymaxConvertException)
                .postForEntity(loymaxConstants.updatePhoneSendConfirmCode)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot send confirm code"));
        validateResponseState(result);
    }

    public LoymaxUserLoginDto getSocialBindings(String token) {
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<LoymaxUserLoginDto>>() {
                })
                .getForEntity(loymaxConstants.userLogins)
                .orElseThrow(() -> new LoymaxException("Loymax not respond with user social bindings"));
        validateResponseState(response);
        return response.getData();
    }

    public void attachUserCard(String token, LoymaxUserCardRequestDto userCardDto) {
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<LoymaxUserLoginDto>>() {
                })
                .postForEntity(loymaxConstants.userCardAttach, userCardDto)
                .orElseThrow(() -> new LoymaxException("Loymax not respond with attach user card request"));
        validateResponseState(response);
    }

    public void createLoymaxUserCard(String token, LoymaxUserCardRequestDto loymaxUserCardRequestDto) {
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse>() {
                })
                .postForEntity(loymaxConstants.userCardSet, loymaxUserCardRequestDto)
                .orElseThrow(() -> new LoymaxException("Loymax not respond with user card data"));
        validateResponseState(response);
    }

    public LoymaxCardSetStatus getCreateLoymaxUserCard(String token, LoymaxUserCardRequestDto loymaxUserCardRequestDto) {
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<LoymaxCardSetStatus>>() {
                })
                .getForEntity(loymaxConstants.userCardSet)
                .orElseThrow(() -> new LoymaxException("Loymax not respond with user card data"));
        validateResponseState(response);
        return response.getData();
    }

    public List<LoymaxUserCardDto> selectCards(String token) {
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<List<LoymaxUserCardDto>>>() {
                })
                .getForEntity(loymaxConstants.selectCards)
                .orElseThrow(() -> new LoymaxException("Loymax not respond with attach user card request"));
        validateResponseState(response);
        return response.getData();
    }

    public LoymaxUserVirtualCardEmitInfo getEmitInfo(String token) {
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<LoymaxUserVirtualCardEmitInfo>>() {
                })
                .getForEntity(loymaxConstants.emitVirtual)
                .orElseThrow(() -> new LoymaxException("Loymax not respond with card emit info request"));
        validateResponseState(response);
        return response.getData();
    }

    public void emitVirtual(String token) {
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse>() {
                })
                .putForEntity(loymaxConstants.emitVirtual)
                .orElseThrow(() -> new LoymaxException("Loymax not respond with card emit info request"));
        validateResponseState(response);
    }

    public void confirmAttachUserCard(String token, String code) {
        var response = restTemplate.request()
                .withContentType(RestTemplateConstants.MediaTypes.APPLICATION_JSON)
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse>() {
                })
                .postForEntity(loymaxConstants.cardAttachConfirm, code)
                .orElseThrow(() -> new LoymaxException("Loymax not respond with confirm attach card request"));
        validateResponseState(response);
    }

    public LoymaxCardAttachStatusDto checkCurrentCardStatus(String token) {
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<LoymaxCardAttachStatusDto>>() {
                })
                .getForEntity(loymaxConstants.cardCheckAttachStatus)
                .orElseThrow(() -> new LoymaxException("Loymax not respond with check attach status request"));
        validateResponseState(response);
        return response.getData();
    }

    public void attachSendConfirmCode(String token) {
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse>() {
                })
                .postForEntity(loymaxConstants.attachSendConfirmCode)
                .orElseThrow(() -> new LoymaxException("Loymax not respond with card emit info request"));
        validateResponseState(response);
    }

    public List<LoymaxUserBalanceInfoDto> getDetailedBalanceInfo(String token) {
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<LoymaxUserBalanceInfoItemsDto>>() {
                })
                .getForEntity(loymaxConstants.userBalanceInfo)
                .orElseThrow(() -> new LoymaxException("Loymax not respond with balance info request"));
        validateResponseState(response);
        return response.getData().getItems();
    }

    public LoymaxUserFavoriteCategories selectFavoriteCategories(String token) {
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<LoymaxUserFavoriteCategories>>() {
                })
                .getForEntity(loymaxConstants.userFavoriteCategories)
                .orElseThrow(() -> new LoymaxException("Loymax not respond with user favorite category info request"));
        var state = getLoymaxDataResultState(response);
        if (!isStateSuccess(state) && response.getResult().getMessage().equals("Не найдено значение атрибута.")) {
            return null;
        }
        validateResponseState(response);

        return response.getData();
    }

    public LoymaxCheck selectUserChecks(String token) {

        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<LoymaxCheck>>() {
                })
                .getForEntity(loymaxConstants.userChecks + "?filter.historyItemType=Purchase")
                .orElseThrow(() -> new LoymaxException("Loymax not respond with user favorite category info request"));
        validateResponseState(response);
        return response.getData();
    }

    public List<LoymaxUserStatus> selectUserStatus(String token) {
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<List<LoymaxUserStatus>>>() {
                })
                .getForEntity(loymaxConstants.selectUserStatus)
                .orElseThrow(() -> new LoymaxException("Loymax not respond with user favorite category info request"));
        validateResponseState(response);
        return response.getData();
    }

    public LoymaxCardReplaceResponse replaceUserCard(String token,
                                                     LoymaxUserCardRequestDto loymaxCardRequest,
                                                     String loymaxCardId) {
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<LoymaxCardReplaceResponse>>() {
                })
                .postForEntity(PathVariableUtils.replacePathVariable(
                        loymaxConstants.cardReplace, loymaxCardId), loymaxCardRequest)
                .orElseThrow(() -> new LoymaxException("Loymax not respond with replace card request"));
        validateResponseState(response);
        return response.getData();
    }

    public void changeBlockState(String token, String loymaxCardId, CardPasswordDto cardPasswordDto) {
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse>() {
                })
                .postForEntity(PathVariableUtils.replacePathVariable(
                                loymaxConstants.cardChangeBlockState, loymaxCardId),
                        "\"" + cardPasswordDto.getPassword() + "\"")
                .orElseThrow(() -> new LoymaxException("Loymax not respond with replace card request"));
        validateResponseState(response);
    }

    public LoymaxUserCardQrDto getQrCode(String token, String loymaxCardId) {
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<LoymaxUserCardQrDto>>() {
                })
                .getForEntity(PathVariableUtils.replacePathVariable(
                        loymaxConstants.getQrCode, loymaxCardId))
                .orElseThrow(() -> new LoymaxException("Loymax does not respond to the request to receive the qr code"));

        validateResponseState(response);
        return response.getData();
    }

    public List<LoymaxCouponsDto> getCouponsList(String token) {
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<List<LoymaxCouponsDto>>>() {
                })
                .getForEntity(loymaxConstants.getCoupons)
                .orElseThrow(() -> new LoymaxException("Loymax does not respond to the request to get coupons list"));
        validateResponseState(response);
        return response.getData();
    }

    public List<LoymaxAttributesDto> getAttributeList(String token) {
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<List<LoymaxAttributesDto>>>() {
                })
                .getForEntity(loymaxConstants.getAttribute)
                .orElseThrow(() -> new LoymaxException("Loymax does not respond to the request to receive the qr code"));

        validateResponseState(response);
        return response.getData();
    }

    public ObjectNode getClientPersonalOffers(String token, String logicalName) {
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<ObjectNode>>() {
                })
                .getForEntity(PathVariableUtils.replacePathVariable(
                        loymaxConstants.getPersonalOffer, logicalName))
                .orElseThrow(() -> new LoymaxException("Loymax does not respond to the request to receive the qr code"));

        validateResponseState(response);
        return response.getData();
    }

    public LoymaxPersonalGoodsResponseDto getClientPersonalGoods(String token, String logicalName) {
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<LoymaxPersonalGoodsResponseDto>>() {
                })
                .getForEntity(PathVariableUtils.replacePathVariable(
                        loymaxConstants.getPersonalOffer, logicalName))
                .orElseThrow(() -> new LoymaxException("Loymax does not respond to the request to receive the qr code"));
        var state = getLoymaxDataResultState(response);
        if (!isStateSuccess(state) && response.getResult().getMessage().equals("Не найдено значение атрибута.")) {
            return null;
        }
        validateResponseState(response);
        return response.getData();
    }

    public void acceptedToGoods(String token, LoymaxAcceptGoodsRequest loymaxAcceptGoodsRequest) {
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse>() {
                })
                .postForEntity(loymaxConstants.acceptGoods, loymaxAcceptGoodsRequest)
                .orElseThrow(() -> new LoymaxException("Loymax not respond with user favorite category info request"));

        validateResponseState(response);
    }

    public LoymaxCheck selectUserChecksWithProperty(String token, String request) {
        var responseCard = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<LoymaxCheck>>() {
                })
                .getForEntity(request)
                .orElseThrow(() -> new LoymaxException("Loymax not respond with user favorite category info request"));
        validateResponseState(responseCard);
        return responseCard.getData();
    }

    public LoymaxCheck selectUserChecksFromAdmin(String token, String request) {
        var responseCard = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<LoymaxCheck>>() {
                })
                .getForEntity(request)
                .orElseThrow(() -> new LoymaxException("Loymax not respond with user favorite category info request"));
        validateResponseState(responseCard);
        return responseCard.getData();
    }

    public void selectFavoriteGoods(String token, LoymaxAcceptCategoryRequest ids) {
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse>() {
                })
                .postForEntity(loymaxConstants.selectFavoriteCategory, ids)
                .orElseThrow(() -> new LoymaxException("Loymax not select favorite category"));

        validateResponseState(response);
    }

    public Boolean findRejectPaper(String token) {
        var loymaxUserQuestions = restTemplate.request()
                .withAuthorizationBearer(token)
                .setResponseType(LoymaxQuestionDataRoot.class).getForEntity(loymaxConstants.userQuestions);

        try {
            var result = loymaxUserQuestions.get().getData().get(1).getQuestions()
                    .stream().filter(x -> x.getLogicalName().equals("eCheque"))
                    .findFirst().get().getAnswer().getValue();
            return Boolean.valueOf(result);
        } catch (NoSuchElementException | NullPointerException e) {
            log.error("Loymax user reject paper not found");
            throw new ResourceNotFoundException("reject paper not found");
        }
    }

    public void mobileApplicationInstalled(String token, LoymaxAcceptGoodsRequest loymaxAcceptGoodsRequest) {
        var response = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationBearer(token)
                .setResponseType(new TypeReference<LoymaxDataResultResponse>() {
                })
                .postForEntity(loymaxConstants.setMobile, loymaxAcceptGoodsRequest)
                .orElseThrow(() -> new LoymaxException("Loymax not respond with user set mobile request"));

        validateResponseState(response);
    }

    public LoymaxCounterValueResponse fetchLoymaxCounter(String adminToken, Integer counterId, Long loymaxPersonId) {
        var url = loymaxConstants.counterValue.replace("{counterId}", String.valueOf(counterId)) + "?filter.personId=" + loymaxPersonId;
        var response = restTemplate.request()
                .withAuthorizationHeader(AuthType.BEARER, adminToken)
                .withFailureCallback(this::handleLoymaxErrors)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<LoymaxCounterValueResponse>>() {
                })
                .getForEntity(url)
                .orElseThrow(this::throwUnexpectedEmptyResponseException);
        validateResponseState(response);
        return response.getData();
    }

    public List<LoymaxUserAttributeDto<String>> importUserAttribute(String adminToken, Long loymaxUserId, String attributeName) {
        var url = loymaxConstants.getGetAttributeValue().replace("{userId}", String.valueOf(loymaxUserId)) +
                "?filter.from=0&filter.count=50";
        var resp = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationHeader(AuthType.BEARER, adminToken)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<List<LoymaxUserAttributeDto<String>>>>() {
                })
                .getForEntity(url)
                .orElseThrow(this::throwUnexpectedEmptyResponseException);
        validateResponseState(resp);
        return resp.getData();
    }

    public List<LoymaxUserInfoSystem> getUserInfo(String adminToken, String phoneNumber) {
        var url = loymaxConstants.getUserInfo() + "?phone=" + phoneNumber;
        var resp = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationHeader(AuthType.BEARER, adminToken)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<List<LoymaxUserInfoSystem>>>() {})
                .getForEntity(url)
                .orElseThrow(this::throwUnexpectedEmptyResponseException);
        validateResponseState(resp);
        return resp.getData();
    }

    public LoymaxUserInfoSystem getUserInfoByUserId(String adminToken, String loymaxUserId) {
        var url = loymaxConstants.getUserInfo() + "/" + loymaxUserId;
        var resp = restTemplate.request()
                .withFailureCallback(this::handleLoymaxErrors)
                .withAuthorizationHeader(AuthType.BEARER, adminToken)
                .setResponseType(new TypeReference<LoymaxDataResultResponse<LoymaxUserInfoSystem>>() {})
                .getForEntity(url)
                .orElseThrow(this::throwUnexpectedEmptyResponseException);
        validateResponseState(resp);
        return resp.getData();
    }

    private RuntimeException throwUnexpectedEmptyResponseException() {
        return new LoymaxException("Unexpected empty loymax response");
    }

    private <T> String getLoymaxDataResultState(LoymaxDataResultResponse<T> dataResultResponse) {
        if (dataResultResponse.getResult() != null) {
            var res = dataResultResponse.getResult();
            return res.getState();
        }
        return LoymaxConstants.RESULT_RESPONSE_NO_STATE;
    }

    private void handleLoymaxErrors(RestResponse response) {
        String msg = new String(response.getBody(), StandardCharsets.UTF_8);
        log.error("loymax respond with code: " + response.getCode() + ". Details: " + msg);
        if (response.getCode() == 401 || response.getCode() == 400) {
            log.error("Loymax refresh token expired");
            throw new LoymaxUnauthorizedException("Внутренняя ошибка сервера. Пожалуйста выполните вход еще раз",
                    LOYMAX_UNAUTHORIZED_EXCEPTION_CODE);
        }
        throw new LoymaxException("Loymax bad response: " + msg);
    }

    private RuntimeException handleLoymaxConvertException(RestResponse response) {
        return new LoymaxException("Unknown loymax response: " + new String(response.getBody(), StandardCharsets.UTF_8));
    }

    private boolean isStateSuccess(String state) {
        return state.equals(LoymaxConstants.RESULT_RESPONSE_STATE_SUCCESS);
    }

    // TODO parse validation error state
    // TODO parse other states
    private void validateResponseState(LoymaxDataResultResponse<?> loymaxResponse) {
        var state = getLoymaxDataResultState(loymaxResponse);
        if (!isStateSuccess(state)) {
            log.error(String.format("Loymax respond with an error: [ state: %s, || message: %s || full: %s ]",
                    loymaxResponse.getResult().getState(), loymaxResponse.getResult().getMessage(), loymaxResponse));
            throw new LoymaxException(loymaxResponse.getResult().getMessage());
        }
    }

    private <T, X> T defaultLoymaxRequest(String url, X body, String token,
                                          TypeReference<T> responseObject, String... exceptionMessage) {
        return restTemplate.request()
                .withAuthorizationBearer(token)
                .withFailureCallback(this::handleLoymaxErrors)
                .setResponseType(responseObject)
                .withConvertException(this::handleLoymaxConvertException)
                .postForEntity(url, body)
                .orElseThrow(() -> new LoymaxException(
                        exceptionMessage != null ? String.join(" ", exceptionMessage) :
                                "loymax error"));
    }

    private TokenExchangeResponse baseExchangeForToken(String body) {
        return restTemplate.request()
                .withContentType(RestTemplateConstants.MediaTypes.APPLICATION_FORM_URLENCODED)
                .withFailureCallback(this::handleLoginException)
                .setResponseType(TokenExchangeResponse.class)
                .withConvertException(this::handleLoymaxConvertException)
                .postForEntity(loymaxConstants.getTokenExchangeUrl(), body)
                .orElseThrow(() -> new UnauthorizedException("Wrong login or password", 401));
    }

}