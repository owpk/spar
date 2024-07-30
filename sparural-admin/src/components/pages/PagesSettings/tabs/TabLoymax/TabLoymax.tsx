import { message } from 'antd'
import { t } from 'i18next'
import React, { FC, useCallback, useEffect, useState } from 'react'
import { SettingsService } from '../../../../../services/SettingsPagesService'
import { SettingsLoymaxType } from '../../../../../types'
import { BlockWrapper } from '../../../../simples/BlockWrapper'
import { Button } from '../../../../simples/Button'
import { ButtonType } from '../../../../simples/Button/Button'
import { TextField } from '../../../../simples/TextField'
import styles from './TabLoymax.module.scss'

type ErrorType = {
    alertHost?: string
    alertUser?: string
    alertPass?: string
}

type Props = {
    data: SettingsLoymaxType
}

const TabLoymax: FC<Props> = ({ data }) => {
    const [host, setHost] = useState<string>('')
    const [username, setUsername] = useState<string>('')
    const [password, setPassword] = useState<string>('')
    const [errors, setErrors] = useState<ErrorType>({})

    useEffect(() => {
        setHost(data.host)
        setUsername(data.username)
        setPassword(data.password)
    }, [data])

    const onSave = useCallback(async () => {
        if (host === '') {
            setErrors({ ...errors, alertHost: t('errors.required_field') })
            return
        }
        if (username === '') {
            setErrors({ ...errors, alertUser: t('errors.required_field') })
            return
        }
        if (password === '') {
            setErrors({ ...errors, alertPass: t('errors.required_field') })
            return
        }
        try {
            await SettingsService.updateSettingsLoymax({
                host,
                username,
                password,
            })
            message.success(t('suсcess_messages.save_data'))
        } catch (error) {}
    }, [host, password, username])
    const onChangeHost = useCallback(
        (e: string) => {
            setErrors({ ...errors, alertHost: '' })
            setHost(e)
        },
        [errors]
    )
    const onChangeUser = useCallback(
        (e: string) => {
            setErrors({ ...errors, alertUser: '' })
            setUsername(e)
        },
        [errors]
    )
    const onChangePassword = useCallback(
        (e: string) => {
            setErrors({ ...errors, alertPass: '' })
            setPassword(e)
        },
        [errors]
    )

    return (
        <div>
            <BlockWrapper>
                <>
                    <div className={styles.block}>
                        <div className={styles.blockOne}>
                            <div style={{ marginBottom: '12px' }}>
                                <TextField
                                    label={'Адрес сервиса'}
                                    value={host}
                                    onChange={onChangeHost}
                                    error={errors?.alertHost || ''}
                                />
                            </div>
                            <TextField
                                label={'Логин'}
                                value={username}
                                onChange={onChangeUser}
                                error={errors?.alertUser || ''}
                            />
                        </div>
                        <div className={styles.blockTwo}>
                            <TextField
                                label={'Пароль'}
                                value={password}
                                onChange={onChangePassword}
                                error={errors?.alertPass || ''}
                            />
                        </div>
                    </div>
                </>
            </BlockWrapper>
            <div className={styles.btn}>
                <Button
                    onClick={onSave}
                    label={t('common.save')}
                    textUp={'capitalize'}
                    typeStyle={ButtonType.SECOND}
                />
            </div>
        </div>
    )
}
export default TabLoymax
