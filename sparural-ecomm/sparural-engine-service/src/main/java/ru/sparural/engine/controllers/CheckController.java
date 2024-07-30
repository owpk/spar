package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.sparural.engine.api.dto.LoymaxUsersDto;
import ru.sparural.engine.api.dto.check.CheckDto;
import ru.sparural.engine.api.dto.check.CheckNotificationInfoDto;
import ru.sparural.engine.api.dto.common.LongList;
import ru.sparural.engine.api.dto.user.UserFilterDto;
import ru.sparural.engine.api.dto.user.UserNotificationInfoDto;
import ru.sparural.engine.api.dto.user.UserPushTokenDto;
import ru.sparural.engine.entity.CheckEntity;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.services.CheckService;
import ru.sparural.engine.services.LoymaxCardService;
import ru.sparural.engine.services.UserPushTokenService;
import ru.sparural.engine.services.UserService;
import ru.sparural.engine.utils.DtoMapperUtils;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@RequiredArgsConstructor
@Slf4j
public class CheckController {

    private final CheckService service;
    private final LoymaxService loymaxService;
    private final CheckService checkService;
    private final LoymaxCardService loymaxCardService;
    private final UserService userService;
    private final UserPushTokenService userPushTokenService;
    private final DtoMapperUtils dtoMapperUtils;

    @KafkaSparuralMapping("checks/get")
    public CheckDto get(@RequestParam Long id,
                        @RequestParam Long userId) {
        return service.get(id, userId);
    }

    @KafkaSparuralMapping("checks/index")
    public List<CheckDto> index(@RequestParam Long userId,
                                @RequestParam Integer offset,
                                @RequestParam Integer limit,
                                @RequestParam Long cardId,
                                @RequestParam Long dateStart,
                                @RequestParam Long dateEnd) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        var foundedLoymaxCardId = 0L;
        if (cardId != 0L) {
            var loymaxCard = loymaxCardService.findByLocalCardId(cardId);
            foundedLoymaxCardId = loymaxCard.getLoymaxCardId();
        }
        loymaxService.refreshTokenIfNeeded(loymaxUser);
        var loymaxChecksList = loymaxService.getChecksList(
                loymaxUser, foundedLoymaxCardId, offset, limit, dateStart, dateEnd);

        var entities = checkService.processingSaveOrUpdateChecks(loymaxChecksList, userId);

        return entities.stream().map(checkService::createDto)
                .sorted((check1, check2) -> Long.compare(check2.getDateTime(), check1.getDateTime()))
                .collect(Collectors.toList());
    }

    @KafkaSparuralMapping("checks/lastNotification")
    public List<CheckNotificationInfoDto> lastUserCheck(@Payload UserFilterDto filter,
                                                        @RequestParam Long startTime) {
        return checkService.getLastCheck(filter, startTime)
                .stream().map(check -> {
                    var checkNotification = new CheckNotificationInfoDto();
                    checkNotification.setCheck(checkService.createDto(check));
                    var user = userService.createDto(userService.findById(check.getUserId()));
                    var userNotification = new UserNotificationInfoDto();
                    userNotification.setUser(user);
                    userNotification.setTokens(dtoMapperUtils.convertList(UserPushTokenDto.class, userPushTokenService.getAllByUserId(user.getId())));
                    checkNotification.setUserNotificationInfo(userNotification);
                    return checkNotification;
                }).collect(Collectors.toList());
    }

    @KafkaSparuralMapping("checks/saveIsNotifCheck")
    public void saveIsNotifCheck(@Payload LongList checkId) {
        checkService.saveIsNotifCheck(checkId.getList());
    }

    @KafkaSparuralMapping("checks/merchant-id")
    public Long getMerchantIdOfLastCheck(@RequestParam Long userId) {
        return checkService.getMerchantIdOfLastCheck(userId);
    }

    @KafkaSparuralMapping("checks/load")
    public void loadCheckForUsers(@Payload LoymaxUsersDto loymaxUsersWrapper) {
        var loymaxUserIds = loymaxUsersWrapper.getLoymaxUsersIds();
        var userIdLoymaxUserId = userService.findUserIdsByLoymaxUserIds(loymaxUserIds);
        userIdLoymaxUserId.forEach(entry -> {
            var loymaxChecksList = loymaxService.loadChecksForUser(entry.getLoymaxUserId());
            checkService.processingSaveOrUpdateChecks(loymaxChecksList, entry.getUserId());
        });
    }

}
