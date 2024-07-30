import { Col, message, Row } from 'antd'
import { FC, useCallback, useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useLocation, useNavigate } from 'react-router-dom'
import { Routes } from '../../../../config'
import { regLatinica } from '../../../../constants'
import { useAppDispatch } from '../../../../hooks/store'
import { StaticScreensService } from '../../../../services/StaticScreensService'
import { setLoading } from '../../../../store/slices/appSlice'
import { MainLayout } from '../../../complexes/MainLayout'
import { EditWrapper } from '../../../simples/EditWrapper'
import { InputHolder } from '../../../simples/InputHolder'
import { TextEditor } from '../../../simples/TextEditor'
import { TextField } from '../../../simples/TextField'

const StaticScreenEdit: FC = () => {
    const { t } = useTranslation()
    const dispatch = useAppDispatch()
    const id = useLocation().search.split('=')[1]
    const style = {}
    const navigation = useNavigate()

    const [alias, setAlias] = useState<string>('')
    const [docName, setDocName] = useState<string>('')
    const [content, setContent] = useState<string>('')
    const [errors, setErrors] = useState({
        alias: '',
        docName: '',
    })

    /**
     * fetch one screen
     */
    const getOneScreenById = useCallback(async () => {
        try {
            dispatch(setLoading(true))
            const response = await StaticScreensService.getStaticScreensById(id)
            setAlias(response.alias)
            setDocName(response.title)
            setContent(response.content)
            dispatch(setLoading(false))
        } catch (error) {
            message.error(t('errors.get_data'))
            dispatch(setLoading(false))
        }
    }, [dispatch, id, t])

    /**
     * save function
     */
    const onHandleSave = useCallback(async () => {
        if (!alias || !docName) {
            message.error(t('errors.save_static_screen_empty_fields'))
            return
        }
        if (alias.match(regLatinica)) {
            setErrors({ ...errors, alias: t('errors.alias_latinica') })
            return
        }
        if (docName.match(regLatinica)) {
            setErrors({ ...errors, docName: t('errors.name_latinica') })
            return
        }

        const sendData = {
            alias,
            title: docName,
            content,
        }
        if (id) {
            try {
                await StaticScreensService.updateStaticScreens(id, sendData)
                message.success(t('suсcess_messages.update_data'))
                navigation(Routes.STATIC_SCREEN)
            } catch (error) {
                message.error(t('errors.update_data'))
            }
        } else {
            try {
                await StaticScreensService.createStaticScreens(sendData)
                message.success(t('suсcess_messages.save_data'))
                navigation(Routes.STATIC_SCREEN)
            } catch (error) {
                message.error(t('errors.save_data'))
            }
        }
    }, [alias, content, docName, id, t])

    /**
     * fetching data if we edit Info screen
     */
    useEffect(() => {
        if (!!id) {
            getOneScreenById().then()
        }
    }, [id])

    const onChangeAlias = useCallback(
        (e: string) => {
            setErrors({ ...errors, alias: '' })
            setAlias(e)
        },
        [errors]
    )

    const onChangeDocName = useCallback(
        (e: string) => {
            setErrors({ ...errors, docName: '' })
            setDocName(e)
        },
        [errors]
    )

    return (
        <MainLayout isLoading={false} title={t('screen_title.static_screen')}>
            <EditWrapper
                onSave={onHandleSave}
                title={t(!id ? 'common.add' : 'common.edit_full')}
            >
                <div>
                    <Row gutter={[16, 16]}>
                        <Col className="gutter-row">
                            <InputHolder>
                                <TextField
                                    label={t('forms.alias_only_lat')}
                                    onChange={onChangeAlias}
                                    value={alias}
                                    error={errors.alias || ''}
                                />
                            </InputHolder>
                        </Col>
                        <Col className="gutter-row">
                            <InputHolder>
                                <TextField
                                    label={t('forms.doc_name')}
                                    onChange={onChangeDocName}
                                    value={docName}
                                    error={errors.docName || ''}
                                />
                            </InputHolder>
                        </Col>
                    </Row>
                    <div style={{ marginTop: '20px' }}>
                        <TextEditor value={content} onChange={setContent} />
                    </div>
                </div>
            </EditWrapper>
        </MainLayout>
    )
}

export default StaticScreenEdit
