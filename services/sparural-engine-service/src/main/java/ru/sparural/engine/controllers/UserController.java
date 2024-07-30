package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.sparural.engine.api.dto.CodeDto;
import ru.sparural.engine.api.dto.DeregistrationConfirm;
import ru.sparural.engine.api.dto.EmailDto;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.PhoneDto;
import ru.sparural.engine.api.dto.PushTokenReqDto;
import ru.sparural.engine.api.dto.RejectPaperCheckDto;
import ru.sparural.engine.api.dto.UserSearchFilterDto;
import ru.sparural.engine.api.dto.common.LongList;
import ru.sparural.engine.api.dto.registration.UserProfileUpdateRequest;
import ru.sparural.engine.api.dto.user.ChangePasswordRequestDto;
import ru.sparural.engine.api.dto.user.LoymaxUserDto;
import ru.sparural.engine.api.dto.user.RoleDto;
import ru.sparural.engine.api.dto.user.UserDto;
import ru.sparural.engine.api.dto.user.UserFilterDto;
import ru.sparural.engine.api.dto.user.UserNotificationInfoDto;
import ru.sparural.engine.api.dto.user.UserProfileDto;
import ru.sparural.engine.api.dto.user.UserPushTokenDto;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.entity.Role;
import ru.sparural.engine.entity.User;
import ru.sparural.engine.loymax.rest.dto.LoymaxConfirmCodeDto;
import ru.sparural.engine.loymax.rest.dto.registration.DeregistrationConfirmDto;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.services.FileDocumentService;
import ru.sparural.engine.services.UserDeleteService;
import ru.sparural.engine.services.UserPushTokenService;
import ru.sparural.engine.services.UserService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.ValidationException;
import ru.sparural.engine.utils.DtoMapperUtils;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;
import ru.sparural.kafka.model.ServiceResponse;
import ru.sparural.utils.ReflectUtils;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@Slf4j
public class UserController {

    private final DtoMapperUtils dtoMapperUtils;
    private final UserService userService;
    private final LoymaxService loymaxService;
    private final UserPushTokenService userPushTokenService;
    private final FileDocumentService fileDocumentService;
    private final UserDeleteService userDeleteService;

    @KafkaSparuralMapping("user/deregistration-send-confirm-code")
    public Boolean unregisterSendConfirmCode(@RequestParam Long userId) {
        var user = loymaxService.getByLocalUserId(userId);
        loymaxService.deregistrationBegin(user);
        return true;
    }

    @KafkaSparuralMapping("user/deregistration-confirm")
    public Boolean unregisterConfirm(@RequestParam Long userId,
                                     @Payload DeregistrationConfirm dto) {
        var user = loymaxService.getByLocalUserId(userId);
        var loymaxDto = new DeregistrationConfirmDto();
        loymaxDto.setConfirmCode(dto.getCode());
        loymaxDto.setReason(dto.getMessage());
        loymaxService.deregistrationConfirm(user, loymaxDto);
        userDeleteService.deregistration(dto, user.getUserId());
        return true;
    }

    @KafkaSparuralMapping("users/notifications-by-filter")
    public List<UserNotificationInfoDto> userNotificationsByFilter(@Payload UserFilterDto filter) {
        return userService.usersByFilter(filter)
                .stream().map(user -> {
                    var userNotification = new UserNotificationInfoDto();
                    userNotification.setUser(user);
                    userNotification.setTokens(dtoMapperUtils.convertList(UserPushTokenDto.class, userPushTokenService.getAllByUserId(user.getId())));
                    return userNotification;
                }).collect(Collectors.toList());
    }

    @KafkaSparuralMapping("user/listByIds")
    public List<UserDto> getUsersByIds(@Payload LongList userIds) {
        return userService.usersByIds(userIds.getList());
    }

