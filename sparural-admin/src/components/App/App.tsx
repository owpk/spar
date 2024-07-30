import React, { useEffect } from 'react'
import {
    BrowserRouter as Router,
    Navigate,
    Route,
    Routes as NavRoutes,
} from 'react-router-dom'
import styles from './App.module.scss'

// import { Error404 } from '../pages/Error404'
import { useAppDispatch, useAppSelector } from '../../hooks/store'
import { Auth } from '../pages/Auth'
import { Routes } from '../../config'
import { RepairPass } from '../pages/RepairPass'
import { CreateNewPass } from '../pages/CreateNewPass'
import { Catalog } from '../pages/catalogsSreens/Catalog'
import { InfoScreenRedact } from '../pages/infoScreens/InfoScreenRedact'
import { InfoScreen } from '../pages/infoScreens/InfoScreen'
import { PagesSettings } from '../pages/PagesSettings'
import { CatalogEdit } from '../pages/catalogsSreens/CatalogEdit'
import { OutsideDocsScreen } from '../pages/outsideDocs/OutSideDocsScreen'
import { OutsideDocsEditScreen } from '../pages/outsideDocs/OutsideDocsEditScreen'
import { StaticScreen } from '../pages/staticScreens/StaticScreen'
import { StaticScreenEdit } from '../pages/staticScreens/StaticScreenEdit'
import { fetchMerchantFormats, loadCities } from '../../store/slices/storage'
import { ShopsScreen } from '../pages/shopsScreen/ShopsSceen'
import { ShopsScreenRedact } from '../pages/shopsScreen/ShopsScreenRedact'
import {
    selectAuthChecking,
    selectIsAuth,
    tempCheck,
} from '../../store/slices/authSlice'
import BannerScreenRedact from '../pages/bannerScreens/BannerScreenRedact/BannerScreenRedact'
import { FavoriteCategories } from '../pages/favoriteCategories/FavoririteCategories'
import { FavoriteCategoriesRedact } from '../pages/favoriteCategories/FavoriteCategoriesRedact'
import { UsersPage } from '../pages/usersPages/UsersPage'
import { PersonalOffers } from '../pages/personalOffers/PersonalOffers'
import { PersonalOffersRedact } from '../pages/personalOffers/PersonalOffersRedact'
import { EditUserPage } from '../pages/usersPages/EditUserPage'
import { RegisteredUsersPage } from '../pages/registeredUsersPages/RegisteredUsersPage'
import { PersonalCoupons } from '../pages/personalCoupons/PersonalCoupons'
import { PersonalCouponsRedact } from '../pages/personalCoupons/PersonalCouponsRedact'
import { RegisteredUserEditPage } from '../pages/registeredUsersPages/RegisteredUserEditPage'
import { GroupUsers } from '../pages/groupUsersScreens/GroupUsers'
import { PersonalProducts } from '../pages/personalProducts/PersonalProducts'
import { PersonalProductsRedact } from '../pages/personalProducts/PersonalProductsRedact'
import { EditGroupUsers } from '../pages/groupUsersScreens/EditGroupUsers'
import { QuestionAnswer } from '../pages/questionAnswer/QuestionAnswer'
import { QuestionAnswerRedact } from '../pages/questionAnswer/QuestionAnswerRedact'
import { BlocksScreen } from '../pages/blocksScreens/BlocksScreen'
import { ShopAttributes } from '../pages/attributesScreens/ShopAttributes'
import { FeedBack } from '../pages/feedBack/FeedBack'
import { FeedBackReadct } from '../pages/feedBack/FeedBackChat'
import { NotificationScreen } from '../pages/notificationsSettingScreens/NotificationScreen'
import { EditNotificationScreen } from '../pages/notificationsSettingScreens/EditNotificationScreen'
import { Loader } from '../simples/Loader'
import { BannerScreen } from '../pages/bannerScreens/BannerScreen'
import { loadRoles } from '../../store/slices/storageRoles'

import { StatusBuyers } from '../pages/StatusBuyers/StatusBuyers'
import { StatusBuyersEdit } from '../pages/StatusBuyers/StatusBuyersEdit'

