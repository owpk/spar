import React, { FC, useCallback, useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useNavigate } from 'react-router-dom'
import { Routes } from '../../../config'
import { regMail, regOnlyNumbers } from '../../../constants'
import { AuthService } from '../../../services/AuthService'
import { Notifiertype } from '../../../types'
import { showTimer } from '../../../utils/helpers'
import { AuthLayout } from '../../complexes/AuthLayout'
import { Button } from '../../simples/Button'
import { Selector } from '../../simples/Selector'
import { SelectOption } from '../../simples/Selector/OptionItem'
import { TextField } from '../../simples/TextField'
import styles from './RepairPass.module.scss'

/**
 * @returns - page send code
 */

const RepairPass: FC = () => {
    const { t } = useTranslation()
    const navigation = useNavigate()
    const modaRecoveryOption: SelectOption[] = [
        { value: Notifiertype.MAIL, label: t('authorisation.email') },
        { value: Notifiertype.PHONE, label: t('authorisation.phone_number') },
    ]

    const [notifier, setNotifier] = useState<SelectOption>({
        value: Notifiertype.MAIL,
        label: t('authorisation.email'),
    })
    const [notifierIdentity, setNotifierIdentity] = useState<string>('')
    const [error, setError] = useState<string>('')

    const notifierLabel = t(
        notifier.value === Notifiertype.MAIL
            ? 'authorisation.enter_email'
            : 'authorisation.enter_phone'
    )

    const validate = (data: string, regExp: RegExp) => {
        let valid = !!data.match(regExp)
        return valid
    }

    const recoveryPassword = async () => {
        const rezult = await AuthService.RecoveryPassword({
            notifier: notifier.value,
            notifierIdentity: notifierIdentity,
        })
    }

    const onSubmit = useCallback(async () => {
        let val = true
        if (!!!notifierIdentity) {
            setError(t('errors.required_field'))
            return
        }
        if (notifier.value === Notifiertype.MAIL) {
            val = validate(notifierIdentity, regMail)
        }
        if (notifier.value === Notifiertype.PHONE) {
            val = validate(
                notifierIdentity.replace(/[^0-9]/g, ''),
                regOnlyNumbers
            )
        }
        if (!val) {
            if (notifier.value === Notifiertype.MAIL) {
                setError(t('errors.wrong_mail_format'))
            }
            if (notifier.value === Notifiertype.PHONE) {
                setError(t('errors.only_numbers'))
            }
            return
        } else {
            setError('')
            recoveryPassword().then()
            navigation(Routes.CREATE_PASS)
        }
    }, [navigation, notifier.value, notifierIdentity, t])

    const onChangeValue = (val: string) => {
        setError('')
        setNotifierIdentity(val)
    }

    return (
        <AuthLayout title={'Восстановление пароля'}>
            <>
                <div className={styles.inputHolder}>
                    <Selector
                        label={'Email / Номер телефона'}
                        options={modaRecoveryOption}
                        onChange={setNotifier}
                        placeholder={t('authorisation.choose_method')}
                        value={[notifier]}
                        classes={{
                            label: styles.label,
                        }}
                    />
                </div>
                <div className={styles.inputHolder}>
                    <TextField
                        classes={{
                            label: styles.label,
                        }}
                        label={notifierLabel}
                        placeholder={''}
                        onChange={onChangeValue}
                        value={notifierIdentity}
                        notification={notifier.value}
                        error={error}
                        mask="mask"
                    />
                </div>
                <Button
                    onClick={onSubmit}
                    label={t('common.go_forward')}
                    type={'submit'}
                    textUp="capitalize"
                />
            </>
        </AuthLayout>
    )
}

export default RepairPass
