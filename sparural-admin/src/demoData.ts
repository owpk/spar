import {
    AttributesType,
    BannerScreenType,
    BlockType,
    CitySelectType,
    collectionScrensType,
    DeliveryType,
    InfoScreenType,
    NotificationSortType,
    NotificationType,
    OutsideDocType,
    QuestionRatingStoreType,
    ReasonDeleteUser,
    ReviewsType,
    StaticScreensType,
    UsersGroup,
    UserType,
} from './types'

export const infoScreensDemo: Array<InfoScreenType> = [
    {
        id: 1,
        text: ' BBBB aaaaa',
        photo: {
            url: 'https://www.1zoom.ru/big2/930/257854-svetik.jpg',
        },
        isPublic: true,
        citySelect: CitySelectType.SELECTION,
        cities: [{ id: 1, name: 'Калининград' }],
        dateStart: 0,
        dateEnd: 0,
    },
    {
        id: 1,
        text: ' CCCCCCCCCCCCCB aaaaa',
        photo: null,
        isPublic: false,
        citySelect: CitySelectType.ALL,
        cities: [
            { id: 1, name: 'Калининград' },
            { id: 4, name: 'Воронеж' },
        ],
        dateStart: 0,
        dateEnd: 0,
    },
]

export const bannersDemo: Array<BannerScreenType> = [
    // {
    //   id: 1,
    //   photo: {
    //     uuid: "b5d0de18-32f8-11ec-8d3d-0242ac130003",
    //     name: "filename",
    //     ext: ".png",
    //     size: 4000,
    //     mime: "image/png",
    //     url: "https://www.1zoom.ru/big2/930/257854-svetik.jpg"
    //   },
    //   title: "О приложеy   ии",
    //   description:
    //     "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Hendrerit purus bibendum facilisis vitae risus at amet, ipsum.",
    //   order: 1,
    //   url: "url to page",
    //   mobileNavigateTarget: {
    //     id: 1,
    //     name: "Уведомления",
    //     code: "notifications"
    //   },
    //   citySelect: "Selection",
    //   cities: [
    //     {
    //       id: 1,
    //       name: "Магнитогорск"
    //     }
    //   ],
    //   isPublic: true
    // }
]

export const staticDemo: Array<StaticScreensType> = [
    {
        id: 1,
        alias: 'loyalty-program',
        title: 'Программа лояльности',
        content: '<p>Текст</p>',
    },
    {
        id: 2,
        alias: 'loyalty-program 2',
        title: 'Программа лояльности 22222',
        content: '<p>Текст</p>',
    },
    {
        id: 2,
        alias: 'loyalty-program 2',
        title: 'Программа лояльности 22222',
        content: '<p>Текст</p>',
    },
    {
        id: 2,
        alias: 'loyalty-program 2',
        title: 'Программа лояльности 22222',
        content: '<p>Текст</p>',
    },
    {
        id: 2,
        alias: 'loyalty-program 2',
        title: 'Программа лояльности 22222',
        content: '<p>Текст</p>',
    },
]

// export const UsersDemo: Array<UserType> = [
//     {
//         id: 1,
//         firstName: 'Имя',
//         lastName: 'Фамилия',
//         patronymicName: 'Отчество',
//         phoneNumber: '79999999999',
//         email: 'user@example.com',
//         gender: 'male',
//         birthday: 686966400,
//         photo: {
//             uuid: 'b5d0de18-32f8-11ec-8d3d-0242ac130003',
//             name: 'filename',
//             ext: '.png',
//             size: 4000,
//             mime: 'image/png',
//             url: 'https://www.1zoom.ru/big2/930/257854-svetik.jpg',
//         },
//         draft: false,
//         roles: [
//             {
//                 id: 2,
//                 code: 'client',
//                 name: 'Клиент',
//             },
//         ],
//         deleteInfo: {
//             reason: 'reject' as ReasonDeleteUser,
//             message: 'GHBXBYF',
//         },
//     },
// ]