import { FeedBackTheme } from '../pages/feedBackTheme/FeedBackTheme'
import { FeedBackThemeRedact } from '../pages/feedBackTheme/FeedBackThemeRedact'
import { DeliveryOptions } from '../pages/deliveryOptions/DeliveryOptions'
import { DeliveryOptionsRedact } from '../pages/deliveryOptions/DeliveryOptionsRedact'
import { QuestionRatingStoreEdit } from '../pages/QuestionRatingStore/QuestionRatingStoreEdit'
import { QuestionRatingStore } from '../pages/QuestionRatingStore/QuestionRatingStore'
import { Reviews } from '../pages/reviews/Reviews'
import { ReviewsInfo } from '../pages/reviews/ReviewsInfo'
import { MainWrapper } from '../complexes/MainWrapper'
import { PersonalDiscounts } from '../pages/personalDiscounts/PersonalDiscounts'
import { PersonalDiscountsRedact } from '../pages/personalDiscounts/PersonalDiscountsRedact'
import RecipeAttributes from '../pages/RecipeAttributes/RecipeAttributes'
import RecipesPage from '../pages/RecipesPage/RecipesPage'
import { RecipesRedact } from '../pages/RecipesPage/RecipesRedact'
import UserAttributesPage from '../pages/UserAttributesPage/UserAttributesPage'
import UserAttributesRedact from '../pages/UserAttributesPage/UserAttributesRedact'
import CountersPage from '../pages/CountersPage/CountersPage'
import CountersRedact from '../pages/CountersPage/CountersRedact'