    @KafkaSparuralMapping("user/change-reject-paper-checks")
    public UserProfileDto rejectPaperChecks(@RequestParam Long userId,
                                            @Payload RejectPaperCheckDto rejectPaperCheckDto) {
        var user = userService.findById(userId);
        user.setRejectPaperChecks(rejectPaperCheckDto.getRejectPaperChecks());
        userService.update(user);
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.changeRejectPaperChecks(rejectPaperCheckDto.getRejectPaperChecks(), loymaxUser);
        UserProfileDto userProfileDto = userService.getUserProfileInfo(userId);
        List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.USER_PHOTO, userProfileDto.getId());
        if (!files.isEmpty()) {
            userProfileDto.setPhoto(files.get(files.size() - 1));
        }
        return userProfileDto;
    }

    @KafkaSparuralMapping("user/get")
    public UserProfileDto getLoymaxUserData(@RequestParam Long userId) {
        ServiceResponse serviceResponse = new ServiceResponse();
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        var userInfo = loymaxService.getUserInfo(loymaxUser);
        var loymaxData = userService.mapLoymaxInfoToUser(userInfo);
        var u = userService.getUserProfileInfo(userId);
        ReflectUtils.updateNotNullFields(loymaxData, u);
        serviceResponse.setBody(u);
        List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.USER_PHOTO, u.getId());
        if (!files.isEmpty()) {
            u.setPhoto(files.get(files.size() - 1));
        }
        return u;
    }

    @KafkaSparuralMapping("user/update-email")
    public Boolean updateUserEmail(@Payload EmailDto emailDto,
                                   @RequestParam Long userId) {
        if (userService.checkIfUserExistsWithPhoneOrEmail(emailDto.getEmail())) {
            throw new ValidationException("Электронный адрес уже существует");
        }

        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);
        loymaxService.cancelEmailProcessing(loymaxUser);
        userService.removeAllUnconfirmedEmailRecordByUser(userId);
        loymaxService.updateUserEmail(emailDto, loymaxUser);

        try {
            userService.createUnconfirmedEmailRecord(userId, emailDto.getEmail());
        } catch (Exception exception) {
            throw new ValidationException("Электронный адрес уже существует");
        }
        return true;
    }

    @KafkaSparuralMapping("user/email-confirm")
    public Boolean confirmUserEmail(@Payload CodeDto codeDto,
                                    @RequestParam Long userId) {
        LoymaxConfirmCodeDto loymaxConfirmCodeDto = new LoymaxConfirmCodeDto();
        loymaxConfirmCodeDto.setConfirmCode(codeDto.getCode());

        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);
        loymaxService.confirmUserEmail(loymaxUser, loymaxConfirmCodeDto);

        var entity = userService.getUnconfirmedEmailRecord(userId);
        var user = userService.findByUserId(entity.getUserId());
        user.setEmail(entity.getEmail());
        userService.update(user);
        userService.removeUnconfirmedRecordByEmail(entity.getEmail());
        return true;
    }

    @KafkaSparuralMapping("user/email-send-confirm-code")
    public Boolean sendConfirmUserEmail(@RequestParam Long userId) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);
        loymaxService.sendEmailConfirmCode(loymaxUser);
        return true;
    }

    @KafkaSparuralMapping("user/update-phone-number")
    public Boolean updateUserPhone(@Payload PhoneDto phoneDto,
                                   @RequestParam Long userId) {
        if (userService.checkIfUserExistsWithPhoneOrEmail(phoneDto.getPhoneNumber())) {
            throw new ValidationException("Номер телефона уже существует");
        }
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);
        try {
            userService.createUnconfirmedPhoneRecord(phoneDto.getPhoneNumber(), userId);
            loymaxService.updateUserPhone(loymaxUser, phoneDto);
        } catch (Exception exception) {
            log.error("Unexpected phone update exception", exception);
            throw new ValidationException("Не удалось обновить номер телефона");
        }
        return true;
    }

    @KafkaSparuralMapping("user/cancel-update-phone-number")
    public Boolean cancelUpdatingPhoneNumber(@RequestParam Long userId) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.cancelUpdatingPhoneNumber(loymaxUser);
        return true;
    }

    @KafkaSparuralMapping("user/phone-confirm")
    public Boolean confirmUserPhone(@Payload CodeDto codeDto,
                                    @RequestParam Long userId) {
        LoymaxConfirmCodeDto loymaxConfirmCodeDto = new LoymaxConfirmCodeDto();
        loymaxConfirmCodeDto.setConfirmCode(codeDto.getCode());

        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);
        loymaxService.confirmUserPhone(loymaxUser, loymaxConfirmCodeDto);

        var entity = userService.getLastUnconfirmedPhoneRecord(userId);
        var user = userService.findByUserId(entity.getUserId());
        user.setPhoneNumber(entity.getPhoneNumber());
        userService.update(user);
        userService.removeUnconfirmedRecordByPhone(entity.getPhoneNumber());
        return true;
    }

    @KafkaSparuralMapping("user/phone-send-confirm-code")
    public Boolean sendConfirmUserPhone(@RequestParam Long userId) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);
        loymaxService.sendPhoneConfirmCode(loymaxUser);
        return true;
    }

    @KafkaSparuralMapping("user/save-push-token")
    public PushTokenReqDto savePushToken(@Payload PushTokenReqDto pushTokenReqDto,
                                         @RequestParam Long userId) {
        if (userId == 0) {
            try {
                userPushTokenService.getByToken(pushTokenReqDto.getToken());
            } catch (ResourceNotFoundException e) {
                var anon = userService.createAnonymous();
                userPushTokenService.bindAsyncPushTokenToUser(anon.getId(), pushTokenReqDto);
            }
        } else {
            try {
                userPushTokenService
                        .findByUserIdAndToken(userId, pushTokenReqDto.getToken());
            } catch (ResourceNotFoundException e) {
                try {
                    var token = userPushTokenService.getByToken(pushTokenReqDto.getToken());
                    userPushTokenService.deleteById(token.getId());
                    userService.deleteAsync(token.getUserid());
                } catch (ResourceNotFoundException r) { /* ignore */ }
                userPushTokenService.bindAsyncPushTokenToUser(userId, pushTokenReqDto);
            }
        }
        return pushTokenReqDto;
    }

    @KafkaSparuralMapping("user/roles")
    public List<RoleDto> listRoles() {
        List<Role> roles = userService.getAllRoles();
        return roles.stream().map(x -> dtoMapperUtils.convert(x, RoleDto.class))
                .collect(Collectors.toList());
    }

    @KafkaSparuralMapping("user/change-password")
    public Boolean userChangePassword(@RequestParam Long userId,
                                      @Payload ChangePasswordRequestDto requestDto) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.changePassword(loymaxUser, requestDto);
        return true;
    }

    @KafkaSparuralMapping("users/index")
    public List<UserDto> list(@Payload UserSearchFilterDto userSearchFilterDto) {
        return userService.list(userSearchFilterDto)
                .stream()
                .map(entity -> {
                    var dto = userService.createDto(entity);
                    var files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.USER_PHOTO, dto.getId());
                    if (!files.isEmpty()) {
                        dto.setPhoto(files.get(files.size() - 1));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @KafkaSparuralMapping("users/get")
    public UserDto get(@RequestParam Long id) {
        var dto = userService.createDto(userService.findById(id));
        List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.USER_PHOTO, dto.getId());
        if (!files.isEmpty()) {
            dto.setPhoto(files.get(files.size() - 1));
        }
        return dto;
    }

    @KafkaSparuralMapping("users/create")
    public UserDto create(@Payload UserDto user) {
        if (userService.checkIfUserExistsWithPhoneOrEmail(user.getPhoneNumber(), user.getEmail())) {
            throw new ValidationException("Номер телефона или электронный адрес уже существует");
        }
        var u = userService.createUserFromDto(user);
        var updated = userService.saveOrUpdate(u);
        return userService.createDto(updated);
    }

    @KafkaSparuralMapping("users/update")
    public UserDto update(@Payload UserDto userRequestDto,
                          @RequestParam Long id) {
        if (userRequestDto.getPhoneNumber().isBlank()) {
            throw new ValidationException("Укажите номер телефона");
        }
        if (userService.checkIfUserExistsWithPhoneOrEmail(userRequestDto.getPhoneNumber(), userRequestDto.getEmail(), id)) {
            throw new ValidationException("Номер телефона или электронный адрес уже существует");
        }
        userRequestDto.setId(id);
        var user = userService.createUserFromDto(userRequestDto);
        var u = userService.update(user);
        return userService.createDto(userService.findByUserId(u.getId()));
    }

    @KafkaSparuralMapping("user/update")
    public UserProfileDto updateUserProfile(@Payload UserProfileUpdateRequest userProfileUpdateRequest,
                                            @RequestParam Long userId) {
        if (userService.checkIfUserExistsWithPhoneOrEmail(userProfileUpdateRequest.getEmail())) {
            throw new ValidationException("Электронный адрес уже существует");
        }
        var user = userService.findByUserId(userId);

        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);
        loymaxService.sendUserUpdateRequest(userProfileUpdateRequest, loymaxUser, "update");
        UserProfileDto userProfileDto = new UserProfileDto();
        User result = userService.updateUserData(userProfileUpdateRequest, user);
        ReflectUtils.updateNotNullFields(result, userProfileDto);
        List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.USER_PHOTO, userProfileDto.getId());
        if (!files.isEmpty()) {
            userProfileDto.setPhoto(files.get(files.size() - 1));
        }
        return userProfileDto;
    }

    @KafkaSparuralMapping("users/delete")
    public Boolean delete(@RequestParam Long id) {
        return userService.delete(id);
    }

    @KafkaSparuralMapping("users/extract")
    public List<Long> extract(@RequestParam List<Long> users, @RequestParam String userGroup) {
        return userService.extract(users, userGroup);
    }

    @KafkaSparuralMapping("user/lastActivity")
    public void setLastActivity(@RequestParam Long userId) {
        userService.updateLastActivity(userId);
    }

    @KafkaSparuralMapping("users/count")
    public Long countUsersByFilter(@Payload UserSearchFilterDto filter) {
        return userService.usersCount(filter);
    }

    @KafkaSparuralMapping("users/index-loymax")
    public List<LoymaxUserDto> fetchAllLoymaxUser() {
        return loymaxService.fetchAllUsers()
                .stream()
                .map(loymaxUser -> {
                    var dto = new LoymaxUserDto();
                    dto.setLoymaxUserId(loymaxUser.getLoymaxUserId());
                    return dto;
                }).collect(Collectors.toList());
    }

    @KafkaSparuralMapping("users/import-user-attribute")
    public void importUserAttribute(@RequestParam Long loymaxUserId, @RequestParam String attributeName) {
        var attribute = loymaxService.importUserAttribute(loymaxUserId, attributeName);

    }
}
