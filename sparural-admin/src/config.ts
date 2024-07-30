import { SelectOption } from './components/simples/Selector/OptionItem'

export enum Routes {
    AUTH = '/',
    REPAIR_PASS = '/repair-pass',
    CREATE_PASS = '/create-pass',

    INFO_SCREEN = '/info-screen',
    INFO_SCREEN_REDACT = '/info-screen-edit',
    INFO_SCREEN_ADD = '/info-screen-create',

    BANNER_SCREEN = '/banner-screen',
    BANNER_SCREEN_REDACT = '/banner-screen-edit',
    BANNER_SCREEN_ADD = '/banner-screen-create',

    CATALOG = '/catalog',
    CATALOG_EDIT = '/catalog/edit',
    CATALOG_CREATE = '/catalog/create',

    SHOPS = '/shops',
    SHOPS_EDIT = '/shops/edit',
    SHOPS_CREATE = '/shops/create',

    PERSONAL_OFFERS = '/personal-offers',
    PERSONAL_OFFERS_EDIT = '/personal-offers/edit',
    PERSONAL_OFFERS_CREATE = '/personal-offers/create',

    PERSONAL_DISCOUNTS = '/personal-discounts',
    PERSONAL_DISCOUNTS_EDIT = '/personal-discounts/edit',
    PERSONAL_DISCOUNTS_CREATE = '/personal-discounts/create',

    FAVORITE_CATEGORIES = '/favorite-categories',
    FAVORITE_CATEGORIES_EDIT = '/favorite-categories/edit',
    FAVORITE_CATEGORIES_CREATE = '/favorite-categories/create',

    STATUS_BUYERS = '/status-buyers',
    STATUS_BUYERS_EDIT = '/status-buyers/edit',
    STATUS_BUYERS_CREATE = '/status-buyers/create',

    PERSONAL_PRODUCTS = '/personal-products',
    PERSONAL_PRODUCTS_EDIT = '/personal-products/edit',
    PERSONAL_PRODUCTS_CREATE = '/personal-products/create',

    PERSONAL_COUPUNS = '/personal-coupons',
    PERSONAL_COUPUNS_EDIT = '/personal-coupons/edit',
    PERSONAL_COUPUNS_CREATE = '/personal-coupons/create',

    QUESTION_ANSWER = '/question-answer',
    QUESTION_ANSWER_EDIT = '/question-answer/edit',
    QUESTION_ANSWER_CREATE = '/question-answer/create',

    SETTINGS = '/settings',

    OUTSIDE_DOCS_SCREEN = '/outside-docs',
    OUTSIDE_DOCS_EDIT_SCREEN = '/outside-docs/edit',
    OUTSIDE_DOCS_CREATE_SCREEN = '/outside-docs/create',

    STATIC_SCREEN = '/static-screen',
    STATIC_SCREEN_EDIT = '/static-screen/edit',
    STATIC_SCREEN_CREATE = '/static-screen/create',

    USERS_SCREEN = '/users_screen',
    USERS_SCREEN_EDIT = '/users-screen/edit',
    USERS_SCREEN_CREATE = '/users-screen/create',

    USERS_ATRIBUTES = '/users-attributes',
    USERS_ATRIBUTES_EDIT = '/users-attributes/edit',
    USERS_ATRIBUTES_CREATE = '/users-attributes/create',

    COUNTERS = '/counters',
    COUNTERS_EDIT = '/counters/edit',
    COUNTERS_CREATE = '/counters/create',

    FEEDBACK = '/feedback',
    FEEDBACK_EDIT = '/feedback/edit',
    FEEDBACK_THEME = '/feedback_theme',
    FEEDBACK_THEME_CREATE = '/feedback_theme/create',
    FEEDBACK_THEME_EDIT = '/feedback_theme/edit',

    REGISTRED_USERS_SCREEN = '/registred-users-screen',
    REGISTRED_USERS_SCREEN_EDIT = '/registred-users-screen/edit',
    REGISTRED_USERS_SCREEN_CREATE = '/registred-users-screen/create',

    GROUP_USERS_SCREEN = '/group-users',
    GROUP_USERS_SCREEN_CREATE = '/group-users/create',
    GROUP_USERS_SCREEN_EDIT = '/group-users/edit',

    BLOCKS = '/blocks',
    ATRIBUTES = '/atributes',

    NOTIFICATIONS = '/notifications',
    NOTIFICATIONS_CREATE = '/notifications/create',
    NOTIFICATIONS_EDIT = '/notifications/edit',

