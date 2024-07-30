import { message } from 'antd'
import React, { FC, useCallback, useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { SettingsService } from '../../../../../services/SettingsPagesService'
import { SettingsKodType } from '../../../../../types'
import { BlockWrapper } from '../../../../simples/BlockWrapper'
import { Button } from '../../../../simples/Button'
import { ButtonType } from '../../../../simples/Button/Button'
import { TextField } from '../../../../simples/TextField'
import styles from './TabKod.module.scss'

type ErrorType = {
    alertTime?: string
    alertAttempts?: string
    alertHour?: string
    alertDay?: string
}

type Props = {
    codes: SettingsKodType
}

const TabKod: FC<Props> = ({ codes }) => {
    const { t } = useTranslation()
    const [valueTime, setValueTime] = useState<string | number>(
        codes?.lifetime || ''
    )
    const [valueAttempts, setValueAttempts] = useState<string | number>(
        codes?.maxUnsuccessfulAttempts || ''
    )
    const [valueHour, setValueHour] = useState<string | number>(
        codes?.maxInHourCount || ''
    )
    const [valueDay, setValueDay] = useState<string | number>(
        codes?.maxDaylyCount || ''
    )
    const [errors, setErrors] = useState<ErrorType>({})

    const onClickSave = async () => {
        const data: SettingsKodType = {
            lifetime: valueTime ? valueTime : '',
            maxUnsuccessfulAttempts: valueAttempts ? valueAttempts : '',
            maxInHourCount: valueHour ? valueHour : '',
            maxDaylyCount: valueDay ? valueDay : '',
        }

        if (valueTime === '' || 0) {
            setErrors({ ...errors, alertTime: t('errors.required_field') })
            return
        }
        if (valueAttempts === '' || 0) {
            setErrors({ ...errors, alertAttempts: t('errors.required_field') })
            return
        }
        if (valueHour === '' || 0) {
            setErrors({ ...errors, alertHour: t('errors.required_field') })
            return
        }
        if (valueDay === '' || 0) {
            setErrors({ ...errors, alertDay: t('errors.required_field') })
            return
        }
        try {
            await SettingsService.updateSettingsCodes(data)
            message.success(t('suсcess_messages.save_data'))
        } catch (error: any) {
            message.error(
                t('errors.update_data') + ` (${error.response.data.message})`
            )
        }
    }
    useEffect(() => {
        setValueTime(codes.lifetime)
        setValueAttempts(codes.maxUnsuccessfulAttempts)
        setValueHour(codes.maxInHourCount)
        setValueDay(codes.maxDaylyCount)
    }, [codes])

    const onChangeTime = useCallback(
        (e: string) => {
            setErrors({ ...errors, alertTime: '' })
            setValueTime(e)
        },
        [errors]
    )
    const onChangeAttempts = useCallback(
        (e: string) => {
            setErrors({ ...errors, alertAttempts: '' })
            setValueAttempts(e)
        },
        [errors]
    )
    const onChangeHour = useCallback(
        (e: string) => {
            setErrors({ ...errors, alertHour: '' })
            setValueHour(e)
        },
        [errors]
    )
    const onChangeDay = useCallback(
        (e: string) => {
            setErrors({ ...errors, alertDay: '' })
            setValueDay(e)
        },
        [errors]
    )

    return (
        <div>
            <BlockWrapper>
                <div className={styles.block}>
                    <div className={styles.item}>
                        <div className={styles.itemBlock}>
                            <TextField
                                value={valueTime || 0}
                                onChange={onChangeTime}
                                label="Время жизни кода"
                                error={errors?.alertTime || ''}
                            />
                        </div>

                        <TextField
                            value={valueAttempts || ''}
                            onChange={onChangeAttempts}
                            label="Максимальное кол-во неудачных попыток"
                            error={errors?.alertAttempts || ''}
                        />
                    </div>

                    <div className={styles.itemTwo}>
                        <div className={styles.itemBlock}>
                            <TextField
                                value={valueHour || ''}
                                onChange={onChangeHour}
                                label="Максимальное кол-во запросов кода в час"
                                error={errors?.alertHour || ''}
                            />
                        </div>
                        <TextField
                            value={valueDay || ''}
                            onChange={onChangeDay}
                            label="Максимальное кол-во запросов кода в сутки"
                            error={errors?.alertDay || ''}
                        />
                    </div>
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
export default TabKod
