import { message } from 'antd'
import produce from 'immer'
import { FC, useCallback, useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { SettingsService } from '../../../../../services/SettingsPagesService'
import { SettingsSocialType } from '../../../../../types'
import { BlockWrapper } from '../../../../simples/BlockWrapper'
import { Button } from '../../../../simples/Button'
import { ButtonType } from '../../../../simples/Button/Button'
import { TextField } from '../../../../simples/TextField'

import styles from './TabNetwork.module.scss'

type Props = {
    data: SettingsSocialType[]
}

const TabNetwork: FC<Props> = ({ data }) => {
    const { t } = useTranslation()
    const social = data.sort((a, b) => a.id - b.id)

    const [currentSocials, setCurrentSocials] =
        useState<SettingsSocialType[]>(social)

    useEffect(() => {
        setCurrentSocials(data)
    }, [data])

    const onClickSave = async () => {
        for (let social of currentSocials) {
            const find = data.find((d) => d.id === social.id)
            if (
                find &&
                (find.appId !== social.appId ||
                    find.appSecret !== social.appSecret)
            ) {
                try {
                    await SettingsService.updateSettingsSocial(social.id, {
                        appId: social.appId,
                        appSecret: social.appSecret,
                    })
                    message.success(t('suсcess_messages.save_data'))
                } catch (error) {
                    message.error(t('errors.update_data') + social.name)
                }
            }
        }
    }

    const onChangeAppId = useCallback((id: number, value) => {
        setCurrentSocials(
            produce((draft) => {
                const find = draft.find((social) => social.id === id)

                if (find) {
                    find.appId = value
                }
            })
        )
    }, [])

    const onChangeAppSecret = useCallback((id: number, value) => {
        setCurrentSocials(
            produce((draft) => {
                const find = draft.find((social) => social.id === id)

                if (find) {
                    find.appSecret = value
                }
            })
        )
    }, [])

    return (
        <div>
            <BlockWrapper>
                <>
                    <div className={styles.blockOne}>
                        {currentSocials.map((social) => {
                            return (
                                <div key={social.id} className={styles.oneSocial}>
                                    <div style={{ marginBottom: '24px' }}>
                                        <span className={styles.socialName}>
                                            {social.name}
                                        </span>
                                    </div>

                                    <div style={{ marginBottom: '16px' }}>
                                        <TextField
                                            label={'appld'}
                                            value={social.appId}
                                            onChange={(e) =>
                                                onChangeAppId(social.id, e)
                                            }
                                        />
                                    </div>

                                    <TextField
                                        label={'appSecret'}
                                        value={social.appSecret}
                                        onChange={(e) =>
                                            onChangeAppSecret(social.id, e)
                                        }
                                    />
                                </div>
                            )
                        })}
                    </div>
                </>
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
export default TabNetwork
