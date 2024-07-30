package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.CatalogDto;
import ru.sparural.engine.api.dto.CoordinatesDto;
import ru.sparural.engine.api.dto.CouponDto;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.PersonalOfferDto;
import ru.sparural.engine.api.dto.cards.UserCardDto;
import ru.sparural.engine.api.dto.goods.PersonalGoodsDto;
import ru.sparural.engine.api.dto.main.CategoryDto;
import ru.sparural.engine.api.dto.main.MainScreenDto;
import ru.sparural.engine.api.dto.main.OfferDto;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.controllers.CatalogController;
import ru.sparural.engine.controllers.ClientPersonalOffersController;
import ru.sparural.engine.controllers.CouponsController;
import ru.sparural.engine.controllers.PersonalGoodsController;
import ru.sparural.engine.entity.Card;
import ru.sparural.engine.entity.City;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.providers.OffersProvider;
import ru.sparural.engine.services.CardsService;
import ru.sparural.engine.services.CitiesService;
import ru.sparural.engine.services.FavoriteCategoriesService;
import ru.sparural.engine.services.FileDocumentService;
import ru.sparural.engine.services.GeolocationService;
import ru.sparural.engine.services.LoymaxCardService;
import ru.sparural.engine.services.MobileMainService;
import ru.sparural.engine.services.UserService;
import ru.sparural.kafka.annotation.RequestParam;

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
@RequiredArgsConstructor
@Service
public class MobileMainServiceImpl implements MobileMainService {

    private final LoymaxService loymaxService;
    private final GeolocationService geolocationService;
    private final CardsService cardsService;
    private final LoymaxCardService loymaxCardService;
    private final PersonalGoodsController personalGoodsController;
    private final CouponsController couponsController;
    private final FavoriteCategoriesService favoriteCategoriesService;
    private final OffersProvider offersProvider;
    private final ClientPersonalOffersController clientPersonalOffersController;
    private final FileDocumentService fileDocumentService;
    private final CatalogController catalogController;
    private final UserService userService;
    private final CitiesService<City> citiesService;

    private ExecutorService executorService;

    @PostConstruct
    private void init() {
        executorService = Executors.newCachedThreadPool();
    }

    public MainScreenDto get(@RequestParam String userLongitude,
                             @RequestParam String userLatitude,
                             @RequestParam Long userId) throws ExecutionException, InterruptedException {
        if (userId == 0) {
            List<CatalogDto> catalogs;
            List<OfferDto> offers = offersProvider.getOfferList(0, 3, userId);

            if (userLatitude != null && userLongitude != null) {
                var coordinatesDto = new CoordinatesDto();
                coordinatesDto.setLatitude(Double.parseDouble(userLatitude));
                coordinatesDto.setLongitude(Double.parseDouble(userLongitude));
                City city = geolocationService.findCityByCoordinates(coordinatesDto);
                catalogs = catalogController.list(city.getId(), 0, 30);
            } else {
                var defaultCityId = citiesService.getByName("Челябинск").getId();
                catalogs = catalogController.list(defaultCityId, 0, 30);
            }

            return MainScreenDto
                    .builder()
                    .offers(offers)
                    .catalogs(catalogs)
                    .build();
        }

        var loymaxUser = loymaxService.getByLocalUserId(userId);
        var dtoResponse = new MainScreenDto();

        var selectCardsTask = executorService.submit(() -> {
            List<UserCardDto> cards = loymaxService.selectCards(loymaxUser);

            List<UserCardDto> isOwnerList = new ArrayList<>();
            List<UserCardDto> notOwnerList = new ArrayList<>();

            for (UserCardDto cardDto : cards) {
                if (cardDto.getImOwner()) {
                    isOwnerList.add(cardDto);
                } else {
                    notOwnerList.add(cardDto);
                }
            }

            var res1 = cardsService.batchSaveOrUpdate(
                    isOwnerList.stream().map(x -> cardsService.createEntityFromDto(x, userId))
                            .collect(Collectors.toList()));
            var bindIdsOwner = res1.stream().map(Card::getId).collect(Collectors.toList());
            cardsService.batchBind(bindIdsOwner, userId, true, true);

            List<Long> res = new ArrayList<>(bindIdsOwner);
            loymaxCardService.batchBindAsync(res, isOwnerList.stream().map(UserCardDto::getId).collect(Collectors.toList()));

            var cardDtos = res1.stream()
                    .map(x -> cardsService.createDtoFromEntity(x, userId))
                    .collect(Collectors.toList());
            dtoResponse.setMyCards(cardDtos);
        });

        var categoriesTask = executorService.submit(() -> {
            List<CategoryDto> categories = favoriteCategoriesService.list(userId, 0, 30).getData();
            if (categories == null) {
                categories = new ArrayList<>();
            } else {
                categories.forEach(dto -> {
                    List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.FAVORITE_CATEGORY_PHOTO, dto.getId());
                    if (!files.isEmpty()) {
                        dto.setPhoto(files.get(files.size() - 1));
                    }
                });
            }
            dtoResponse.setFavoriteCategories(categories);
        });

