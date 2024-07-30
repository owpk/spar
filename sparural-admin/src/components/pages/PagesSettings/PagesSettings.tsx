import { message} from 'antd'
import React, { FC, useCallback, useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useAppDispatch } from '../../../hooks/store'
import { SettingsService } from '../../../services/SettingsPagesService'
import {
    SettingsAuthType,
    SettingsGeneralType,
    SettingsKodType,
    SettingsLoymaxType,
    SettingsNotificatonType,
    SettingsPaymentType,
    SettingsSocialType,
} from '../../../types'
import { MainLayout } from '../../complexes/MainLayout'

import { TabAuth } from './tabs/TabAuth'
import memoize from 'memoize-one'
import { TabGeneral } from './tabs/TabGeneral'
import { TabKod } from './tabs/TabKod'
import { TabLoymax } from './tabs/TabLoymax'
import { TabNetwork } from './tabs/TabNetwork'
import { TabNotifications } from './tabs/TabNotifications'
import { TabPayment } from './tabs/TabPayment'
import { useLocation, useNavigate } from 'react-router-dom'
import { Routes } from '../../../config'

type Props = {}

export enum SettingsTab {
    CODES = 'codes',
    SOCIAL = 'social',
    PAYMENT = 'payment',
    NOTIFICATION = 'notifical',
    LOYMAX = 'loymax',
    AUTH = 'auth',
    GENERAL = 'general'
}

const PagesSettings: FC<Props> = () => {
    const { t } = useTranslation()
    const navigation = useNavigate()
    const activeTabQuery = useLocation().search.split('=')[1]

    const [loading, setLoading] = useState(false)

    const [codes, setCodes] = useState<SettingsKodType>({} as SettingsKodType)
    const [social, setSocial] = useState<SettingsSocialType[]>([])
    const [payment, setPayment] = useState<SettingsPaymentType>({} as SettingsPaymentType)
    const [notifical, setNotifical] = useState<SettingsNotificatonType>({} as SettingsNotificatonType)
    const [loymax, setLoymax] = useState<SettingsLoymaxType>({} as SettingsLoymaxType)
    const [auth, setAuth] = useState<SettingsAuthType>({} as SettingsAuthType)
    const [general, setGeneral] = useState<SettingsGeneralType>({} as SettingsGeneralType)

    const [activeTab, setActiveTab] = useState<SettingsTab>(activeTabQuery ? activeTabQuery as SettingsTab : SettingsTab.CODES)


    const tabsArr = memoize(() => {
        return Object.values(SettingsTab).map((value: SettingsTab) => {
            return ({
                name: t(`settingsPages.${value}`),
                key: value
            })
        })
    })

    /**
     *
     * load info for of each components
     */

    // !TODO - connecte back
    const load = async (key: SettingsTab) => {
        switch (key) {
            case SettingsTab.CODES:
                try {
                    setLoading(true)
                    const result = await SettingsService.getSettingsCoder()
                    setCodes(result)
                    setLoading(false)
                } catch {
                    setLoading(false)
                    message.error(t('errors.get_data'))
                }

                break
            case SettingsTab.SOCIAL:
                try {
                    setLoading(true)
                    const result = await SettingsService.getSettingsSocial()
                    setSocial(result)
                    setLoading(false)
                } catch {
                    setLoading(false)
                    message.error(t('errors.get_data'))
                }

                break
            case SettingsTab.PAYMENT:
                try {
                    setLoading(true)
                    const result = await SettingsService.getSettingsPayment()
                    setPayment(result)
                    setLoading(false)
                } catch {
                    setLoading(false)
                    message.error(t('errors.get_data'))
                }

                break
            case SettingsTab.NOTIFICATION:
                try {
                    setLoading(true)
                    const result =
                        await SettingsService.getSettingsNotification()
                    setNotifical(result)
                    setLoading(false)
                } catch {
                    setLoading(false)
                    message.error(t('errors.get_data'))
                }

                break
            case SettingsTab.LOYMAX:
                try {
                    setLoading(true)
                    const result = await SettingsService.getSettingsLoymax()
                    setLoymax(result)
                    setLoading(false)
                } catch {
                    setLoading(false)
                    message.error(t('errors.get_data'))
                }

                break
            case SettingsTab.AUTH:
                try {
                    setLoading(true)
                    const result = await SettingsService.getSettingsAuth()
                    setAuth(result)
                    setLoading(false)
                } catch {
                    setLoading(false)
                    message.error(t('errors.get_data'))
                }

                break
            case SettingsTab.GENERAL:
                try {
                    setLoading(true)
                    const result = await SettingsService.getSettingsGeneral()
                    setGeneral(result)
                    setLoading(false)
                } catch {
                    setLoading(false)
                    message.error(t('errors.get_data'))
                }

                break
        }
    }
    useEffect(() => {
        load(activeTab).then()
    }, [activeTab])

    /**
     * changeActiveTab
     */

    const onChangeTab = (e: string) => {
        setActiveTab(e as SettingsTab)
        navigation(`${Routes.SETTINGS}?tab=${e}`)
    }
    return (
        <MainLayout
            tabActive={activeTab}
            onChangeTab={onChangeTab}
            tabs={tabsArr()}
            isLoading={loading} title={'Настройки'}>
            <>
                <>
                {activeTab === SettingsTab.CODES && <TabKod
                    codes={codes}
                />}
                {activeTab === SettingsTab.SOCIAL && <TabNetwork
                    data={social}
                />}
                {activeTab === SettingsTab.PAYMENT && <TabPayment
                    data={payment}
                />}
                {activeTab === SettingsTab.NOTIFICATION && <TabNotifications
                    data={notifical}
                />}
                {activeTab === SettingsTab.LOYMAX && <TabLoymax
                    data={loymax}
                />}
                {activeTab === SettingsTab.AUTH && <TabAuth
                    data={auth}
                />}
                {activeTab === SettingsTab.GENERAL && <TabGeneral
                    data={general}
                />}
                </>
            </>
        </MainLayout>
    )
}
export default PagesSettings
