import { Col, message, Row } from 'antd'
import React, { FC, useCallback, useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useLocation, useNavigate } from 'react-router-dom'
import { Routes } from '../../../config'
import { Counters, UserAtributes } from '../../../types'
import { MainLayout } from '../../complexes/MainLayout'
import { EditWrapper } from '../../simples/EditWrapper'
import { TextField } from '../../simples/TextField'
import { UsersService } from '../../../services/UsersService'
import { GroupUsersService } from '../../../services/GroupUsersService'

const UserAttributesRedact: FC = () => {
    const { t } = useTranslation()
    const counterId = useLocation().search.split('=')[1]

    const navigate = useNavigate()

    const style = {}

    const [item, setItem] = useState<Counters>()
    const [loymaxId, setLoymaxId] = useState<string>('')
    const [name, setName] = useState<string>('')

    const [loading, setLoading] = useState(false)

    const load = async (id: string) => {
        setLoading(true)
        try {
            const result = await GroupUsersService.getOneCounterById(+id)

            setItem(result)
            setLoymaxId(result.loymaxId)
            setName(result.name)
        } catch (error) {
            message.error(t('errors.get_data'))
        }
        setLoading(false)
    }
    useEffect(() => {
        if (counterId !== undefined) {
            load(counterId).then()
        }
    }, [counterId])

    const onHandleSave = useCallback(async () => {
        const data = {
            name,
            loymaxId,
        }
        if (counterId) {
            try {
                const redact = await GroupUsersService.updateCounter(
                    +counterId,
                    data
                )
                message.success(t('suсcess_messages.save_data'))
                navigate(Routes.COUNTERS)
            } catch (error) {
                message.error(t('errors.save_data'))
            }
        } else {
            try {
                const save = await GroupUsersService.createCounter(data)
                message.success(t('suсcess_messages.save_data'))
                navigate(Routes.COUNTERS)
            } catch (error) {
                message.error(t('errors.save_data'))
            }
        }
    }, [counterId, navigate, t, name, loymaxId])

    return (
        <MainLayout isLoading={loading} title={t('screen_title.counters')}>
            <EditWrapper
                onSave={onHandleSave}
                title={t(!counterId ? 'common.add' : 'common.edit_full')}
            >
                <Row
                    gutter={{ xs: 8, sm: 16, md: 24, lg: 32 }}
                    style={{ alignItems: 'end' }}
                >
                    <Col className="gutter-row" span={6}>
                        <div style={style}>
                            <TextField
                                label={t('forms.counter_name')}
                                onChange={setName}
                                value={name}
                            />
                        </div>
                    </Col>
                    <Col className="gutter-row" span={6}>
                        <div style={style}>
                            <div style={style}>
                                <TextField
                                    label={t('forms.loymax_id')}
                                    onChange={setLoymaxId}
                                    value={loymaxId}
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
