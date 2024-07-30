import { message } from 'antd'
import React, { FC, useCallback, useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useNavigate } from 'react-router-dom'
import { Routes } from '../../../../config'
import { PersonalProductsService } from '../../../../services/PersonalProductsService'
import { PersonalProductsType } from '../../../../types'
import { DeleteModal } from '../../../complexes/DeleteModal'
import { MainLayout } from '../../../complexes/MainLayout'
import { MainTable } from '../../../complexes/MainTable'
import { Celltype, ColumnType } from '../../../complexes/MainTable/TableBody'

type Props = {}

type TableGoodsType = {
    id: number
    itendificatoin_products: string
    name: string
    description: string
    preview: string
    previzNeiiew: string
}

const PersonalProducts: FC<Props> = () => {
    const { t } = useTranslation()
    const navigate = useNavigate()
    const [del, setDel] = useState<number>(0)
    const [loading, setLoading] = useState(false)
    const [search, setSearch] = useState<string>('')
    const offset = useRef(0)
    const [list, setList] = useState<Array<TableGoodsType>>([])

    const columns: Array<ColumnType> = [
        {
            key: 'itendificatoin_products',
            title: t('forms.identification_products'),
            width: 2,
        },
        {
            key: 'name',
            title: t('forms.name'),
            width: 2,
        },
        {
            key: 'description',
            title: t('forms.description'),
            width: 3,
        },
        {
            key: 'preview',
            title: t('forms.preview'),
            type: Celltype.IMAGE,
            width: 1.5,
        },
        {
            key: 'previzNeiiew',
            title: t('forms.iz-nei'),
            type: Celltype.IMAGE,
            width: 1.5,
        },
    ]

    const load = async () => {
        setLoading(true)
        try {
            const result = await PersonalProductsService.getPersonalProducts({
                offset: offset.current,
                search: search,
            })

            offset.current = offset.current + result.length
            const rows = result.map((item: PersonalProductsType) => {
                return {
                    id: item.id,
                    itendificatoin_products: item?.goodsId || '',
                    name: item?.name || '',
                    description: item?.description || '',
                    preview: item?.preview?.uuid || '',
                    previzNeiiew: item?.photo?.uuid || '',
                }
            })
            setList([...list, ...rows])
        } catch (error) {
            message.error(t('errors.get_data'))
        }
        setLoading(false)
    }

    const loadForSearch = async () => {
        setLoading(true)
        try {
            const result = await PersonalProductsService.getPersonalProducts({
                offset: offset.current,
                search: search,
            })

            offset.current = offset.current + result.length
            const rows = result.map((item: PersonalProductsType) => {
                return {
                    id: item.id,
                    itendificatoin_products: item?.goodsId || '',
                    name: item?.name || '',
                    description: item?.description || '',
                    preview: item?.preview?.uuid || '',
                    previzNeiiew: item?.photo?.uuid || '',
                }
            })
            setList(rows)
        } catch (error) {
            message.error(t('errors.get_data'))
        }
        setLoading(false)
    }

    const clear = () => {
        setList([])
        setDel(0)
        offset.current = 0
    }

    useEffect(() => {
        load().then()
    }, [])

    useEffect(() => {
        clear()
        loadForSearch().then()
    }, [search])

    const goToEdit = (id: number) => {
        navigate(`${Routes.PERSONAL_PRODUCTS_EDIT}?id=${id}`)
    }
    const onCreateNew = () => {
        navigate(Routes.PERSONAL_PRODUCTS_CREATE)
    }

    const onDeleteScreen = useCallback(async () => {
        try {
            const response =
                await PersonalProductsService.deletePersonalProducts(del)

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

    return (
        <MainLayout
            title={t('screen_title.personal_products')}
            searchPlaceholder={t('forms.search_product')}
            onAdd={onCreateNew}
            onEndReached={load}
            onSearch={setSearch}
        >
            <>
                <MainTable
                    columns={columns}
                    onEdit={goToEdit}
                    data={list}
                    onDelete={setDel}
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
export default PersonalProducts
