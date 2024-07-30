export type User = {
    id: 1
    firstName: string
    lastName: string
    phoneNumber: string
    email: string
}

export enum Notifiertype {
    PHONE = 'phoneNumber',
    MAIL = 'email',
}

export type Phototype = {
    uuid?: string
    name?: string
    ext?: string
    size?: number
    mime?: string
    url: string
}
export type CityType = {
    id: number
    name: string
}
export enum CitySelectType {
    SELECTION = 'Selection',
    ALL = 'All',
    NOWHERE = 'Nowhere',
}

/**
 * INFO SCREEN
 */
export type InfoScreenType = {
    id: number
    text: string
    photo: Phototype | null
    isPublic: boolean
    citySelect: CitySelectType
    cities: Array<CityType>
    dateStart: number
    dateEnd: number
}

export type CreateInfoScreen = {
    isPublic?: boolean
    draft?: boolean
    citySelect?: CitySelectType
    cities?: Array<{ id: string | number; name: string }>
    dateStart?: number
    dateEnd?: number
}

/**
 * BANNER SCREEN
 */

export type MobileNavigateTargetType = {
    id: number
    name: string
    code: string
}
export type CreateBannerPlaceType = {
    title?: string
    description?: string
    order?: number | string
    draft?: boolean
    url?: string | null
    mobileNavigateTargetId?: number | null
    citySelect?: CitySelectType
    cities?: Array<{ id: string | number; name: string }>
    dateStart?: number
    dateEnd?: number
    isPublic?: boolean
}
export type BannerScreenType = {
    id: number
    photo: Phototype
    title: string
    description: string
    order: number
    url: string
    mobileNavigateTarget?: MobileNavigateTargetType
    citySelect: string
    cities: Array<CityType>
    dateStart: number
    dateEnd: number
    isPublic: boolean
}
export type PersonalOffersType = {
    id: number
    attribute: string
    title: string
    description: string
    begin: number
    end: number
    preview: PreviewType
    photo: Phototype
    isPublic: boolean
}

export type PersonalDiscountsType = {
    id: number
    isPublic: boolean
    loymaxCounterId: string
    loymaxOfferId: string
    maxValue: number
    offerId: number
}

export type PreviewType = {
    uuid: string
    name: string
    ext: string
    size: number
    mime: string
    url: string
}

/**
 * PersonalCouponsType
 */

export type PersonalCouponsType = {
    id: number
    title: string
    end: number
    photo: Phototype
    isPublic: boolean
    draft?: boolean
}

export type CreatePersonalCouponsType = {
    title?: string
    end?: number
    photo?: Phototype
    isPublic?: boolean
    draft?: boolean
}

/**
 * CATLOGS
 */
export type CatalogsType = {
    id: number
    name?: string
    photo?: Phototype
    url?: string
    draft?: boolean
    citySelect?: string
    cities: Array<CityType>
}
export type CreateCatalogType = {
    name?: string
    url?: string
    draft?: boolean
    citySelect?: CitySelectType
    cities?: Array<{ id: string | number; name: string }>
}

/**
 * STATIC SCREENS
 */
export type StaticScreensType = {
    id: number
    alias: string
    title: string
    content: string
}

/**
 * OUTSIDE DOC
 */

export type OutsideDocType = {
    id: number
    alias: string
    title: string
    url: string
}

/**
 * PAGES SETTINGS
 */
export type SettingsKodType = {
    lifetime: number | string
    maxUnsuccessfulAttempts: number | string
    maxInHourCount: number | string
    maxDaylyCount: number | string
}
export type SettingsPaymentType = {
    tinkoffMerchantId: string
}
export type SettingsSocialType = {
    id: number
    name: string
    appId: string
    appSecret: string
}
export type SettingsSocialUpdateType = {
    appId: string
    appSecret: string
}

