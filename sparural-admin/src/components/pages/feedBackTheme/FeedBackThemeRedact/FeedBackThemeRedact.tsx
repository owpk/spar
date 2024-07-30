import { FC, useCallback, useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { MainLayout } from '../../../complexes/MainLayout'
import { EditWrapper } from '../../../simples/EditWrapper'

import styles from './FeedBackThemeRedact.module.scss'
import { useLocation, useNavigate } from 'react-router-dom'
import { TextField } from '../../../simples/TextField'
import { FeedBackThemeService } from '../../../../services/FeedBackThemeService'
import { message } from 'antd'
import { CreateFeedBackThemeType } from '../../../../types'
import { Routes } from '../../../../config'
type Props = {}

const FeedBackThemeRedact: FC<Props> = () => {
    const { t } = useTranslation()
    const id = useLocation().search.split('=')[1]
    const navigation = useNavigate()
    const [loading, setLoading] = useState(false)
    const [theme, setTheme] = useState<string>('')

    const getOneScreenById = useCallback(async () => {
        try {
            setLoading(true)
            const response = await FeedBackThemeService.getFeedBackThemenById(
                Number(id)
            )
            setTheme(response.name)
            setLoading(false)
        } catch (error) {
            message.error(t('errors.get_data'))
            setLoading(false)
        }
    }, [id, t])

    const onHandleSave = useCallback(async () => {
        const sendData: CreateFeedBackThemeType = {
            name: theme,
         
        }

       if(id){
        try {
             await FeedBackThemeService.updateFeedBackThemen(
                Number(id),
                sendData
            )
            message.success(t('suсcess_messages.update_data'))
            navigation(Routes.FEEDBACK_THEME)
        } catch (error: any) {
            message.error(
                t('errors.update_data') + ` (${error.response.data.message})`
            )
        }
       }else{
            try {
                await FeedBackThemeService.createFeedBackThemen(
                    sendData
                )
                message.success(t('suсcess_messages.save_data'))
                navigation(Routes.FEEDBACK_THEME)
            } catch (error: any) {
                message.error(
                    t('errors.update_data') + ` (${error.response.data.message})`
                )
            }
       }
    }, [theme, id, t, navigation])

    /**
     * fetching data if we edit Info screen
     */
    useEffect(() => {
        if (!!id) {
            getOneScreenById().then()
        }
    }, [id])

    return (
        <MainLayout title={t('screen_title.feedback')} isLoading={false}>
            <EditWrapper
                title={t(!id ? 'common.add' : 'common.edit_full')}
                onSave={onHandleSave}
            >
                <>
                    <div className={styles.block}>
                        <div className={styles.blockInput}>
                            <TextField
                                label={t('forms.theme_message')}
                                value={theme}
                                onChange={setTheme}
                            />
                        </div>
                    </div>
                </>
            </EditWrapper>
        </MainLayout>
    )
}
export default FeedBackThemeRedact