        var catalogsTask = executorService.submit(() -> {
            List<CatalogDto> catalogs;
            if (userLatitude != null && userLongitude != null) {
                var coordinatesDto = new CoordinatesDto();
                coordinatesDto.setLatitude(Double.parseDouble(userLatitude));
                coordinatesDto.setLongitude(Double.parseDouble(userLongitude));
                City city = geolocationService.findCityByCoordinates(coordinatesDto);

                if (city != null) {
                    userService.updateLastCityId(userId, city.getId());
                }

                catalogs = catalogController.list(city.getId(), 0, 30);
            } else {
                Long cityId;
                try {
                    cityId = userService.getCityIdByUserId(userId);
                } catch (Exception e) {
                    cityId = citiesService.getByName("Челябинск").getId();
                }
                catalogs = catalogController.list(cityId, 0, 30);
            }
            dtoResponse.setCatalogs(catalogs);
        });

        var goodsTask = executorService.submit(() -> {
            var personalGoods = personalGoodsController.list(0, 3, userId);
            dtoResponse.setPersonalGoods(personalGoods);
        });
        var couponsTask = executorService.submit(() -> {
            var coupons = couponsController.list(0, 3, userId);
            dtoResponse.setCoupones(coupons);
        });
        var offersTask = executorService.submit(() -> {
            var offers = offersProvider.getOfferList(0, 3, userId);
            dtoResponse.setOffers(offers);
        });
        var persOffersTaks = executorService.submit(() -> {
            var personalOffers = clientPersonalOffersController.get(0, 3, userId);
            dtoResponse.setPersonalOffers(personalOffers);
        });

        selectCardsTask.get();
        catalogsTask.get();
        categoriesTask.get();
        goodsTask.get();
        couponsTask.get();
        offersTask.get();
        persOffersTaks.get();

        return dtoResponse;
    }

    @Override
    public List<UserCardDto> selectCards(Long userId) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);

        List<UserCardDto> cards = loymaxService.selectCards(loymaxUser);

        List<UserCardDto> isOwnerList = new ArrayList<>();
        List<UserCardDto> notOwnerList = new ArrayList<>();

        for (UserCardDto cardDto : cards) {
            if (cardDto.getImOwner()) {
                isOwnerList.add(cardDto);
            } else {
                notOwnerList.add(cardDto);
            }
        }

        var res1 = cardsService.batchSaveOrUpdate(
                isOwnerList.stream().map(x -> cardsService.createEntityFromDto(x, userId))
                        .collect(Collectors.toList()));
        var bindIdsOwner = res1.stream().map(Card::getId).collect(Collectors.toList());
        cardsService.batchBind(bindIdsOwner, userId, true, true);

        List<Long> res = new ArrayList<>(bindIdsOwner);
        loymaxCardService.batchBindAsync(res, isOwnerList.stream().map(UserCardDto::getId).collect(Collectors.toList()));

        return res1.stream()
                .map(x -> cardsService.createDtoFromEntity(x, userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDto> selectCategory(Long userId, Integer offset, Integer limit) {
        List<CategoryDto> categories = favoriteCategoriesService.list(userId, offset, limit).getData();
        if (categories == null) {
            categories = new ArrayList<>();
        } else {
            categories.forEach(dto -> {
                List<FileDto> files = fileDocumentService
                        .getFileByDocumentId(FileDocumentTypeField.FAVORITE_CATEGORY_PHOTO, dto.getId());
                if (!files.isEmpty()) {
                    dto.setPhoto(files.get(files.size() - 1));
                }
            });
        }
        return categories;
    }

    @Override
    public List<PersonalGoodsDto> selectPersonalGoods(Long userId, Integer offset, Integer limit) {
        return personalGoodsController.list(offset, limit, userId);
    }

    @Override
    public List<CouponDto> selectCoupons(Long userId, Integer offset, Integer limit) {
        return couponsController.list(offset, limit, userId);
    }

    @Override
    public List<OfferDto> selectOffers(Integer offset, Integer limit, Long userId) {
        return offersProvider.getOfferList(offset, limit, userId);
    }

    @Override
    public List<PersonalOfferDto> selectPersOffers(Long userId, Integer offset, Integer limit) {
        return clientPersonalOffersController.get(offset, limit, userId);
    }
}