export enum SettingsNotificationNames {
    EMAIL = 'email',
    SMS = 'sms',
    VIBER = 'viber',
    WHATSAPP = 'whatsapp',
    PUSH = 'push',
}
export type SettingsNotificatonType = {
    [SettingsNotificationNames.EMAIL]: {
        devinoLogin: string
        devinoPassword: string
        senderName: string
        senderEmail: string
        frequency?: number
    }
    [SettingsNotificationNames.SMS]: {
        gatewayLogin: string
        gatewayPassword: string
        senderName: string
        frequency?: number
    }
    [SettingsNotificationNames.VIBER]: {
        devinoLogin: string
        devinoPassword: string
        senderName: string
        frequency?: number
    }
    [SettingsNotificationNames.WHATSAPP]: {
        devinoLogin: string
        devinoPassword: string
        senderName: string
        frequency?: number
    }
    [SettingsNotificationNames.PUSH]: {
        firebaseProjectId: string
        huaweiAppId: string
        huaweiAppSecret: string
        frequency?: number
    }
}

export type SettingsLoymaxType = {
    host: string
    username: string
    password: string
    updatePeriodInactiveUsers?: number
}

export type SettingsAuthType = {
    secretKey: string
}

export type SettingsGeneralType = {
    timezone?: number
    notificationsFrequency: number
}

// FAVORITE CATEGORIES

export type FavoriteCategoriesType = {
    id: number
    name: string
    preferenceType: number
    preferenceValue: number
    isPublic: boolean
    photo: Phototype | null
    accepted: boolean
}
export type CreateFavoriteCategoriesType = {
    name?: string
    preferenceType?: number
    preferenceValue?: number
    isPublic?: boolean
    photo?: Phototype | null
    accepted?: boolean
}

// USERS
export enum GenderType {
    MAIL = 'male',
    FEMALE = 'female',
    OTHER = 'other',
}
export enum RoleCode {
    CLIENT = 'client',
    ADMIN = 'admin',
    MANAGER = 'manager',
}

export enum ReasonDeleteUser {
    REJECT = 'reject',
    BAN = 'ban',
}

export type Counters = {
    id: number
    loymaxId: string
    name: string
}

export type CreateCounters = {
    loymaxId: string
    name: string
}

export type UserAtributes = {
    attributeName: string
    id: number
    name: string
}

export type UserRoleType = {
    id: number
    code: string
    name: string
}

export type DeleteInfoType = {
    reason: ReasonDeleteUser
    message: string
}

export type UserCardType = {
    id: number
    number: string
    barCode: string
    block: boolean
    expiryDate: number
    status: string
    imOwner: boolean
}

export type UserType = {
    id: number
    firstName: string
    lastName: string
    patronymicName: string
    phoneNumber: string
    email: string
    gender: 'male' | 'female' | 'other'
    birthday: number
    photo: Phototype | null
    draft: boolean
    roles: Array<UserRoleType>
    deleteInfo: DeleteInfoType | null
    device: {
        identifier: string
        data: string
    }
}

export type MessageType = {
    id: number
    sender: {
        id: number
        firstName: string
        lastName: string
        photo: {
            uuid: string
            name: string
            ext: string
            size: number
            mime: string
            url: string
        }
    } | null
    messageType: 'text' | 'file'
    text: string
    file: {
        uuid: string
        name: string
        ext: string
        size: number
        mime: string
        url: string
    }
    isReceived: boolean
    isRead: boolean
    createdAt: number
    updatedAt: number
}

export type FeedbackChatsList = {
    id: number
    user: {
        user: UserType
        rejectPaperChecks: boolean
        card: UserCardType[]
    }
    device: any
    lastMessage: MessageType
    unreadMessagesCount: number
}

export type FeedbackItemType = {
    id: number
    user: UserType
    lastMessage: {
        id: number
        sender: {
            id: number
            firstName: string
            lastName: string
            photo: {
                uuid: string
                name: string
                ext: string
                size: number
                mime: string
                url: string
            }
        } | null
        messageType: 'text' | 'file'
        text: string
        file: {
            uuid: string
            name: string
            ext: string
            size: number
            mime: string
            url: string
        }
        isReceived: boolean
        isRead: boolean
        createdAt: number
        updatedAt: number
    }
    unreadMessagesCount: number
}

export type CreateUserType = {
    firstName?: string
    lastName?: string
    patronymicName?: string
    phoneNumber?: string
    email?: string
    gender?: GenderType
    birthday?: number
    draft?: boolean
    //потом изменить type roles
    roles?: Array<{ id: string | number }>
}

