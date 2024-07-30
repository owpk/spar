import React, { FC } from 'react'
import MenuItem, { MenuItemType } from './MenuItem'
import styles from './Sidebar.module.scss'

import { ReactComponent as InfoScreenIcon } from '../../../assets/icons/infoScreen.svg'
import { ReactComponent as BannerIcon } from '../../../assets/icons/banner.svg'
import { ReactComponent as Catalog } from '../../../assets/icons/catalog.svg'
import { ReactComponent as Settings } from '../../../assets/icons/settings.svg'
import { ReactComponent as Shops } from '../../../assets/icons/shop.svg'
import { ReactComponent as PersonalOffers } from '../../../assets/icons/personal.svg'
import { ReactComponent as FavoriteCategories } from '../../../assets/icons/favoriteCategory.svg'
import { ReactComponent as PersonalCoupons } from '../../../assets/icons/personalCoupon.svg'
import { ReactComponent as PersonalProducts } from '../../../assets/icons/personalThings.svg'
import { ReactComponent as QuestionAnswer } from '../../../assets/icons/questionAnswer.svg'
import { ReactComponent as Blocks } from '../../../assets/icons/blocks.svg'
import { ReactComponent as Groups } from '../../../assets/icons/registerGroup.svg'
import { ReactComponent as RegUsers } from '../../../assets/icons/userRegisterPage.svg'
import { ReactComponent as Users } from '../../../assets/icons/userPage.svg'
import { ReactComponent as Static } from '../../../assets/icons/static.svg'
import { ReactComponent as Atributes } from '../../../assets/icons/shopIcons.svg'
import { ReactComponent as RecipesIcon } from '../../../assets/icons/RecipesIcon.svg'
import { ReactComponent as FeedBack } from '../../../assets/icons/feedbacks.svg'
import { ReactComponent as Notifications } from '../../../assets/icons/notifications.svg'
import { ReactComponent as StatusBuyers } from '../../../assets/icons/statusBuyer.svg'
import { ReactComponent as DeliveryOptions } from '../../../assets/icons/delivery_options.svg'
import { ReactComponent as QuestionRatingStoreIcon } from '../../../assets/icons/QuestionRatingStoreIcon.svg'
import { ReactComponent as ReviewsIcon } from '../../../assets/icons/reviews.svg'
import { ReactComponent as FeedBakcTheme } from '../../../assets/icons/feedbackTheme.svg'

import { Routes } from '../../../config'
import { useTranslation } from 'react-i18next'
import { PersonalDiscounts } from '../../pages/personalDiscounts/PersonalDiscounts'

