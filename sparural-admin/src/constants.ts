export enum Endpoints {
    LOGIN = 'login',
    LOGOUT = 'logout/',

    REFRESH_TOKEN = 'auth/refresh-tokens',
    RECOVERY_PASSWORD = 'recovery-password',
    SHOPS = 'merchants',
    SHOPS_FORMATS = 'merchant-formats',

    USER_DATA = 'user',
    USER_CHECK = 'user/check',

    ACCOUNTS_TYPES = 'accounts/types',

    FETCH_CITIES = 'cities',

    INFO_SCREEN = 'info-screens',
    BANNER_PLACE = 'onbox-banners',
    CATALOGS_PLACE = 'catalogs',
    PERSONAL_OFFERS = 'personal-offers',
    PERSONAL_DISCOUNTS = 'offers-counters',
    PERSONAL_COUPONS = 'coupon-emmissions',
    FAVORITE_CATEGORIES = 'favorite-categories',
    PERSONAL_PRODUCTS = 'personal-goods',
    GOODS = 'goods',
    RECIPES = 'recipes',

    SETTINGS_CODES = 'confirm-code-settings',
    SETTINGS_AUTH = 'auth-settings',
    SETTING_PAYMENT = 'payment-settings',
    SETTING_LOYMAX = 'loymax-settings',
    SETTING_NOTIFICATION = 'notifications-settings',
    SETTING_SOCIAL = 'socials',
    SETTING_GENERAL = 'settings',

    STATIC_PAGES = 'contents',
    EXTERNAL_DOCS = 'external-documents',

    USERS = 'users',
    USERS_ATRIBUTES = 'users_attributes',
    ROLES_USER = 'user/roles',
    USERS_GROUP = 'users-groups',
    BLOCKS = 'main-blocks',
    ATTRIBUTES = 'merchant-attributes',
    NOTIFICATIONS = 'messages-templates',
    TRIGGERS_TYPE_GET = 'triggers-types',
    PUSH_ACTIONS = 'push-actions',
    NOTIFICATIONS_TYPES = 'notifications-types',
    COUNTERS = 'counters',

    RECIPE_ATTRIBUTES = 'recipe-attributes',

    STATUS_BUYERS = 'client-statues',

    FEED_BACK_THEME = 'user-requests-subjects',
    FEEDBACK = 'user-requests',

    FEEDBACK_CHATS = 'support-chats',

    QUESTION_ANSWER = 'faq',

    DELIVERY_OPTIONS = 'delivery',

    QUESTION_RATING_STORE = 'merchant-comments-questions',
    ANSWER_RATING_STORE = 'merchant-comments-answers',
    REVIEWS = 'merchant-comments',
    REVIEWS_REPORT_EXPORT = 'merchant-comments/export',

    SELECT_SCREENS = 'screens',

    CURRENCIES = 'currencies',
}

export const onPageCount = 20

export const time = [
    { value: 0, label: '00' },
    { value: 1, label: '01' },
    { value: 2, label: '02' },
    { value: 3, label: '03' },
    { value: 4, label: '04' },
    { value: 5, label: '05' },
    { value: 6, label: '06' },
    { value: 7, label: '07' },
    { value: 8, label: '08' },
    { value: 9, label: '09' },
    { value: 10, label: '10' },
    { value: 11, label: '11' },
    { value: 12, label: '12' },
    { value: 13, label: '13' },
    { value: 14, label: '14' },
    { value: 15, label: '15' },
    { value: 16, label: '16' },
    { value: 17, label: '17' },
    { value: 18, label: '18' },
    { value: 19, label: '19' },
    { value: 20, label: '20' },
    { value: 21, label: '21' },
    { value: 22, label: '22' },
    { value: 23, label: '23' },
]
export const regMail =
    /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
export const regOnlyNumbers = /^(\s*|\d+)$/
export const regLatinica = /^[а-яё]+$/