export const UsersGroupDemo: Array<UsersGroup> = [
    {
        id: 1,
        name: 'TEST',
        isSys: false,
    },
    {
        id: 2,
        name: 'TEST-2',
        isSys: true,
    },
]

export const BlocksDemo: Array<BlockType> = [
    {
        code: 'coupones',
        name: 'Персональные купоны',
        order: 1,
        showCounter: true,
        showEndDate: true,
        showPercents: true,
        showBillet: true,
    },
    {
        code: 'main',
        name: 'Main купоны',
        order: 2,
        showCounter: true,
        showEndDate: false,
        showPercents: true,
        showBillet: false,
    },
]

export const AttributesDemo: Array<AttributesType> = [
    {
        id: 5,
        name: '',
        icon: {
            uuid: undefined,
            name: undefined,
            ext: undefined,
            size: undefined,
            mime: undefined,
            url: 'https://pbs.twimg.com/media/Ekja3s5XEAMjtmF.jpg',
        },
        draft: false,
    },
    {
        id: 6,
        name: '',
        icon: {
            uuid: undefined,
            name: undefined,
            ext: undefined,
            size: undefined,
            mime: undefined,
            url: 'https://pbs.twimg.com/media/Ekja3s5XEAMjtmF.jpg',
        },
        draft: false,
    },
]

export const NotificationsDemo: Array<NotificationType> = [
    {
        id: 1,
        messageType: 'push' as NotificationSortType,
        name: 'Название рассылки',
        subject: '',
        message: 'Текст сообщения',
        messageHTML: '',
        screen: {
            id: 1,
            code: 'offers',
            name: 'Акции',
        },
        notificationType: {
            id: 1,
            name: 'Акции',
        },
        sendToEveryone: false,
        users: [
            {
                id: 1,
                firstName: 'Иван',
                lastName: 'Иванов',
                phoneNumber: '79999999999',
                email: 'user@example.com',
            },

            // ...
        ],
        usersGroup: [
            {
                id: 1,
                name: 'Группа пользователей',
            },
        ],
        isSystem: false,
        requred: false,
        trigger: {
            id: 1,
            triggerType: {
                id: 1,
                code: 'made-purchase-in-store',
                name: 'Совершена покупка в магазине',
            },
            dateStart: 1636599129,
            dateEnd: null,
            frequency: 172800,
            timeStart: '08:00',
            timeEnd: '22:00',
            timeUnit: 'm',
        },
        lifetime: 900,
        currencyDaysBeforeBurning: 0,
        currencyId: 0,
    },
]

export const _DEMO_EXTERNAL: OutsideDocType[] = [
    {
        id: 1,
        alias: 'loyalty-program',
        title: 'Программа лояльности',
        url: 'path to document',
    },
    {
        id: 2,
        alias: 'loyalty-program-2',
        title: 'Программа лояльности',
        url: 'path to document',
    },
]

export const DeliveryDemoData: DeliveryType[] = [
    {
        id: 1,
        title: 'SPAR в Сбермаркете!',
        shortDescription: 'Теперь в Сбермаркете есть и SPAR',
        url: 'https://example.ru/path',
        photo: {
            uuid: 'b5d0de18-32f8-11ec-8d3d-0242ac130003',
            name: 'filename',
            ext: '.png',
            size: 4000,
            mime: 'image/png',
            url: 'https://storage.ru/path/to/filename.png',
        },
        isPublic: true,
        draft: false,
    },
    {
        id: 2,
        title: 'SPAR в Сбермаркете!',
        shortDescription: 'Теперь в Сбермаркете есть и SPAR',
        url: 'https://example.ru/path',
        photo: {
            uuid: 'b5d0de18-32f8-11ec-8d3d-0242ac130003',
            name: 'filename',
            ext: '.png',
            size: 4000,
            mime: 'image/png',
            url: 'https://storage.ru/path/to/filename.png',
        },
        isPublic: true,
        draft: false,
    },
    {
        id: 3,
        title: 'SPAR в Сбермаркете!',
        shortDescription: 'Теперь в Сбермаркете есть и SPAR',
        url: 'https://example.ru/path',
        photo: {
            uuid: 'b5d0de18-32f8-11ec-8d3d-0242ac130003',
            name: 'filename',
            ext: '.png',
            size: 4000,
            mime: 'image/png',
            url: 'https://storage.ru/path/to/filename.png',
        },
        isPublic: true,
        draft: false,
    },
]

