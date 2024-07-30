import { Col, message, Row } from 'antd'
import React, { FC, useCallback, useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useLocation, useNavigate } from 'react-router-dom'
import { Routes } from '../../../config'
import { UserAtributes } from '../../../types'
import { MainLayout } from '../../complexes/MainLayout'
import { EditWrapper } from '../../simples/EditWrapper'
import { TextField } from '../../simples/TextField'
import { UsersService } from '../../../services/UsersService'

const UserAttributesRedact: FC = () => {
    const { t } = useTranslation()
    const attributeId = useLocation().search.split('=')[1]

    const navigate = useNavigate()

    const style = {}

    const [item, setItem] = useState<UserAtributes>()
    const [attributeName, setAttributeName] = useState<string>('')
    const [name, setName] = useState<string>('')

    const [loading, setLoading] = useState(false)

    const load = async (id: string) => {
        setLoading(true)
        try {
            const result = await UsersService.getUserAtribute(+id)

            setItem(result)
            setAttributeName(result.attributeName)
            setName(result.name)
        } catch (error) {
            message.error(t('errors.get_data'))
        }
        setLoading(false)
    }
    useEffect(() => {
        if (attributeId !== undefined) {
            load(attributeId).then()
        }
    }, [attributeId])

    const onHandleSave = useCallback(async () => {
        const data = {
            name,
            attributeName,
        }
        if (attributeId) {
            try {
                const redact = await UsersService.updateUserAttribute(
                    +attributeId,
                    data
                )
                message.success(t('suсcess_messages.save_data'))
                navigate(Routes.USERS_ATRIBUTES)
            } catch (error) {
                message.error(t('errors.save_data'))
            }
        } else {
            try {
                const save = await UsersService.createUserAttribute(data)
                message.success(t('suсcess_messages.save_data'))
                navigate(Routes.USERS_ATRIBUTES)
            } catch (error) {
                message.error(t('errors.save_data'))
            }
        }
    }, [attributeId, navigate, t, name, attributeName])

    return (
        <MainLayout isLoading={loading} title={t('screen_title.users_attributes')}>
            <EditWrapper
                onSave={onHandleSave}
                title={t(!attributeId ? 'common.add' : 'common.edit_full')}
            >
                <Row
                    gutter={{ xs: 8, sm: 16, md: 24, lg: 32 }}
                    style={{ alignItems: 'end' }}
                >
                    <Col className="gutter-row" span={6}>
                        <div style={style}>
                            <TextField
                                label={t('forms.alias_only_lat')}
                                onChange={setAttributeName}
                                value={attributeName}
                            />
                        </div>
                    </Col>
                    <Col className="gutter-row" span={6}>
                        <div style={style}>
                            <div style={style}>
                                <TextField
                                    label={t('forms.name')}
                                    onChange={setName}
                                    value={name}
                                />
                            </div>
                        </div>
                    </Col>
                </Row>
            </EditWrapper>
        </MainLayout>
    )
}

export default UserAttributesRedact