// USERS GROUP
export type UsersGroup = {
    id: number
    name: string
    isSys?: boolean
}
export type CreateUsersGroup = {
    name: string
}

// BLOCKS

export type BlockType = {
    code: string
    name?: string
    order: number
    showCounter: boolean
    showEndDate: boolean
    showPercents: boolean
    showBillet: boolean
}

// ATTRIBUTES
export type AttributesType = {
    id: number
    name: string
    icon: Phototype | null
    draft: boolean
    showOnPreview?: boolean
}

// GOODS

export type GoodsType = {
    id?: number
    goodsId: string
    name: string
    description: string
    preview: {
        uuid?: string
        name?: string
        ext?: string
        size?: number
        mime?: string
        url?: string
    } | null
    photo: {
        uuid?: string
        name?: string
        ext?: string
        size?: number
        mime?: string
        url?: string
    } | null
}

// NotificationType
export enum NotificationSortType {
    PUSH = 'push',
    SMS = 'sms',
    EMAIL = 'email',
    VIBER = 'viber',
    WHATSAPP = 'whatsapp',
}

export type NotificationScreenType = {
    id: number
    code: string
    name: string
}

export type TriggerType = {
    id: number
    code: string
    name: string
}
export type NotificationTypesType = {
    id: number
    name: string
}

export type NotificationTriggerType = {
    id: number
    triggerType: TriggerType
    dateStart: number
    dateEnd: null | number
    frequency: number | null
    timeStart: string
    timeEnd: string
    timeUnit: string
}

export type NotificationType = {
    id: number
    messageType: NotificationSortType
    name: string
    subject: string
    message: string
    messageHTML: string
    screen: NotificationScreenType
    notificationType: NotificationTypesType
    currencyId: number
    currencyDaysBeforeBurning: number
    sendToEveryone: boolean
    users: Array<User>
    usersGroup: Array<UsersGroup>
    isSystem: boolean
    requred: boolean
    trigger: NotificationTriggerType
    lifetime?: number
    photo?: Phototype
    daysWithoutPurchasing?: number
}

export type updateNotificationType = {
    messageType: NotificationSortType
    name?: string
    subject?: string
    message?: string
    messageHTML?: string
    screenId?: number
    notificationTypeId?: number
    currencyId?: number
    currencyDaysBeforeBurning?: number
    sendToEveryone?: boolean
    users?: number[]
    usersGroup?: number[]
    requred?: boolean
    trigger?: updateNotificationTypeTrigger
    lifetime?: number
    isSystem?: boolean
    daysWithoutPurchasing?: number
}

export type updateNotificationTypeTrigger = {
    triggersTypeId?: number
    dateStart?: number
    dateEnd?: number
    frequency: number
    timeUnit?: string
    timeStart?: string
    timeEnd?: string
}

export type responsUpdateNotificationTypeType = {
    id: number
    messageType: string
    name: string
    subject: string
    message: string
    messageHTML: string
    screen: responseUpdateNotificationTypeScreen
    notificationType: responseUpdateNotificationType
    sendToEveryone: boolean
    users: responseUpdateNotificationTypeUser[]
    usersGroup: NotificationType[]
    isSystem: boolean
    requred: boolean
    trigger: responseUpdateNotificationTypeTrigger
    lifetime: number
}

export type responseUpdateNotificationType = {
    id: number
    name: string
}

export type responseUpdateNotificationTypeScreen = {
    id: number
    code: string
    name: string
}

export type responseUpdateNotificationTypeTrigger = {
    id: number
    triggerType: Screen
    dateStart: number
    dateEnd: null
    frequency: number
    timeStart: string
    timeEnd: string
}

export type responseUpdateNotificationTypeUser = {
    id: number
    firstName: string
    lastName: string
    phoneNumber: string
    email: string
}

export type PushActionType = {
    id: number
    name: string
    code: string
}

// Application
export type FetchCityType = {
    name: string
    createdAt: number
    id: number
    timezone: number
    updatedAt: number
}

// StatusBuyers

export type StatusBuyersType = {
    id: 1
    name: string
    threshold: number
    icon: Phototype
}