const Sidebar: FC = () => {
    const { t } = useTranslation()

    /**
     * Main menu items
     */

    const mainMenu: MenuItemType[] = [
        {
            route: Routes.INFO_SCREEN,
            label: t('screen_title.info_screen'),
            icon: <InfoScreenIcon />,
            activRoutes: [Routes.INFO_SCREEN_ADD, Routes.INFO_SCREEN_REDACT],
        },
        {
            route: Routes.BANNER_SCREEN,
            label: t('screen_title.banner_place'),
            icon: <BannerIcon />,
            activRoutes: [
                Routes.BANNER_SCREEN_ADD,
                Routes.BANNER_SCREEN_REDACT,
            ],
        },
        {
            route: Routes.CATALOG,
            label: t('screen_title.catalogs'),
            icon: <Catalog />,
            activRoutes: [Routes.CATALOG_CREATE, Routes.CATALOG_EDIT],
        },
        {
            route: Routes.SHOPS,
            label: t('screen_title.shops'),
            icon: <Shops />,
            activRoutes: [Routes.SHOPS_CREATE, Routes.SHOPS_EDIT],
        },
        {
            route: Routes.ATRIBUTES,
            label: t('screen_title.shop_atributes'),
            icon: <Atributes />,
        },
        {
            route: Routes.NOTIFICATIONS,
            label: t('screen_title.notification_screen'),
            icon: <Notifications />,
            activRoutes: [
                Routes.NOTIFICATIONS_CREATE,
                Routes.NOTIFICATIONS_EDIT,
            ],
        },
        {
            route: Routes.FAVORITE_CATEGORIES,
            label: t('screen_title.favorite_categories'),
            icon: <FavoriteCategories />,
            activRoutes: [
                Routes.FAVORITE_CATEGORIES_CREATE,
                Routes.FAVORITE_CATEGORIES_EDIT,
            ],
        },
        {
            route: Routes.PERSONAL_OFFERS,
            label: t('screen_title.personal_offers'),
            icon: <PersonalOffers />,
            activRoutes: [
                Routes.PERSONAL_OFFERS_CREATE,
                Routes.PERSONAL_OFFERS_EDIT,
            ],
        },

        {
            route: Routes.PERSONAL_DISCOUNTS,
            label: t('screen_title.personal_discounts'),
            icon: <PersonalOffers />,
            activRoutes: [
                Routes.PERSONAL_DISCOUNTS_EDIT,
                Routes.PERSONAL_DISCOUNTS_CREATE,
            ],
        },

        {
            route: Routes.PERSONAL_COUPUNS,
            label: t('screen_title.personal_coupons'),
            icon: <PersonalCoupons />,
            activRoutes: [
                Routes.PERSONAL_COUPUNS_CREATE,
                Routes.PERSONAL_COUPUNS_EDIT,
            ],
        },
        {
            route: Routes.PERSONAL_PRODUCTS,
            label: t('screen_title.personal_products'),
            icon: <PersonalProducts />,
            activRoutes: [
                Routes.PERSONAL_PRODUCTS_CREATE,
                Routes.PERSONAL_PRODUCTS_EDIT,
            ],
        },
        {
            route: Routes.RECIPES,
            label: t('screen_title.recipes'),
            icon: <RecipesIcon />,
            activRoutes: [
                Routes.RECIPES,
                Routes.RECIPES_CREATE,
                Routes.RECIPES_EDIT,
            ],
        },
        {
            route: Routes.RECIPE_ATTRIBUTES,
            label: t('screen_title.recipe-attributes'),
            icon: <Atributes />,
        },
        {
            route: Routes.BLOCKS,
            label: t('screen_title.blocks'),
            icon: <Blocks />,
        },
        {
            route: Routes.COUNTERS,
            label: t('screen_title.counters'),
            icon: <QuestionAnswer />,
        },
        {
            route: Routes.USERS_ATRIBUTES,
            label: t('screen_title.users_attributes'),
            icon: <Users />,
        },
        {
            route: Routes.USERS_SCREEN,
            label: t('screen_title.users_page'),
            icon: <Users />,
            activRoutes: [Routes.USERS_SCREEN_CREATE, Routes.USERS_SCREEN_EDIT],
        },
        {
            route: Routes.REGISTRED_USERS_SCREEN,
            label: t('screen_title.registered_users_page'),
            icon: <RegUsers />,
            activRoutes: [
                Routes.REGISTRED_USERS_SCREEN_CREATE,
                Routes.REGISTRED_USERS_SCREEN_EDIT,
            ],
        },
        {
            route: Routes.GROUP_USERS_SCREEN,
            label: t('screen_title.user_group'),
            icon: <Groups />,
            activRoutes: [
                Routes.GROUP_USERS_SCREEN_CREATE,
                Routes.GROUP_USERS_SCREEN_EDIT,
            ],
        },
        {
            route: Routes.SETTINGS,
            label: 'Страница настройки',
            icon: <Settings />,
            activRoutes: [Routes.SETTINGS],
        },
        {
            route: Routes.OUTSIDE_DOCS_SCREEN,
            label: t('screen_title.outside_docs'),
            icon: <Catalog />,
            activRoutes: [
                Routes.OUTSIDE_DOCS_EDIT_SCREEN,
                Routes.OUTSIDE_DOCS_CREATE_SCREEN,
            ],
        },
        {
            route: Routes.STATIC_SCREEN,
            label: t('screen_title.static_screen'),
            icon: <Static />,
            activRoutes: [
                Routes.STATIC_SCREEN_CREATE,
                Routes.STATIC_SCREEN_EDIT,
            ],
        },
        {
            route: Routes.QUESTION_ANSWER,
            label: t('screen_title.question_answer'),
            icon: <QuestionAnswer />,
            activRoutes: [
                Routes.QUESTION_ANSWER_CREATE,
                Routes.QUESTION_ANSWER_EDIT,
            ],
        },
        {
            route: Routes.STATUS_BUYERS,
            label: t('screen_title.status_buyers'),
            icon: <StatusBuyers />,
            activRoutes: [
                Routes.STATUS_BUYERS_CREATE,
                Routes.STATUS_BUYERS_EDIT,
            ],
        },
        {
            route: Routes.QUESTION_RATING_STORE,
            label: t('screen_title.question_rating_store'),
            icon: <QuestionRatingStoreIcon />,
            activRoutes: [
                Routes.QUESTION_RATING_STORE_EDIT,
                Routes.QUESTION_RATING_STORE_CREATE,
            ],
        },
        {
            route: Routes.FEEDBACK,
            label: t('screen_title.feedback'),
            icon: <FeedBack />,
            activRoutes: [Routes.FEEDBACK_EDIT],
        },

        {
            route: Routes.FEEDBACK_THEME,
            label: t('screen_title.feedbacktheme'),
            icon: <FeedBakcTheme />,
            activRoutes: [
                Routes.FEEDBACK_THEME_EDIT,
                Routes.FEEDBACK_THEME_CREATE,
            ],
        },

        {
            route: Routes.REVIEWS,
            label: t('screen_title.reviews'),
            icon: <ReviewsIcon />,
            activRoutes: [Routes.REVIEWS_INFO],
        },

        {
            route: Routes.DELIVERY_OPTIONS,
            label: t('screen_title.deliveryOptions'),
            icon: <DeliveryOptions />,
            activRoutes: [
                Routes.DELIVERY_OPTIONS_EDIT,
                Routes.DELIVERY_OPTIONS_CREATE,
            ],
        },
    ]

    return (
        <div className={styles.sidebarInner}>
            <div className="">
                {mainMenu.map((item) => (
                    <MenuItem data={item} key={item.route} />
                ))}
            </div>
        </div>
    )
}

export default React.memo(Sidebar)
