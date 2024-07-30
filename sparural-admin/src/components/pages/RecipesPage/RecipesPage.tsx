import { message } from 'antd'
import React, { FC, useCallback, useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useNavigate } from 'react-router-dom'
import { Routes } from '../../../config'
import { DeleteModal } from '../../complexes/DeleteModal'
import { MainLayout } from '../../complexes/MainLayout'
import { MainTable } from '../../complexes/MainTable'
import { Celltype, ColumnType } from '../../complexes/MainTable/TableBody'
import styles from './RecipesPage.module.scss'
import { Recipe, RecipesService } from '../../../services/RecipesService'

type Props = {}

type TableGoodsType = {
    id: number
    itendificatoin_products: string
    name: string
    description: string
    preview: string
    previzNeiiew: string
}

const RecipesPage: FC<Props> = () => {
    const { t } = useTranslation()
    const navigate = useNavigate()
    const [del, setDel] = useState<number>(0)
    const [loading, setLoading] = useState(false)
    const [search, setSearch] = useState<string>('')
    const offset = useRef(0)
    const [list, setList] = useState<Array<TableGoodsType>>([])

    const columns: Array<ColumnType> = [
        {
            key: 'name',
            title: t('forms.header'),
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
            const result = await RecipesService.getRecipes({
                offset: offset.current,
            })

            offset.current = offset.current + result.length
            const rows = result.map((item: Recipe) => {
                return {
                    id: item.id,
                    itendificatoin_products: item?.id.toString() || '',
                    name: item?.title || '',
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
    }, [search])

    const goToEdit = (id: number) => {
        navigate(`${Routes.RECIPES_EDIT}?recipeId=${id}`)
    }
    const onCreateNew = () => {
        navigate(Routes.RECIPES_CREATE)
    }

    const onDeleteScreen = useCallback(async () => {
        try {
            const response = await RecipesService.deleteRecipe(del)

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
            title={t('screen_title.recipes')}
            onAdd={onCreateNew}
            onEndReached={load}
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
export default RecipesPage
