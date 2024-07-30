import { message } from 'antd'
import React, { FC, useCallback, useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useNavigate } from 'react-router-dom'
import { Routes } from '../../../../config'
import { ShopsService } from '../../../../services/ShopsService'
import { CreateShopsType, ShopsType } from '../../../../types'
import { DeleteModal } from '../../../complexes/DeleteModal'
import { MainLayout } from '../../../complexes/MainLayout'
import { MainTable } from '../../../complexes/MainTable'
import { Celltype, ColumnType } from '../../../complexes/MainTable/TableBody'
import styles from './ShopsScreen.module.scss'
import produce from 'immer'

type Props = {}

type TableShopType = {
    id: number
    format: string
    name: string
    city: string
    address: string
    time_work: string
    button?: boolean
}

const ShopsScreen: FC<Props> = () => {
    const { t } = useTranslation()
    const navigate = useNavigate()
    const [del, setDel] = useState<number>(0)
    const [loading, setLoading] = useState(false)
    const [list, setList] = useState<Array<TableShopType>>([])
    const offset = useRef(0)
    const has = useRef(true)
    const shopsRef = useRef<Array<ShopsType>>([])

    const columns: Array<ColumnType> = [
        {
            key: 'format',
            title: t('common.format'),
            width: 2,
        },
        {
            key: 'name',
            title: t('common.name_shop'),
            width: 2,
        },
        {
            key: 'city',
            title: t('common.city'),
            width: 2,
        },
        {
            key: 'address',
            title: t('common.adress'),
            width: 2,
        },
        {
            key: 'time_work',
            title: t('common.time_work'),
            width: 2,
        },
        {
            key: 'button',
            title: t('common.button'),
            width: 2,
            type: Celltype.BUTTON,
        },
    ]


    const goToRedact = (id: number) => {
        navigate(`${Routes.SHOPS_EDIT}?id=${id}`)
    }
    const goToCreate = () => {
        navigate(Routes.SHOPS_CREATE)
    }

    const load = async () => {
        if (!has.current || loading) {
            return
        }

        setLoading(true)
        try {
            const result = await ShopsService.getShops({
                offset: offset.current,
            })
            if (!result.length) {
                has.current = false
                setLoading(false)
                return
            }

            const rows = result.map((item: ShopsType) => {
                const address = item.address.split(',').splice(0, item.address.split(',').length - 1).join(', ') || ''
                return {
                    id: item.id,
                    format: item.format.name || '',
                    name: item.title || '',
                    city: item.address.split(',')[0] || '',
                    address: item.address.split(',').slice(1).join(', '),
                    time_work: `${t("forms.from")} ${item.workingHoursFrom} ${t("forms.to")} ${item.workingHoursTo}` || '',
                    button: item.isPublic
                }
            })

            offset.current = offset.current + result.length

            setList([...list, ...rows])
        } catch (error) {
            message.error(t('errors.get_data'))
        }
        setLoading(false)
    }
    useEffect(() => {
        load().then()
    }, [])
    const onDeleteScreen = useCallback(async () => {
        try {
            const response = await ShopsService.deleteShops(del)

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
    }, [del, t])

    const onPublish = useCallback(
        async (id: number, Publick: boolean) => {
            try {
                // const updateShops = shopsRef.current.find(
                //     (i) => i.id === id
                // ) as CreateShopsType
                // if (updateShops) {
                    const response = await ShopsService.updateShops(
                        id,
                        {
                            isPublic: Publick,
                        }
                    )
                    setList(
                        produce((draft) => {
                            const find = draft.find((i) => i.id === id)
                            if (find) {
                                find.button = !find.button
                            }
                        })
                    )
                // }
            } catch (error) {
                message.warning(t('errors.update_data'))
            }
        },
        [t, shopsRef.current]
    )

    const handleEndReached = async () => {
        await load()
    }
    return (
        <MainLayout
            title={t('screen_title.shops')}
            onAdd={goToCreate}
            isLoading={loading}
            onEndReached={handleEndReached}
        >
            <>
                <MainTable
                    columns={columns}
                    data={list}
                    onDelete={setDel}
                    onEdit={(id) => goToRedact(id)}
                    onBtnClick={onPublish}
                />
                <DeleteModal
                    onSubmit={onDeleteScreen}
                    onCancel={() => setDel(0)}
                    visible={!!del}
                />
            </>
        </MainLayout>
    )
}
export default ShopsScreen