export const quaestuinRatingStore: QuestionRatingStoreType[] = [
    {
        code: 'greate',
        question: 'Расскажите пожалуйста, что Вам понравилось?',
        grade: [5],
        type: 'NoAnswer',
        options: [
            {
                id: 1,
                answer: 'Ответ при постановке покупателем менее 5 звёзд за посещение №1',
            },
        ],
    },
    {
        code: 'greate',
        question: 'Расскажите пожалуйста, что Вам понравилось?',
        grade: [5],
        type: 'NoAnswer',
        options: [
            {
                id: 2,
                answer: 'Ответ при постановке покупателем менее 5 звёзд за посещение №1',
            },
        ],
    },
    {
        code: 'greate',
        question: 'Расскажите пожалуйста, что Вам понравилось?',
        grade: [5],
        type: 'NoAnswer',
        options: [
            {
                id: 3,
                answer: 'Ответ при постановке покупателем менее 5 звёзд за посещение №1',
            },
        ],
    },
]

export const dataReviews: ReviewsType[] = [
    {
        id: 1,
        user: {
            id: 100,
            firstName: 'Иван',
            lastName: 'Иванов',
            // Отчество
        },
        merchant: {
            id: 1,
            title: 'Гипермараптакет',
            address: 'ул. Цвилппилинга, 25, Челяптабинск',
        },
        grade: 2,
        comment: 'Комментарий',
        options: [
            {
                id: 1,
                answer: 'Ответ при постановке покупателем менее 5 звёзд за посещение №1',
            },
        ],
        createdAt: 1636214355, // Дата, когда отзыв был оставлен
        updatedAt: 1636214355,
    },
    {
        id: 2,
        user: {
            id: 100,
            firstName: 'Иван',
            lastName: 'Иванов',
            // Отчество
        },
        merchant: {
            id: 1,
            title: 'Гипермаркет',
            address: 'ул. Цвиллинга, 25, Челябинск',
        },
        grade: 1,
        comment: 'Комментарий',
        options: [
            // Причина
            {
                id: 1,
                answer: 'Ответ при постановке покупателем менее 5 звёзд за посещение №1',
            },
        ],
        createdAt: 1636214355, // Дата, когда отзыв был оставлен
        updatedAt: 1636214355,
    },
    {
        id: 3,
        user: {
            id: 100,
            firstName: 'Иван',
            lastName: 'Иванов',
            // Отчество
        },
        merchant: {
            id: 1,
            title: 'Гипермаркет',
            address: 'ул. Цвиллинга, 25, Челябинск',
        },
        grade: 5,
        comment: 'Комментарий',
        options: [
            // Причина
            {
                id: 1,
                answer: 'Ответ при постановке покупателем менее 5 звёзд за посещение №1',
            },
        ],
        createdAt: 1636214355, // Дата, когда отзыв был оставлен
        updatedAt: 1636214355,
    },
]

export const dataСollectionScrens: collectionScrensType[] = [
    {
        id: 1,
        code: 'notifications',
        name: 'Уведомления',
    },
    {
        id: 2,
        code: '2otifications',
        name: '2ведомления',
    },
    {
        id: 3,
        code: '3otifications',
        name: '3ведомления',
    },
]
