import { message } from 'antd'
import React, { FC, useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { SettingsService } from '../../../../../services/SettingsPagesService'
import { SettingsNotificationNames, SettingsNotificatonType } from '../../../../../types'
import { BlockWrapper } from '../../../../simples/BlockWrapper'
import { Button } from '../../../../simples/Button'
import { ButtonType } from '../../../../simples/Button/Button'
import { InputHolder } from '../../../../simples/InputHolder'
import { TextField } from '../../../../simples/TextField'
import styles from './TabNotifications.module.scss'

type Props = {
    data: SettingsNotificatonType
}

const _INIT_NOTIFICATIONS: SettingsNotificatonType = {
    [SettingsNotificationNames.PUSH]: {
        firebaseProjectId: '',
        huaweiAppId: '',
        huaweiAppSecret: '',
        frequency: 0
    },
    [SettingsNotificationNames.EMAIL]: {
        devinoLogin: '',
        devinoPassword: '',
        senderName: '',
        senderEmail: '',
        frequency: 0
    },
    [SettingsNotificationNames.SMS]: {
        gatewayLogin: '',
        gatewayPassword: '',
        senderName: '',
        frequency: 0
    },
    [SettingsNotificationNames.VIBER]: {
        devinoLogin: '',
        devinoPassword: '',
        senderName: '',
        frequency: 0
    },
    [SettingsNotificationNames.WHATSAPP]: {
        devinoLogin: '',
        devinoPassword: '',
        senderName: '',
        frequency: 0
    },
}

const TabNotifications: FC<Props> = ({ data }) => {
    const { t } = useTranslation()
    const [settings, setSettings] = useState<SettingsNotificatonType>(_INIT_NOTIFICATIONS)
    const [errors, setErrors] = useState<Partial<SettingsNotificatonType>>({})

    useEffect(() => {
        setSettings(data)
    },[data])

    const onClickSave = async () => {
        let hasError: boolean = false
        Object.keys(settings).forEach((item) => {
            Object.keys(settings[item as SettingsNotificationNames]).forEach((field) => {
                if (!settings[item as SettingsNotificationNames][field as keyof SettingsNotificatonType[SettingsNotificationNames]]) {
                    setErrors(prev => ({ ...prev, [item as SettingsNotificationNames]: { ...prev[item as SettingsNotificationNames], [field as keyof SettingsNotificatonType[SettingsNotificationNames]]: t("errors.required_field") } }))
                    hasError = true
                }
            })
        })
        if (hasError) return
        try {
            await SettingsService.updateSettingsNotification(
                settings
            )
            message.success(t("suсcess_messages.save_data"))
        } catch {
            message.error(t('errors.update_data'))
        }
    }

    /**
     * function which change settings
     */
    const changeSettings = (type: SettingsNotificationNames, key: keyof SettingsNotificatonType[SettingsNotificationNames], value: string | number) => {
        setErrors(prev => ({ ...prev, [type]: { ...prev[type], [key]: undefined } }))
        setSettings(prev => ({ ...prev, [type]: { ...prev[type], [key]: value } }))
    }

    return (
        <div>
            <BlockWrapper>
                <div className={styles.block}>
                    {
                        Object.keys(settings).map((item: any) => {
                            return (
                                <div key={item} className={styles.blockOne}>
                                    <div style={{ marginBottom: '24px' }}>
                                        <span>{t(`forms.settings_notifications.${item}`)}</span>
                                    </div>
                                    {
                                        Object.keys(settings[item as SettingsNotificationNames]).map((field) => {
                                            // @ts-ignore
                                            const error = (errors[item as SettingsNotificationNames] && item && errors[item as SettingsNotificationNames][field as keyof SettingsNotificatonType[SettingsNotificationNames]]) ? errors[item as SettingsNotificationNames][field as keyof SettingsNotificatonType[SettingsNotificationNames]] : ''
                                            return (<InputHolder key={field}>
                                                <TextField
                                                    // @ts-ignore
                                                    error={error}
                                                    label={t(`forms.settings_notifications.${field}`)}
                                                    value={settings[item as SettingsNotificationNames][field as keyof SettingsNotificatonType[SettingsNotificationNames]] || ''}
                                                    onChange={(e) =>
                                                        changeSettings(item as SettingsNotificationNames, field as keyof SettingsNotificatonType[SettingsNotificationNames], e)
                                                    }
                                                />
                                            </InputHolder>)
                                        })
                                    }
                                </div>
                            )
                        })
                    }

                </div>

            </BlockWrapper>
            <div className={styles.btn}>
                <Button
                    onClick={onClickSave}
                    label={'Сохранить'}
                    textUp={'capitalize'}
                    typeStyle={ButtonType.SECOND}
                />
            </div>
        </div>
    )
}
export default TabNotifications
