package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.sparural.engine.api.dto.check.CheckDto;
import ru.sparural.engine.api.dto.check.CheckNotificationInfoDto;
import ru.sparural.engine.api.dto.common.LongList;
import ru.sparural.engine.api.dto.user.UserFilterDto;
import ru.sparural.engine.api.dto.user.UserNotificationInfoDto;
import ru.sparural.engine.api.dto.user.UserPushTokenDto;
import ru.sparural.engine.entity.LoymaxCard;
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

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@RequiredArgsConstructor
@Slf4j
public class CheckController {

    private final ExecutorService executor = Executors.newFixedThreadPool(10);
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
        var loymaxCard = new LoymaxCard();
        if (cardId != 0L) {
            loymaxCard = loymaxCardService.findByLocalCardId(cardId);
        }
        loymaxService.refreshTokenIfNeeded(loymaxUser);
        var loymaxChecksList = loymaxService.getChecksList(loymaxUser, loymaxCard, offset, limit, dateStart, dateEnd);
        if (loymaxChecksList == null) {
            return null;
        }
        return checkService.processingSaveOrUpdateChecks(loymaxChecksList, userId);
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
    public void loadChecksForUser(@Payload UserFilterDto userFilterDto) {
        var users = userFilterDto.getUserIds();
        users.forEach(user -> executor.submit(() -> {
            checkService.loadChecksForUser(user);
        }));
    }
}
