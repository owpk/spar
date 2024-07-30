import React, { useCallback, useEffect, useRef, useState } from 'react'
import styles from './PersonalDiscounts.module.scss'
import { MainLayout } from '../../../complexes/MainLayout'
import { MainTable } from '../../../complexes/MainTable'
import { DeleteModal } from '../../../complexes/DeleteModal'
import { t } from 'i18next'
import { Celltype, ColumnType } from '../../../complexes/MainTable/TableBody'
import { message } from 'antd'
import { PersonalDiscountsSerivce } from '../../../../services/PersonalDiscountsSerivce'
import { PersonalDiscountsType } from '../../../../types'
import { useNavigate } from 'react-router-dom'
import { Routes } from '../../../../config'
import produce from 'immer'

type Props = {}

type TablePersonalDiscountsData = {
    id: number
    isPublic: boolean
    loymaxCounterId: string
    loymaxOfferId: string
    maxValue: number
    offerId: number
}

const PersonalDiscounts = ({}: Props) => {
    const navigate = useNavigate()
    const [list, setList] = useState<Array<TablePersonalDiscountsData>>([])

    const [loading, setLoading] = useState(false)
    const [del, setDel] = useState<number>(0)
    const offset = useRef(0)

    const load = async () => {
        setLoading(true)
        offset.current = list.length

        try {
            const result = await PersonalDiscountsSerivce.getPersonalDiscounts({
                offset: offset.current,
            })
            const rows = result.map((item: PersonalDiscountsType) => {
                return {
                    id: item.id,
                    isPublic: item?.isPublic,
                    loymaxCounterId: item?.loymaxCounterId,
                    loymaxOfferId: item?.loymaxOfferId,
                    maxValue: item?.maxValue,
                    offerId: item?.offerId,
                }
            })
            setList([...list, ...rows])
        } catch (error) {
            message.error(t('errors.get_data'))
        }
        setLoading(false)
    }

    const onHandlePublic = useCallback(async (id: number, pub: boolean) => {
        try {
            const find = list.find(item => item.id === id)

            if (!find) {
                throw new Error('Personal discount not found')
            }

            const response =
                await PersonalDiscountsSerivce.updatePersonalDiscount(id, {
                    id: find.id,
                    loymaxOfferId: find.loymaxOfferId,
                    loymaxCounterId: find.loymaxCounterId,
                    maxValue: find.maxValue,
                    isPublic: pub,
                })
            setList(
                produce((draft) => {
                    const find = draft.find((i) => i.id === id)
                    if (find) {
                        find.isPublic = response.isPublic
                    }
                })
            )
        } catch (error) {}
    }, [list])

    useEffect(() => {
        load().then()
    }, [])

    const columns: Array<ColumnType> = [
        {
            key: 'loymaxOfferId',
            type: Celltype.STRING,
            title: t('forms.discount_identificator'),
            width: 0.5,
        },
        {
            key: 'loymaxCounterId',
            type: Celltype.STRING,
            title: t('forms.counter_identificator'),
            width: 2,
        },
        {
            key: 'maxValue',
            type: Celltype.STRING,
            title: t('forms.max_value'),
            width: 3,
        },
        {
            key: 'preview',
            title: t('forms.on_off'),
            type: Celltype.BUTTON,
            width: 1.5,
        },
    ]

    const goToEdit = (id: number) => {
        navigate(`${Routes.PERSONAL_DISCOUNTS_EDIT}?id=${id}`)
    }

    /**
     * navigate to create screen
     */
    const onCreateNew = () => {
        navigate(Routes.PERSONAL_DISCOUNTS_CREATE)
    }

    const onDelete = useCallback(async () => {
        try {
            const response =
                await PersonalDiscountsSerivce.deletePersonalDiscount(del)

            if (response) {
                message.success(t('suсcess_messages.delete_data'))
                setList((prev) => prev.filter((i) => i.id !== del))
                setDel(0)
            } else {
                message.error(t('errors.delete_data'))
            }
        } catch (error) {
            message.error(t('errors.delete_data'))
        }
    }, [del])

    return (
        <MainLayout
            title={t('screen_title.personal_discounts')}
            onAdd={onCreateNew}
            isLoading={loading}
            onEndReached={load}
        >
            <>
                <MainTable
                    columns={columns}
                    onEdit={goToEdit}
                    data={list}
                    onDelete={setDel}
                    onBtnClick={onHandlePublic}
                    isPublic={true}
                    onLabel="Включить"
                    offLabel="Выключить"
                />
                <DeleteModal
                    onSubmit={onDelete}
                    onCancel={() => setDel(0)}
                    visible={!!del}
                />
            </>
        </MainLayout>
    )
}

export default PersonalDiscounts
