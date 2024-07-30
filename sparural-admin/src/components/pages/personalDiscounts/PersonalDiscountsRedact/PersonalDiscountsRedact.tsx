import React, { useCallback, useEffect, useState } from 'react'
import styles from './PersonalDiscountsRedact.module.scss'
import { MainLayout } from '../../../complexes/MainLayout'
import { EditWrapper } from '../../../simples/EditWrapper'
import { InputHolder } from '../../../simples/InputHolder'
import { TextField } from '../../../simples/TextField'
import { t } from 'i18next'
import { Col, Row, message } from 'antd'
import { useLocation, useNavigate } from 'react-router-dom'
import {
    CreatePersonalDiscountType,
    PersonalDiscountsSerivce,
} from '../../../../services/PersonalDiscountsSerivce'
import { Routes } from '../../../../config'
import { Checkbox } from '../../../simples/Checkbox'

type Props = {}

const PersonalDiscountsRedact = (props: Props) => {
    const id = useLocation().search.split('=')[1]
    const navigate = useNavigate()

    const [loading, setLoading] = useState(false)

    const [discountIdentificator, setDiscountIdentificator] = useState('')
    const [counterIdentificator, setCounterIdentificator] = useState('')
    const [maxValue, setMaxValue] = useState('')
    const [isPublic, setIsPublic] = useState(false)

    const [currentId, setCurrentId] = useState<number>(id ? +id : 0)

    const getPersonalDiscountsById = useCallback(async () => {
        try {
            const response =
                await PersonalDiscountsSerivce.getPersonalDiscountsById(
                    Number(id)
                )

            setDiscountIdentificator(response?.loymaxOfferId)
            setCounterIdentificator(response?.loymaxCounterId)
            setMaxValue(String(response?.maxValue))
            setIsPublic(response.isPublic)
            setLoading(false)
        } catch (error) {
            message.error(t('errors.get_data'))
            setLoading(false)
        }
    }, [id, t])

    const onHandleSave = async () => {
        const data: CreatePersonalDiscountType = {
            isPublic: isPublic,
            loymaxCounterId: counterIdentificator,
            loymaxOfferId: discountIdentificator,
            maxValue: Number(maxValue),
        }
        if (currentId) {
            try {
                const redact =
                    await PersonalDiscountsSerivce.updatePersonalDiscount(
                        currentId,
                        data
                    )
            } catch (error) {
                message.error(t('errors.save_data'))
                return
            }
        } else {
            try {
                const save =
                    await PersonalDiscountsSerivce.createPersonalDiscount(data)
            } catch (error) {
                message.error(t('errors.save_data'))
                return
            }
        }
        navigate(Routes.PERSONAL_DISCOUNTS)
        setCounterIdentificator('')
        setCurrentId(0)
        setDiscountIdentificator('')
        setIsPublic(false)
        setMaxValue('')
    }

    useEffect(() => {
        if (!!id) {
            getPersonalDiscountsById().then()
        }
    }, [id])

    return (
        <MainLayout title={t('screen_title.personal_discounts')} isLoading={loading}>
            <EditWrapper
                title={t(!id ? 'common.add' : 'common.edit_full')}
                onSave={onHandleSave}
            >
                <Row gutter={[16, 16]}>
                    <Col>
                        <InputHolder>
                            <TextField
                                label={t('forms.discount_identificator_loymax')}
                                value={discountIdentificator}
                                onChange={setDiscountIdentificator}
                            />
                        </InputHolder>
                        <InputHolder>
                            <TextField
                                label={t('forms.counter_identificator_loymax')}
                                value={counterIdentificator}
                                onChange={setCounterIdentificator}
                            />
                        </InputHolder>
                    </Col>
                    <Col>
                        <InputHolder classes={styles.inputHolderBeforeCheck}>
                            <TextField
                                label={t('forms.max_value')}
                                value={maxValue}
                                onChange={setMaxValue}
                            />
                        </InputHolder>
                        <InputHolder>
                            <Checkbox
                                value={Number(isPublic)}
                                onClick={() => setIsPublic(!isPublic)}
                                isChecked={isPublic}
                                label="Включить"
                                labelPosition="right"
                            />
                        </InputHolder>
                    </Col>
                </Row>
            </EditWrapper>
        </MainLayout>
    )
}

export default PersonalDiscountsRedact
