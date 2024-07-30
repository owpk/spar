import { message } from 'antd'
import React, { FC, useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { SettingsService } from '../../../../../services/SettingsPagesService'
import { SettingsGeneralType } from '../../../../../types'
import { BlockWrapper } from '../../../../simples/BlockWrapper'
import { Button } from '../../../../simples/Button'
import { ButtonType } from '../../../../simples/Button/Button'
import { TextField } from '../../../../simples/TextField'
import styles from './TabGeneral.module.scss'
type Props = {
    data: SettingsGeneralType
}

const TabGeneral: FC<Props> = ({ data }) => {
    const { t } = useTranslation()
    const [value, setValue] = useState<SettingsGeneralType>(data)

    useEffect(() => {
        setValue(data)
    },[data])

    const onClickSave = async () => {
        const data: SettingsGeneralType = {
            timezone: value.timezone,
            notificationsFrequency: value.notificationsFrequency,
        }
        try {
            await SettingsService.updateSettingsGeneral(data)
            message.success(t("suсcess_messages.save_data"))
        } catch {
            message.error(t('errors.update_data'))
        }
    }

    /**
     * Changing frequency
     */

    const onChangeFrenquency = (e: string) => {
        setValue({...value, notificationsFrequency: +e})
    }

    return (
        <div>
            <BlockWrapper>
                <div style={{ width: '50%', paddingBottom: 20 }}>
                    <TextField
                        label={'Частота'}
                        value={value.notificationsFrequency || 0}
                        onChange={onChangeFrenquency}
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
export default TabGeneral