const App = () => {
    const dispatch = useAppDispatch()
    const isAuth = useAppSelector(selectIsAuth)
    const preloading = useAppSelector(selectAuthChecking)

    useEffect(() => {
        dispatch(tempCheck())
        dispatch(loadCities()).then()
        dispatch(loadRoles()).then()
        dispatch(fetchMerchantFormats()).then()
    }, [dispatch])

    if (preloading) {
        return <Loader />
    }

    return (
        <div className={styles.app}>
            <Router>
                {!isAuth ? (
                    <NavRoutes>
                        <Route path={Routes.AUTH} element={<Auth />} />
                        <Route
                            path={Routes.REPAIR_PASS}
                            element={<RepairPass />}
                        />
                        <Route
                            path={Routes.CREATE_PASS}
                            element={<CreateNewPass />}
                        />

                        <Route
                            path="*"
                            element={<Navigate to={Routes.AUTH} />}
                        />
                    </NavRoutes>
                ) : (
                    <MainWrapper>
                        <NavRoutes>
                            <Route
                                path={Routes.INFO_SCREEN}
                                element={<InfoScreen />}
                            />
                            <Route
                                path={Routes.INFO_SCREEN_REDACT}
                                element={<InfoScreenRedact />}
                            />
                            <Route
                                path={Routes.INFO_SCREEN_ADD}
                                element={<InfoScreenRedact />}
                            />

                            <Route
                                path={Routes.BANNER_SCREEN}
                                element={<BannerScreen />}
                            />
                            <Route
                                path={Routes.BANNER_SCREEN_REDACT}
                                element={<BannerScreenRedact />}
                            />
                            <Route
                                path={Routes.BANNER_SCREEN_ADD}
                                element={<BannerScreenRedact />}
                            />
                            <Route
                                path={Routes.CATALOG}
                                element={<Catalog />}
                            />
                            <Route
                                path={Routes.CATALOG_CREATE}
                                element={<CatalogEdit />}
                            />
                            <Route
                                path={Routes.CATALOG_EDIT}
                                element={<CatalogEdit />}
                            />
                            <Route
                                path={Routes.SHOPS}
                                element={<ShopsScreen />}
                            />
                            <Route
                                path={Routes.SHOPS_CREATE}
                                element={<ShopsScreenRedact />}
                            />
                            <Route
                                path={Routes.SHOPS_EDIT}
                                element={<ShopsScreenRedact />}
                            />
                            <Route
                                path={Routes.PERSONAL_OFFERS}
                                element={<PersonalOffers />}
                            />
                            <Route
                                path={Routes.PERSONAL_OFFERS_EDIT}
                                element={<PersonalOffersRedact />}
                            />
                            <Route
                                path={Routes.PERSONAL_OFFERS_CREATE}
                                element={<PersonalOffersRedact />}
                            />
                            <Route
                                path={Routes.PERSONAL_DISCOUNTS}
                                element={<PersonalDiscounts />}
                            />
                            <Route
                                path={Routes.PERSONAL_DISCOUNTS_EDIT}
                                element={<PersonalDiscountsRedact />}
                            />
                            <Route
                                path={Routes.PERSONAL_DISCOUNTS_CREATE}
                                element={<PersonalDiscountsRedact />}
                            />
                            <Route
                                path={Routes.FAVORITE_CATEGORIES}
                                element={<FavoriteCategories />}
                            />
                            <Route
                                path={Routes.FAVORITE_CATEGORIES_CREATE}
                                element={<FavoriteCategoriesRedact />}
                            />
                            <Route
                                path={Routes.FAVORITE_CATEGORIES_EDIT}
                                element={<FavoriteCategoriesRedact />}
                            />
                            <Route
                                path={Routes.PERSONAL_COUPUNS}
                                element={<PersonalCoupons />}
                            />
                            <Route
                                path={Routes.PERSONAL_COUPUNS_CREATE}
                                element={<PersonalCouponsRedact />}
                            />
                            <Route
                                path={Routes.PERSONAL_COUPUNS_EDIT}
                                element={<PersonalCouponsRedact />}
                            />
                            <Route
                                path={Routes.PERSONAL_PRODUCTS}
                                element={<PersonalProducts />}
                            />
                            <Route
                                path={Routes.PERSONAL_PRODUCTS_CREATE}
                                element={<PersonalProductsRedact />}
                            />
                            <Route
                                path={Routes.PERSONAL_PRODUCTS_EDIT}
                                element={<PersonalProductsRedact />}
                            />
                            <Route
                                path={Routes.RECIPES}
                                element={<RecipesPage />}
                            />
                            <Route
                                path={Routes.RECIPES_CREATE}
                                element={<RecipesRedact />}
                            />
                            <Route
                                path={Routes.RECIPES_EDIT}
                                element={<RecipesRedact />}
                            />
                            <Route
                                path={Routes.RECIPE_ATTRIBUTES}
                                element={<RecipeAttributes />}
                            />
                            <Route
                                path={Routes.QUESTION_ANSWER}
                                element={<QuestionAnswer />}
                            />
                            <Route
                                path={Routes.QUESTION_ANSWER_CREATE}
                                element={<QuestionAnswerRedact />}
                            />
                            <Route
                                path={Routes.QUESTION_ANSWER_EDIT}
                                element={<QuestionAnswerRedact />}
                            />
                            <Route
                                path={Routes.OUTSIDE_DOCS_SCREEN}
                                element={<OutsideDocsScreen />}
                            />
                            <Route
                                path={Routes.OUTSIDE_DOCS_CREATE_SCREEN}
                                element={<OutsideDocsEditScreen />}
                            />
                            <Route
                                path={Routes.OUTSIDE_DOCS_EDIT_SCREEN}
                                element={<OutsideDocsEditScreen />}
                            />
                            <Route
                                path={Routes.STATIC_SCREEN}
                                element={<StaticScreen />}
                            />
                            <Route
                                path={Routes.STATIC_SCREEN_CREATE}
                                element={<StaticScreenEdit />}
                            />
                            <Route
                                path={Routes.STATIC_SCREEN_EDIT}
                                element={<StaticScreenEdit />}
                            />
                            <Route
                                path={Routes.FEEDBACK}
                                element={<FeedBack />}
                            />

                            <Route
                                path={Routes.FEEDBACK_EDIT}
                                element={<FeedBackReadct />}
                            />
                            <Route
                                path={Routes.FEEDBACK_THEME}
                                element={<FeedBackTheme />}
                            />

                            <Route
                                path={Routes.FEEDBACK_THEME_EDIT}
                                element={<FeedBackThemeRedact />}
                            />
                            <Route
                                path={Routes.FEEDBACK_THEME_CREATE}
                                element={<FeedBackThemeRedact />}
                            />

                            <Route
                                path={Routes.SETTINGS}
                                element={<PagesSettings />}
                            />

                            <Route
                                path={Routes.USERS_SCREEN}
                                element={<UsersPage />}
                            />
                            <Route
                                path={Routes.USERS_SCREEN_CREATE}
                                element={<EditUserPage />}
                            />
                            <Route
                                path={Routes.USERS_SCREEN_EDIT}
                                element={<EditUserPage />}
                            />

                            <Route
                                path={Routes.COUNTERS}
                                element={<CountersPage />}
                            />
                            <Route
                                path={Routes.COUNTERS_EDIT}
                                element={<CountersRedact />}
                            />
                            <Route
                                path={Routes.COUNTERS_CREATE}
                                element={<CountersRedact />}
                            />

                            <Route
                                path={Routes.USERS_ATRIBUTES}
                                element={<UserAttributesPage />}
                            />
                            <Route
                                path={Routes.USERS_ATRIBUTES_EDIT}
                                element={<UserAttributesRedact />}
                            />
                            <Route
                                path={Routes.USERS_ATRIBUTES_CREATE}
                                element={<UserAttributesRedact />}
                            />

                            <Route
                                path={Routes.REGISTRED_USERS_SCREEN}
                                element={<RegisteredUsersPage />}
                            />
                            <Route
                                path={Routes.REGISTRED_USERS_SCREEN_CREATE}
                                element={<RegisteredUserEditPage />}
                            />
                            <Route
                                path={Routes.REGISTRED_USERS_SCREEN_EDIT}
                                element={<RegisteredUserEditPage />}
                            />
                            <Route
                                path={Routes.GROUP_USERS_SCREEN}
                                element={<GroupUsers />}
                            />
                            <Route
                                path={Routes.GROUP_USERS_SCREEN_CREATE}
                                element={<EditGroupUsers />}
                            />
                            <Route
                                path={Routes.GROUP_USERS_SCREEN_EDIT}
                                element={<EditGroupUsers />}
                            />
                            <Route
                                path={Routes.BLOCKS}
                                element={<BlocksScreen />}
                            />
                            <Route
                                path={Routes.ATRIBUTES}
                                element={<ShopAttributes />}
                            />
                            <Route
                                path={Routes.NOTIFICATIONS}
                                element={<NotificationScreen />}
                            />
                            <Route
                                path={Routes.NOTIFICATIONS_CREATE}
                                element={<EditNotificationScreen />}
                            />
                            <Route
                                path={Routes.NOTIFICATIONS_EDIT}
                                element={<EditNotificationScreen />}
                            />
                            <Route
                                path={Routes.STATUS_BUYERS}
                                element={<StatusBuyers />}
                            />
                            <Route
                                path={Routes.STATUS_BUYERS_CREATE}
                                element={<StatusBuyersEdit />}
                            />
                            <Route
                                path={Routes.STATUS_BUYERS_EDIT}
                                element={<StatusBuyersEdit />}
                            />

                            <Route
                                path={Routes.DELIVERY_OPTIONS}
                                element={<DeliveryOptions />}
                            />
                            <Route
                                path={Routes.DELIVERY_OPTIONS_CREATE}
                                element={<DeliveryOptionsRedact />}
                            />
                            <Route
                                path={Routes.DELIVERY_OPTIONS_EDIT}
                                element={<DeliveryOptionsRedact />}
                            />
                            <Route
                                path={Routes.QUESTION_RATING_STORE}
                                element={<QuestionRatingStore />}
                            />
                            <Route
                                path={Routes.QUESTION_RATING_STORE_EDIT}
                                element={<QuestionRatingStoreEdit />}
                            />
                            <Route
                                path={Routes.QUESTION_RATING_STORE_CREATE}
                                element={<QuestionRatingStoreEdit />}
                            />
                            <Route
                                path={Routes.REVIEWS}
                                element={<Reviews />}
                            />
                            <Route
                                path={Routes.REVIEWS_INFO}
                                element={<ReviewsInfo />}
                            />

                            <Route
                                path="*"
                                element={<Navigate to={Routes.INFO_SCREEN} />}
                            />
                        </NavRoutes>
                    </MainWrapper>
                )}
            </Router>
        </div>
    )
}

export default App