    DELIVERY_OPTIONS = '/delivery-options',
    DELIVERY_OPTIONS_CREATE = '/delivery-options/create',
    DELIVERY_OPTIONS_EDIT = '/delivery-options/edit',

    QUESTION_RATING_STORE = '/question-rating-store',
    QUESTION_RATING_STORE_EDIT = '/question-rating-store/edit',
    QUESTION_RATING_STORE_CREATE = '/question-rating-store/create',
    REVIEWS = '/reviews',
    REVIEWS_INFO = '/reviews-info',

    RECIPES_CREATE = '/recipes/create',
    RECIPES_EDIT = '/recipes/edit',
    RECIPES = '/recipes',

    RECIPE_ATTRIBUTES = '/recipe-attributes',
}

export enum Versions {
    USERS = 1,
    INFO_SCREEN = 1,
    BANNER_PLACE = 1,
    CODE_SETTINGS = 1,
    LOYMAX_SETTINGS = 1,
    AUTH_SETTINGS = 1,
    PROFILE_OFFERS = 1,
    PROFILE_COUPONS = 1,
    FAVORITE_CATEGORIES = 1,
    OUTSIDE_DOCS = 1,
    STATIC_SCREEN = 1,
    BLOCKS = 1,

    RECIPES = 1,
    COUNTERS = 1,

    STATUS_BUYERS = 1,

    FEEDBACKTHEME = 1,

    QUESTION_ANSWER = 1,

    SHOPS = 1,

    PERSONAL_PRODUCTS = 1,

    DELIVERY_OPTIONS = 1,

    QUESTION_RATING_STORE = 1,
    ATTRIBUTES = 1,
}

// time interval
// export enum IntervalTimeType {
//   MINUTES = "minutes",
//   HOURS = "hours",
//   DAYS = "days",
//   WEEKS = "weeks",
//   MONTHS = "months"
// }
export enum IntervalTimeType {
    MINUTES = 'm',
    HOURS = 'h',
    DAYS = 'd',
    WEEKS = 'w',
    MONTHS = 'M',
}
/**
 * FILE service
 */

export enum EntitiesFieldName {
    INFO_SCREEN_PHOTO = 'INFO_SCREEN_PHOTO',
    ONBOX_BANNER_PHOTO = 'ONBOX_BANNER_PHOTO',
    USER_PHOTO = 'USER_PHOTO',
    FAVORITE_CATEGORY_PHOTO = 'FAVORITE_CATEGORY_PHOTO',
    COUPON_EMISSION_PHOTO = 'COUPON_EMISSION_PHOTO',
    PERSONAL_OFFER_PREVIEW = 'PERSONAL_OFFER_PREVIEW',
    PERSONAL_OFFER_PHOTO = 'PERSONAL_OFFER_PHOTO',
    GOODS_PHOTO = 'GOODS_PHOTO',
    GOODS_PREVIEW = 'GOODS_PREVIEW',
    CATALOG = 'CATALOG',
    RECIPE_PHOTO = 'RECIPE_PHOTO',
    RECIPE_PREVIEW = 'RECIPE_PREVIEW',
    RECIPE_ATTRIBUTE_ICON = 'RECIPE_ATTRIBUTE_ICON',
    RECIPE_FORMAT_ICON = 'RECIPE_FORMAT_ICON',
    MERCHANT_ATTRIBUTE_ICON = 'MERCHANT_ATTRIBUTE_ICON',
    MERCHANT_FORMAT_ICON = 'MERCHANT_FORMAT_ICON',
    USER_REQUEST_ATTACHMENTS = 'USER_REQUEST_ATTACHMENTS',
    DELIVERY_PHOTO = 'DELIVERY_PHOTO',
    MESSAGE_TEMPLATE_PHOTO = 'MESSAGE_TEMPLATE_PHOTO',
    CLIENT_STATUS_ICON = 'CLIENT_STATUS_ICON',
    SUPPORT_CHATS = 'SUPPORT_CHATS',
    SUPPORT_CHATS_MESSAGE_FILE = 'SUPPORT_CHATS_MESSAGE_FILE',
}

export enum FileSource {
    REQUEST = 'request',
    REMOTE = 'remote',
}

export enum SourceParametr {
    REQUEST = 'request',
    REMOTE = 'remote',
}

export type SourceParametrType = {
    [key in SourceParametr]: { [key: string]: string }
}

export type UploadFileEntitiesType = {
    field: EntitiesFieldName
    documentId: number
}
