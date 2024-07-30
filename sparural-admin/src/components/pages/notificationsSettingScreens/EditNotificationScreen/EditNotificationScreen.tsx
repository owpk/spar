import { FC, useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useLocation, useNavigate } from 'react-router-dom'
import {
    NotificationSortType,
    NotificationTypesType,
    PushActionType,
    TriggerType,
    updateNotificationType,
} from '../../../../types'
import { MainLayout } from '../../../complexes/MainLayout'
import memoize from 'memoize-one'
import { EditWrapper } from '../../../simples/EditWrapper'
import { PushTab } from './PushTab'
import { SmsTab } from './SmsTab'
import { EmailTab } from './EmailTab'
import { ViberTab } from './ViberTab'
import { WhatsappTab } from './WhatsappTab'
import { NotificationService } from '../../../../services/NotificationService'
import {
    DraftNotification,
    NotificationContext,
    NotificationErrors,
    NotificationsTypeContext,
    PushActionsContext,
    TriggersContext,
    ValidateErrorContext,
    CurrenciesContext,
} from './contexts'
import { message } from 'antd'
import { SelectOption } from '../../../simples/Selector/OptionItem'
import { getPeriod } from '../../../../utils/helpers'
import {
    EntitiesFieldName,
    FileSource,
    IntervalTimeType,
    Routes,
} from '../../../../config'
import {
    UploadFileDocType,
    useUploadFileMutation,
} from '../../../../services/FileService'
import { CurrenciesService } from '../../../../services/CurrenciesService'

const _INIT_NITIFICATION: DraftNotification = {
    name: '',
    interval: {},
    startDate: {},
    subject: '',
    text: '',
    messageHTML: '',
    sendAll: false,
    users: [],
    required: false,
    isSystem: false,
}

