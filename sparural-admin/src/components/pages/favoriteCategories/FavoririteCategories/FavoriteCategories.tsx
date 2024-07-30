import { message } from 'antd'
import produce from 'immer'
import React, { FC, useCallback, useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useNavigate } from 'react-router-dom'
import { Routes } from '../../../../config'
import { FavoriteCategoriesService } from '../../../../services/FavoriteCategoriesService'
import { FavoriteCategoriesType } from '../../../../types'
import { DeleteModal } from '../../../complexes/DeleteModal'
import { MainLayout } from '../../../complexes/MainLayout'
import { MainTable } from '../../../complexes/MainTable'
import { Celltype, ColumnType } from '../../../complexes/MainTable/TableBody'

type Props = {}

type TableDataType = {
    id: number
    image: string
    button: boolean
    name: string

  }

const FavoriteCategories: FC<Props> = () => {
    const { t } = useTranslation()
    const navigate = useNavigate()
    const [del, setDel] = useState<number>(0)
    const [list, setList] = useState<Array<TableDataType>>([])
    const [loading, setLoading] = useState(false)
    const offset = useRef(0)

    const columns: Array<ColumnType> = [
        {
            key: 'name',
            title: t('common.name'),
            width: 10,
        },
        {
            key: 'image',
            title: t('common.image'),
            type: Celltype.IMAGE,
            width: 2,
        },
        {
            key: 'button',
            title: t('common.button'),
            type: Celltype.BUTTON,
            width: 4,
        },
    ]

    const load = async () => {
        setLoading(true)
        try {
            const result =
                await FavoriteCategoriesService.getFavoriteCategories({
                    offset: offset.current,
                })

            offset.current = offset.current + result.length
            const rows = result.map((item: FavoriteCategoriesType) => {
                return {
                  id: item.id,
                  image:item?.photo?.uuid  || "",
                  button: !!item?.isPublic || false,
                  name: item?.name || "",

                };
              });
            setList([...list, ...rows])
        } catch (error) {
            message.error(t('errors.get_data'))
        }
        setLoading(false)
    }
    useEffect(() => {
        load().then()
    }, [])

    const goToRedact = (id: number) => {
        navigate(`${Routes.FAVORITE_CATEGORIES_EDIT}?id=${id}`)
    }

    /**
     * change is Publick
     */
    const onHandlePublic = useCallback( async(id: number, publick: boolean) => {
        try {
            const res = await FavoriteCategoriesService.updateFavoriteCategories(id, {
                isPublic: publick
            })
            setList(produce(
                draft => {
                    const find = draft.find(i => i.id === id)
                    if(find)
                    find.button = res.isPublic
                }
            ))
            message.success(t("suсcess_messages.update_data"))
        } catch (error) {
            message.success(t("errors.update_data"))
        }
    },[t])

    /**
     * delete category
     */
    const onHandleDelete = useCallback( async() => {
        try {
            await FavoriteCategoriesService.deleteFavoriteCategoriesr(del)
            setList(prev => prev.filter(i => i.id !== del))
            message.success(t("suсcess_messages.delete_data"))
            setDel(0)
        } catch (error) {
            message.error(t("errors.delete_data"))
        }
    },[del, t])
    return (
        <MainLayout
            title={t('screen_title.favorite_categories')}
            isLoading={loading}
            onEndReached={load}
            // onAdd={goToCreate} Loymax
        >
            <>
                <MainTable
                    columns={columns}
                    data={list}
                    onEdit={(id) => goToRedact(id)}
                    onDelete={setDel}
                    onBtnClick={onHandlePublic}
                />
                <DeleteModal
                    onSubmit={onHandleDelete}
                    onCancel={() => setDel(0)}
                    visible={!!del}
                />
            </>
        </MainLayout>
    )
}
export default FavoriteCategories