export type CreateStatusBuyersType = {
    name?: string
    threshold?: number | string
    icon?: Phototype
    draft?: boolean
}

// FeedBackTheme

export type FeedBackThemeType = {
    id: number
    name: string
}
export type CreateFeedBackThemeType = {
    name: string
    draft?: boolean
}

// FeedBack

export type FeedBackType = {
    id: number
    user: {
        firstName: string
        lastName: string
        phoneNumber: string
        email: string
        emailConfirmed: true
        gender: 'male' | 'female' | 'other'
        birthday: number
        photo: Phototype
    }
    fullName: string
    email: string
    subject: {
        id: number
        name: string
    }
    message: string
    attachments: Phototype[]
}

export type QuestionAnswerType = {
    id: number
    question: string
    answer: string
    order?: number
}

export type CreateQuestonAnswerType = {
    question?: string
    answer?: string
    order?: number
    draft?: boolean
}

// Shops

export enum ShopStatus {
    OPEN = 'Open',
    // CLOSED_UNTIL = 'ClosedUntil',
    ON_REPAIR = 'OnRepair',
    CLOSED = 'Closed',
}
export type MerchantFormatType = {
    id: number
    name: string
    icon: Phototype
    draft: boolean
}
export type ShopsType = {
    id: number
    title: string
    address: string
    longitude: number
    latitude: number
    format: MerchantFormatType
    workingHoursFrom: string
    workingHoursTo: string
    status: ShopStatus
    workingStatus: ShopStatus
    attributes: Attributes[]
    loymaxLocationId: string
    isPublic: boolean
}
export type CreateShopsType = {
    title?: string
    address?: string
    longitude?: number
    latitude?: number
    formatId?: number
    workingHoursFrom?: string
    workingHoursTo?: string
    workingStatus?: ShopStatus // Open, OnRepair, Closed,
    attributes?: number[]
    draft?: boolean
    isPublic?: boolean
}

export type Attributes = {
    id: number
    name: string
    icon: Phototype
}

// PersonalProducts

export type PersonalProductsType = {
    id: number
    goodsId: string
    name: string
    description: string
    preview: Phototype
    photo: Phototype
}
export type CreatePersonalProductsType = {
    goodsId: string
    name: string
    draft?: boolean
    description?: string
    preview?: Phototype
    photo?: Phototype
}
//person

// deliveryOptions

export type DeliveryType = {
    id: number
    title: string
    shortDescription: string
    url?: string
    photo?: null | Phototype
    isPublic?: boolean
    draft?: boolean
}

export type DeliveryCreateType = {
    title?: string
    shortDescription?: string
    url?: string
    isPublic?: boolean
    draft?: boolean
}

/**
 * code type
 */
export enum QuestionCode {
    BAD = 'bad',
    GREATE = 'greate',
    GOOD = 'good',
}

export type QuestionRatingStoreOptionsType = {
    id: number
    answer: string
}
export type QuestionRatingStoreType = {
    code: string
    question: string
    grade: Array<number>
    type: string
    options: QuestionRatingStoreOptionsType[]
}
// export type createQuestionRatingStoreType = {
//   code: string;
//   question: string;
//   grade: Array<number>;
//   type: string;
//   options: Array<QuestionRatingStoreOptionsType>;
// };
// export type requestcreateQuestionRatingStoreType = {
//   code: string;
//   name: string;
//   grade: Array<number>;
//   type: string;
// };
// collection_Screns

export type collectionScrensType = {
    id?: number
    code: string
    name: string
}

// Reviews screen

export type ReviewsType = {
    id: number
    user: ReviewsTypeUser
    merchant: ReviewsTypeMerchant
    grade: number
    comment: string
    options: Option[]
    createdAt: number
    updatedAt: number
}

export type ReviewsTypeMerchant = {
    id: number
    title: string
    address: string
}

export type Option = {
    id: number
    answer: string
}

export type ReviewsTypeUser = {
    id: number
    firstName: string
    lastName: string
}

export type Currency = {
    id: number
    name: string
    description: string
    isDeleted: boolean
    nameCases: {
        nominative: string
        genitive: string
        plural: string
        abbreviation: string
    }
}