const EditNotificationScreen: FC = () => {
    const { t } = useTranslation()
    const id = useLocation().search.split('=')[1]
    const activeTab = useLocation()
        .search.split('=')[0]
        .slice(1) as NotificationSortType
    const [currentTab, setCurrentTab] =
        useState<NotificationSortType>(activeTab)

    const navigate = useNavigate()

    const [triggers, setTriggers] = useState<Array<TriggerType>>([])
    const triggersOptions = useMemo<Array<SelectOption>>(() => {
        return triggers.map(trigger => ({value: trigger.code, label: trigger.name}))
    }, [triggers])
    const [pushActions, setPushActions] = useState<PushActionType[]>([])
    const [notificationsTypes, setNotificationsTypes] =
        useState<NotificationTypesType[]>()
    const [currencies, setCurrencies] = useState<SelectOption[]>([])

    const [notification, setNotification] =
        useState<DraftNotification>(_INIT_NITIFICATION)

    const [validateError, setValidateError] = useState<NotificationErrors>({})

    const [sendFile, { error }] = useUploadFileMutation()

    const isGet = useRef<boolean>(false)

    const IntervalOptions = memoize(() => {
        return Object.keys(IntervalTimeType).map((key: string) => {
            let option: any = IntervalTimeType
            return {
                value: option[key],
                label: t(`options.interval.${option[key]}`),
            }
        })
    })

    const pushActionsOptions: SelectOption[] = useMemo((): SelectOption[] => {
        return pushActions.map((i) => ({
            value: i.id,
            label: i.name,
        }))
    }, [pushActions])

    /**
     * get by id
     */

    const getNotificationById = useCallback(async () => {
        if (!id) return
        const response = await NotificationService.getNotificationById(+id)
        if (response) {
            const findtrigger = triggersOptions.find(
                (i) => i.value === response.trigger?.triggerType?.code
            )
            /*const findPeriod = IntervalOptions().find(
                (item) =>
                    item.value ===
                    getPeriodFromSeconds(response?.trigger?.frequency || 0)
                        .period
            )*/
            const findPushAction = pushActionsOptions.find(
                (i) => i.value === response?.screen?.id
            )
            const findNotificationType = notificationsTypes?.find(
                (i) => i.id === response.notificationType?.id
            )

            const findPeriod = IntervalOptions().find((item) => item.value === getPeriod(response?.trigger?.timeUnit).period)

            setNotification({
                name: response.name,
                text: response.message,
                messageHTML: response.messageHTML,
                subject: response.subject,
                sendAll: response.sendToEveryone,
                notificationType: findNotificationType
                    ? {
                          value: findNotificationType.id,
                          label: findNotificationType.name,
                      }
                    : undefined,
                required: response.requred,
                pushTarget: findPushAction,
                startDate: {
                    start: +response?.trigger?.dateStart || undefined,
                    end: response?.trigger.dateEnd
                        ? +response?.trigger?.dateEnd
                        : undefined,
                },
                interval: {
                    start: response?.trigger?.timeStart || undefined,
                    end: response?.trigger?.timeEnd || undefined,
                },
                currencyId: { value: response.currencyId, label: '' },
                currencyDaysBeforeBurning:
                    response.currencyDaysBeforeBurning,
                trigger: findtrigger,
                /*periodCount: getPeriodFromSeconds(
                    response.trigger.frequency || 0
                ).value,*/
                periodCount:response.trigger.frequency,
                period: findPeriod,
                users: response?.users.map((item) => ({
                    value: item.id,
                    label: `${item.lastName || ''} ${item.firstName || ''}`,
                })),
                group:
                    response.usersGroup && response.usersGroup.length > 0
                        ? {
                              value: response.usersGroup[0].id,
                              label: response.usersGroup[0].name,
                          }
                        : undefined,
                photoUrl: response?.photo || undefined,
                isSystem: response.isSystem || false,
                daysWithoutPurchasing: response.daysWithoutPurchasing,
            })
        }
    }, [IntervalOptions, id, notificationsTypes, pushActionsOptions, triggersOptions])

    useEffect(() => {
        if (
            id &&
            pushActionsOptions.length > 0 &&
            triggersOptions.length > 0 &&
            !isGet.current &&
            notificationsTypes &&
            currencies.length > 0
        ) {
            isGet.current = true
            getNotificationById().then()
        }
    }, [
        id,
        pushActionsOptions,
        triggersOptions,
        notificationsTypes,
        getNotificationById,
        currencies,
    ])

    /**
     * upload image
     */
    const uploadFile = useCallback(
        async (image: File, id: number) => {
            const sendData: UploadFileDocType = {
                source: FileSource.REQUEST,
                'source-parameters': JSON.stringify({}),
                entities: [
                    {
                        field: EntitiesFieldName.MESSAGE_TEMPLATE_PHOTO,
                        documentId: id,
                    },
                ],
                file: image,
            }
            await sendFile(sendData)
        },
        [sendFile]
    )

    /**
     * save function
     */
    const onHandleSave = useCallback(
        async (data?: updateNotificationType): Promise<number | undefined> => {
            try {
                if (!data) return undefined
                if (id) {
                    const response =
                        await NotificationService.updateNotifications(+id, data)
                    message.success(t('suсcess_messages.update_data'))
                    return response.id
                } else {
                    const response =
                        await NotificationService.createNotifications(data)
                    message.success(t('suсcess_messages.save_data'))

                    return response.id
                }
            } catch (error) {
                message.error(t('errors.save_data'))

                return undefined
            }
        },
        [id, t]
    )

    /**
     * save data
     */
    const onHandleSendToSaveData = useCallback(async () => {
        let hasError = false
        if (!notification.name) {
            hasError = true
            setValidateError((prev) => ({
                ...prev,
                name: t('errors.required_field'),
            }))
        }
        if (!notification.interval.start) {
            hasError = true
            setValidateError((prev) => ({
                ...prev,
                intervalStart: t('errors.required_field'),
            }))
        }
        if (!notification.interval.end) {
            hasError = true
            setValidateError((prev) => ({
                ...prev,
                intervalEnd: t('errors.required_field'),
            }))
        }
        if (!notification.trigger) {
            hasError = true
            setValidateError((prev) => ({
                ...prev,
                trigger: t('errors.required_field'),
            }))
        }
        if (!notification.startDate?.start) {
            hasError = true
            setValidateError((prev) => ({
                ...prev,
                startDateStart: t('errors.required_field'),
            }))
        }
        if (!notification.startDate?.end) {
            hasError = true
            setValidateError((prev) => ({
                ...prev,
                startDateEnd: t('errors.required_field'),
            }))
        }
        if (!notification.periodCount) {
            hasError = true
            setValidateError((prev) => ({
                ...prev,
                periodCount: t('errors.required_field'),
            }))
        }
        if (!notification.period) {
            hasError = true
            setValidateError((prev) => ({
                ...prev,
                period: t('errors.required_field'),
            }))
        }
        if (activeTab === NotificationSortType.EMAIL) {
            if (!notification.messageHTML) {
                hasError = true
                setValidateError((prev) => ({
                    ...prev,
                    messageHTML: t('errors.required_field'),
                }))
            }
            if (!notification.subject) {
                hasError = true
                setValidateError((prev) => ({
                    ...prev,
                    subject: t('errors.required_field'),
                }))
            }
        }
        if (hasError) return

        const triggerType = triggers.find(t => notification.trigger && t.code === notification.trigger.value)

        if (!triggerType) {
            throw new Error('Trigger Type not found')
        }

        const sendData: updateNotificationType = {
            messageType: activeTab,
            name: notification.name,
            message: notification.text || '',
            messageHTML: notification.messageHTML || undefined,
            subject: notification.subject || undefined,
            sendToEveryone: notification.sendAll,
            users: notification.users.map((i) => +i.value),
            usersGroup: notification.group ? [+notification.group?.value] : [],
            requred: notification.required,
            screenId: notification.pushTarget
                ? +notification.pushTarget.value
                : undefined,
            notificationTypeId: notification.notificationType
                ? +notification.notificationType.value
                : undefined,
            trigger: {
                triggersTypeId: triggerType.id,
                dateStart: notification.startDate?.start,
                dateEnd: notification.startDate?.end,
                timeStart: notification.interval?.start,
                timeEnd: notification.interval.end,
                frequency:  Number(notification.periodCount),
                timeUnit:notification.period?.value as IntervalTimeType
            },
            lifetime: 900,
            isSystem: notification.isSystem || false,
            currencyId: notification.currencyId?.value
                ? Number(notification.currencyId.value)
                : 0,
            currencyDaysBeforeBurning:
                notification.currencyDaysBeforeBurning
                    ? +notification.currencyDaysBeforeBurning
                    : 0,
            daysWithoutPurchasing: notification.daysWithoutPurchasing,
        }
        const currentId = await onHandleSave(sendData)
        if (notification.file && currentId) {
            await uploadFile(notification.file, currentId)
        }
        if (currentId) {
            navigate(`${Routes.NOTIFICATIONS}?tab=${activeTab}`)
        }
    }, [
        notification.name,
        notification.trigger,
        notification.text,
        notification.messageHTML,
        notification.subject,
        notification.sendAll,
        notification.users,
        notification.group,
        notification.required,
        notification.pushTarget,
        notification.notificationType,
        notification.startDate?.start,
        notification.startDate?.end,
        notification.interval?.start,
        notification.interval.end,
        notification.currencyId,
        notification.currencyDaysBeforeBurning,
        notification.periodCount,
        notification.period?.value,
        notification.isSystem,
        notification.file,
        notification.daysWithoutPurchasing,
        activeTab,
        onHandleSave,
        t,
        uploadFile,
        navigate,
    ])

    const tabsArr = memoize(() => {
        return Object.keys(NotificationSortType).map((key: string) => {
            let notify: any = NotificationSortType
            return {
                name: t(`notifications.${notify[key]}`),
                key: notify[key],
            }
        })
    })

    /**
     * fetch triggers
     */
    const getTriggers = useCallback(async () => {
        try {
            const response = await NotificationService.getTriggerTypes()
            setTriggers(response)
        } catch (error) {}
    }, [])
    /**
     * fetch push actions
     */
    const getPushActions = useCallback(async () => {
        try {
            const response = await NotificationService.getPushActions()
            setPushActions(response)
        } catch (error) {}
    }, [])
    /**
     * fetch notifications types
     */
    const getNotificationsTypes = useCallback(async () => {
        try {
            const response = await NotificationService.getNotificationsTypes()
            setNotificationsTypes(response)
        } catch (error) {}
    }, [])

    /**
     * fetch triggers
     */
    const getCurrencies = useCallback(async () => {
        try {
            const currencies = await CurrenciesService.getList()

            setCurrencies(currencies.map(currency => ({
                label: currency.name,
                value: currency.id
            })))
        } catch (error) {}
    }, [setCurrencies])

    useEffect(() => {
        getTriggers().then()
        getPushActions().then()
        getNotificationsTypes().then()
        getCurrencies().then()
    }, [getNotificationsTypes, getPushActions, getTriggers, getCurrencies])

    /**
     * change active tab
     */
    const onChangeActiveTab = (e: string) => {
        setValidateError({})
        setCurrentTab(e as NotificationSortType)
        navigate(`${Routes.NOTIFICATIONS_CREATE}?${e}`)
        // navigate(`${Routes.NOTIFICATIONS}?tab=${e}`)
    }

    return (
        <MainLayout
            tabs={tabsArr()}
            tabActive={id ? activeTab : undefined}
            onChangeTab={(key) =>
                !id && onChangeActiveTab(key as NotificationSortType)
            }
            defaultTab={activeTab}
            title={t('screen_title.notification_screen')}
        >
            <>
                <TriggersContext.Provider value={[triggersOptions]}>
                    <PushActionsContext.Provider
                        value={[pushActions, setPushActions]}
                    >
                        <NotificationsTypeContext.Provider
                            value={notificationsTypes || []}
                        >
                            <NotificationContext.Provider
                                value={[notification, setNotification]}
                            >
                                <CurrenciesContext.Provider
                                    value={[currencies, setCurrencies]}
                                >
                                    <ValidateErrorContext.Provider
                                        value={[validateError, setValidateError]}
                                    >
                                        <EditWrapper
                                            onSave={onHandleSendToSaveData}
                                            title={t(
                                                !id
                                                    ? 'common.add'
                                                    : 'common.edit_full'
                                            )}
                                        >
                                            <>
                                                {currentTab ===
                                                    NotificationSortType.PUSH && (
                                                    <PushTab />
                                                )}
                                                {currentTab ===
                                                    NotificationSortType.SMS && (
                                                    <SmsTab />
                                                )}
                                                {currentTab ===
                                                    NotificationSortType.EMAIL && (
                                                    <EmailTab />
                                                )}
                                                {currentTab ===
                                                    NotificationSortType.VIBER && (
                                                    <ViberTab />
                                                )}
                                                {currentTab ===
                                                    NotificationSortType.WHATSAPP && (
                                                    <WhatsappTab />
                                                )}
                                            </>
                                        </EditWrapper>
                                    </ValidateErrorContext.Provider>
                                </CurrenciesContext.Provider>
                            </NotificationContext.Provider>
                        </NotificationsTypeContext.Provider>
                    </PushActionsContext.Provider>
                </TriggersContext.Provider>
            </>
        </MainLayout>
    )
}

export default EditNotificationScreen
