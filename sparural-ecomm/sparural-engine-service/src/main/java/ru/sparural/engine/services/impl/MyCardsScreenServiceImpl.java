package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.AccountsLifeTimesByTimeDTO;
import ru.sparural.engine.api.dto.cards.UserCardDto;
import ru.sparural.engine.api.dto.check.CheckDto;
import ru.sparural.engine.api.dto.screen.mycards.ClientStatus;
import ru.sparural.engine.api.dto.screen.mycards.MyCardsInfoScreenDto;
import ru.sparural.engine.api.dto.screen.mycards.MyCardsInfoScreenDtoV3;
import ru.sparural.engine.api.dto.screen.mycards.Status;
import ru.sparural.engine.api.dto.user.account.UserAccounts;
import ru.sparural.engine.controllers.CheckController;
import ru.sparural.engine.controllers.FavoriteCategoriesController;
import ru.sparural.engine.entity.Card;
import ru.sparural.engine.entity.LoymaxUser;
import ru.sparural.engine.loymax.rest.dto.status.LoymaxUserStatusItem;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.services.CardsService;
import ru.sparural.engine.services.LoymaxCardService;
import ru.sparural.engine.services.MyCardsScreenService;
import ru.sparural.engine.services.UserStatusService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MyCardsScreenServiceImpl implements MyCardsScreenService {

    private final LoymaxService loymaxService;
    private final LoymaxCardService loymaxCardService;
    private final CardsService cardsService;
    private final UserStatusService userStatusService;
    private final FavoriteCategoriesController favoriteCategoriesController;
    private final CheckController checkController;

    private ExecutorService executorService;

    @PostConstruct
    private void init() {
        executorService = Executors.newCachedThreadPool();
    }

    @Override
    public MyCardsInfoScreenDtoV3 getMyCardsScreenV3(Long userId) throws ExecutionException, InterruptedException {
        var cardsInfoScreenDto = new MyCardsInfoScreenDtoV3();

        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);

        var cardsTask = executorService.submit(() -> {
            List<Card> cards = new ArrayList<>();
            try {
                cards = selectAndBindUserCards(loymaxUser, userId);
                var cardsDto = cards.stream().map(x -> cardsService.createDtoFromEntity(x, userId))
                        .collect(Collectors.toList());
                cardsInfoScreenDto.setMyCards(cardsDto);
            } catch (Exception e) {
                log.error("Loymax select cards task exception", e);
            }

            try {
                var userCard = cards.stream()
                        .filter(x -> x.getOwnerId().equals(userId))
                        .findFirst().map(x -> cardsService.createDtoFromEntity(x, userId))
                        .orElseGet(UserCardDto::new);
                List<CheckDto> checks = new ArrayList<>();
                if (cardsInfoScreenDto.getMyCards() != null) {
                    var checksForCard = checkController.index(
                            userId, 0, 1, userCard.getId(), 0L, 0L);
                    checks.addAll(checksForCard);
                }
                cardsInfoScreenDto.setChecks(checks);
            } catch (Exception e) {
                log.error("Set checks task error", e);
            }
        });

        var setStatusTask = executorService.submit(() -> {
            try {
                var status = selectAndBindUserStatus(loymaxUser);
                cardsInfoScreenDto.setStatus(status);
            } catch (Exception e) {
                log.error("Select and bind user status task exception: ", e);
            }
        });

        cardsTask.get();
        setStatusTask.get();
        return cardsInfoScreenDto;
    }

    @Override
    public MyCardsInfoScreenDto getMyCardsScreenV2(Long userId) throws ExecutionException, InterruptedException {
        MyCardsInfoScreenDto cardsInfoScreenDto = new MyCardsInfoScreenDto();

        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);

        var cardsTask = executorService.submit(() -> {
            List<Card> cards = new ArrayList<>();
            try {
                cards = selectAndBindUserCards(loymaxUser, userId);
                var cardsDto = cards.stream().map(x -> cardsService.createDtoFromEntity(x, userId))
                        .collect(Collectors.toList());
                cardsInfoScreenDto.setMyCards(cardsDto);
            } catch (Exception e) {
                log.error("Loymax select cards task exception", e);
            }

            try {
                var userCard = cards.stream()
                        .filter(x -> x.getOwnerId().equals(userId))
                        .findFirst().map(x -> cardsService.createDtoFromEntity(x, userId))
                        .orElseGet(UserCardDto::new);
                List<CheckDto> checks = new ArrayList<>();
                if (cardsInfoScreenDto.getMyCards() != null) {
                    var checksForCard = checkController.index(
                            userId, 0, 1, userCard.getId(), 0L, 0L);
                    checks.addAll(checksForCard);
                }
                cardsInfoScreenDto.setChecks(checks);
            } catch (Exception e) {
                log.error("Set checks task error", e);
            }
        });

        var setAccountTask = executorService.submit(() -> {
            try {
                var accounts = loymaxService
                        .getDetailedBalanceInfo(loymaxUser)
                        .stream()
                        .filter(x -> !x.getCurrency().getIsDeleted())
                        .collect(Collectors.toList());
                for (UserAccounts x : accounts) {
                    for (AccountsLifeTimesByTimeDTO y : x.getAccountLifeTimesByTime()) {
                        if (y.getAmount() != null) y.setAmount(Math.abs(y.getAmount()));
                    }
                }
                cardsInfoScreenDto.setAccounts(accounts);
            } catch (Exception e) {
                log.error("Loymax select account task exception", e);
            }
        });

        var setStatusTask = executorService.submit(() -> {
            try {
                var status = selectAndBindUserStatus(loymaxUser);
                cardsInfoScreenDto.setStatus(status);
            } catch (Exception e) {
                log.error("Select and bind user status task exception: ", e);
            }
        });

        cardsTask.get();
        setAccountTask.get();
        setStatusTask.get();
        return cardsInfoScreenDto;
    }

    @Override
    public MyCardsInfoScreenDto getMyCardsScreenV1(Long userId) throws ExecutionException, InterruptedException {
        MyCardsInfoScreenDto cardsInfoScreenDto = new MyCardsInfoScreenDto();

        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);

        var cardsTask = executorService.submit(() -> {
            try {
                var res1 = selectAndBindUserCards(loymaxUser, userId);
                cardsInfoScreenDto.setMyCards(
                        res1.stream().map(x -> cardsService.createDtoFromEntity(x, userId))
                                .collect(Collectors.toList()));
            } catch (Exception e) {
                log.error("Loymax select cards task exception", e);
            }

            try {
                List<CheckDto> list = new ArrayList<>();
                if (cardsInfoScreenDto.getMyCards() != null) {
                    cardsInfoScreenDto.getMyCards()
                            .forEach(x -> {
                                var checksForCard = checkController.index(
                                        userId, 0, 1, 0L, 0L, 0L);
                                list.addAll(checksForCard);
                            });
                }
                cardsInfoScreenDto.setChecks(list);
            } catch (Exception e) {
                log.error("Set checks task error", e);
            }
        });

        var setAccountTask = executorService.submit(() -> {
            try {
                var accounts = loymaxService
                        .getDetailedBalanceInfo(loymaxUser)
                        .stream()
                        .filter(x -> !x.getCurrency().getIsDeleted())
                        .collect(Collectors.toList());
                for (UserAccounts x : accounts) {
                    for (AccountsLifeTimesByTimeDTO y : x.getAccountLifeTimesByTime()) {
                        if (y.getAmount() != null) y.setAmount(Math.abs(y.getAmount()));
                    }
                }
                cardsInfoScreenDto.setAccounts(accounts);
            } catch (Exception e) {
                log.error("Loymax select account task exception", e);
            }
        });

        var setFavoriteCategoriesTask = executorService.submit(() -> {
            try {
                cardsInfoScreenDto.setFavoriteCategories(
                        favoriteCategoriesController.list(userId, 0, 30).getData());
            } catch (Exception e) {
                log.error("Set favorite cards task error: ", e);
            }
        });

        var setStatusTask = executorService.submit(() -> {
            try {
                var status = selectAndBindUserStatus(loymaxUser);
                cardsInfoScreenDto.setStatus(status);
            } catch (Exception e) {
                log.error("Select and bind user status task exception: ", e);
            }
        });

        cardsTask.get();
        setAccountTask.get();
        setFavoriteCategoriesTask.get();
        setStatusTask.get();
        return cardsInfoScreenDto;
    }

    @Override
    public List<Card> selectAndBindUserCards(LoymaxUser loymaxUser, Long userId) {
        List<UserCardDto> cards = loymaxService.selectCards(loymaxUser);

        List<UserCardDto> isOwnerList = cards.stream()
                .filter(UserCardDto::getImOwner)
                .collect(Collectors.toList());

        var res1 = cardsService.batchSaveOrUpdate(
                isOwnerList.stream().map(x -> cardsService.createEntityFromDto(x, userId))
                        .collect(Collectors.toList()));

        var bindIdsOwner = res1.stream().map(Card::getId).collect(Collectors.toList());

        cardsService.batchBind(bindIdsOwner, userId, true, true);

        List<Long> res = new ArrayList<>(bindIdsOwner);

        loymaxCardService.batchBindAsync(res, isOwnerList.stream()
                .map(UserCardDto::getId)
                .collect(Collectors.toList()));
        return res1;
    }

    @Override
    public Status selectAndBindUserStatus(LoymaxUser loymaxUser) {
        var loymaxUserStatus = loymaxService.selectStatus(loymaxUser);
        var loymaxClientStatus = loymaxUserStatus.getCurrentStatus();
        var status = new Status();
        var loymaxListStatuses = loymaxUserStatus.getStatuses();
        var currentValue = loymaxUserStatus.getCurrentValue();
        String statusNextLeft = null;
        boolean setNowStatus = false; //Trigger to set a status that has not yet been reached
        for (LoymaxUserStatusItem loymaxStatus : loymaxListStatuses) {
            Integer threshold = loymaxStatus.getThreshold(); //Status limit
            if (setNowStatus) { //Check trigger
                statusNextLeft = loymaxStatus.getName();
                break;
            }
            //If there is a limit and the buyer bought less than the status limit, then he strives for this status
            if (threshold != null && threshold > currentValue) {
                status.setLeftUntilNextStatus(threshold - currentValue + 1);
                status.setNextStatus(loymaxStatus.getName());
                setNowStatus = true;
                continue;
            } else { //The user has crossed the limit of this status and he receives this status next month
                status.setNextStatus(loymaxStatus.getName());
            }
            if (threshold == null) {
                //If we have reached the status without a limit and the statusNextLeft
                // trigger did not work, then it will receive the maximum status in the next month
                status.setNextStatus(loymaxStatus.getName());
                statusNextLeft = loymaxStatus.getName();
                break;
            }
        }
        var clientStatus = new ClientStatus();
        clientStatus.setName(loymaxClientStatus.getName());
        clientStatus.setThreshold(loymaxClientStatus.getThreshold());
        status.setClientStatus(clientStatus);
        status.setCurrentValue(loymaxUserStatus.getCurrentValue());

        if (statusNextLeft != null)
            status.setStatusNextLeft(statusNextLeft);
        else
            status.setStatusNextLeft(loymaxClientStatus.getName());

        if (status.getNextStatus() == null)
            status.setNextStatus(clientStatus.getName());

        userStatusService.saveOrUpdate(status, loymaxUser.getUserId());
        return status;
    }
}
