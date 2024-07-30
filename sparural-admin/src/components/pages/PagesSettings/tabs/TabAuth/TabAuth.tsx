import { message } from 'antd'
import React, { FC, useCallback, useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { SettingsService } from '../../../../../services/SettingsPagesService'
import { SettingsAuthType } from '../../../../../types'
import { BlockWrapper } from '../../../../simples/BlockWrapper'
import { Button } from '../../../../simples/Button'
import { ButtonType } from '../../../../simples/Button/Button'
import { TextField } from '../../../../simples/TextField'
import styles from './TabAuth.module.scss'

type ErrorType = {
    alert?: string
}

type Props = {
    data: SettingsAuthType
}

const TabAuth: FC<Props> = ({ data }) => {
    const { t } = useTranslation()
    const [value, setValue] = useState<string>('')
    const [errors, setErrors] = useState<ErrorType>({})

    useEffect(() => {
        setValue(data?.secretKey || '')
    }, [data])

    const onClickSave = async () => {
        if (value === '') {
            setErrors({ ...errors, alert: t('errors.required_field') })
            return
        }
        const data: SettingsAuthType = {
            secretKey: value,
        }
        try {
            const response = await SettingsService.updateSettingsAuth(data)
            setValue(response.secretKey)
            message.success(t('suсcess_messages.save_data'))
        } catch {
            message.error(t('errors.update_data'))
        }
    }

    const onChangeValue = useCallback(
        (e: string) => {
            setErrors({ ...errors, alert: '' })
            setValue(e)
        },
        [errors]
    )

    return (
        <div>
            <BlockWrapper>
                <div style={{ width: '50%', paddingBottom: 20 }}>
                    <TextField
                        label={'Ключ подписи acces-токена'}
                        value={value}
                        onChange={onChangeValue}
                        error={errors?.alert || ''}
                    />
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
export default TabAuth
