import { FC, useCallback, useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { MainLayout } from '../../../complexes/MainLayout'
import memoize from 'memoize-one'
import { NotificationSortType, NotificationType } from '../../../../types'
import { PushTab } from './PushTab'
import { NotificationService } from '../../../../services/NotificationService'
import { message } from 'antd'
import { SmsTab } from './SmsTab'
import { EmailTab } from './EmailTab'
import { ViberTab } from './ViberTab'
import { WhatsappTab } from './WhatsappTab'
import { useLocation, useNavigate } from 'react-router-dom'
import { Routes } from '../../../../config'
import { DeleteTemplateContext } from './contexts'
import { DeleteModal } from '../../../complexes/DeleteModal'
import { getPeriodFromSeconds } from '../../../../utils/helpers'


export type DataTable = {
    id: number,
    name: string
    trigger: string
    start_date: string
    frenquency: string | number
    spam_type: string
    text: string
    where_link_lead: string
    recipients: string
    mail_theme: string
    subject: string
}

const NotificationScreen: FC = () => {
    const { t } = useTranslation()
    const navigate = useNavigate()
    const activeTabQuery = useLocation().search.split('=')[1]
    const [list, setList] = useState<Array<DataTable>>([])
    const currentList = useRef<Array<DataTable>>(list)
    currentList.current = list
    const [loading, setLoading] = useState(false)
    const offset = useRef(0)
    const has = useRef(true)

    const [del, setDel] = useState<number>(0)

    const [activeTab, setActiveTab] = useState<NotificationSortType>(activeTabQuery ? activeTabQuery as NotificationSortType : NotificationSortType.PUSH)

    const tabsArr = memoize(() => {
        return Object.keys(NotificationSortType).map((key: string) => {
            let notify: any = NotificationSortType
            return ({
                name: t(`notifications.${notify[key]}`),
                key: notify[key]
            })
        })
    })

    const load = async () => {
        if (!has.current || loading) {
            return
        }

        setLoading(true)
        try {
            const result = await NotificationService.getNotifications({
                offset: offset.current,
                messageType: activeTab
            })
            if (!result.length) {
                has.current = false
                setLoading(false)
                return
            }

            let rows: Array<DataTable> = []
            rows = result.map((item: NotificationType) => {

                const recipients = []
                if (item.sendToEveryone) {
                    recipients.push(t("table.recipient_all"))
                } else {
                    if (item.users) {
                        if (item.users.length === 1) {
                            const userFullName = []
                            if (item.users[0].lastName) {
                                userFullName.push(item.users[0].lastName)
                            }
                            if (item.users[0].firstName) {
                                userFullName.push(item.users[0].firstName)
                            }

                            recipients.push(userFullName.join(' '))
                        } else if (item.users.length > 1) {
                            recipients.push(`${t('table.users_count')} ${item.users.length}`)
                        }
                    }
                    if (item.usersGroup) {
                        if (item.usersGroup.length === 1) {
                            recipients.push(item.usersGroup[0].name)
                        } else if (item.usersGroup.length > 1) {
                            recipients.push(`${t('table.users_groups_count')} ${item.usersGroup.length}`)
                        }
                    }
                }

              //  const frequencyData = getPeriodFromSeconds(item?.trigger?.frequency || 0)
              const frequencyData = item?.trigger?.frequency
                return ({
                    id: item?.id,
                    name: item?.name,
                    trigger: item?.trigger?.triggerType?.name || '',
                    start_date: item?.trigger?.timeStart,
                   // frenquency: `${frequencyData.value} ${t(`options.interval.${frequencyData.period}`).slice(0, 3)}`,
                    frenquency: frequencyData,
                    spam_type: item?.notificationType?.name || '',
                    text: item?.message || '',
                    where_link_lead: item?.screen?.name || '',
                    subject: item?.subject || '',
                    recipients: recipients.join(', ')
                }) as DataTable
            })

            offset.current = offset.current + result.length


            setList([...currentList.current, ...rows])
            if (result.length === 1) {
                has.current = false
            }
        } catch (error) {
            message.error(t("errors.get_data"))
        }
        setLoading(false)


    }
    const clear = async () => {
        has.current = true
        offset.current = 0
        currentList.current = []
        setList([])
        setLoading(false)
        await load()
    }
    useEffect(() => {
        clear().then()
    }, [activeTab])


    /**
     * go to create page
     */
    const goToCreate = useCallback(() => {
        navigate(`${Routes.NOTIFICATIONS_CREATE}?${activeTab}`)
    }, [activeTab, navigate])

    /**
     * change active tab
     */
    const onChangeActiveTab = (e: string) => {
        setActiveTab(e as NotificationSortType)
        navigate(`${Routes.NOTIFICATIONS}?tab=${e}`)
    }

    /**
     * delete - template
     */
    const deleteTemlate = useCallback(async () => {
        const response = await NotificationService.deleteTeplate(del)
        if (response) {
            message.success(t("suсcess_messages.delete_data"))
            setList(prev => prev.filter(i => i.id !== del))
            setDel(0)

        } else {
            message.success(t("errors.delete_data"))

        }
    }, [t, del])

    return (
        <MainLayout
            tabActive={activeTab}
            onEndReached={load}
            onChangeTab={onChangeActiveTab}
            tabs={tabsArr()}
            onAdd={goToCreate}
            title={t("screen_title.notification_screen")}
        >
            <>
                <DeleteTemplateContext.Provider value={setDel}>
                    {activeTab === NotificationSortType.PUSH && <PushTab
                        data={list}
                    />}
                    {activeTab === NotificationSortType.SMS && <SmsTab
                        data={list}
                    />}
                    {activeTab === NotificationSortType.EMAIL && <EmailTab
                        data={list}
                    />}
                    {activeTab === NotificationSortType.VIBER && <ViberTab
                        data={list}
                    />}
                    {activeTab === NotificationSortType.WHATSAPP && <WhatsappTab
                        data={list}
                    />}

                </DeleteTemplateContext.Provider>
                <DeleteModal
                    onSubmit={deleteTemlate}
                    onCancel={() => setDel(0)}
                    visible={!!del}
                />
            </>
        </MainLayout>
    )
}


export default NotificationScreen;

