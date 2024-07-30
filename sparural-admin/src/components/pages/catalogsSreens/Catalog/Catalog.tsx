import { FC, useCallback, useEffect, useRef, useState } from 'react'
import { MainLayout } from '../../../complexes/MainLayout'
import styles from './Catalog.module.scss'
import { MainTable } from '../../../complexes/MainTable'
import { DeleteModal } from '../../../complexes/DeleteModal'
import { useTranslation } from 'react-i18next'
import { useNavigate } from 'react-router-dom'
import { Routes } from '../../../../config'
import { Celltype, ColumnType } from '../../../complexes/MainTable/TableBody'
import { CatalogsPagesService } from '../../../../services/CatalogsService'
import { message } from 'antd'

type TableCatalogType = {
    id: number
    name: string
    image: string
    url: string
}

const Catalog: FC = () => {
    const { t } = useTranslation()
    const navigate = useNavigate()
    const [del, setDel] = useState<number>(0)
    const [list, setList] = useState<Array<TableCatalogType>>([])
    const [loading, setLoading] = useState(false)
    const offset = useRef(0)

    // prepare columns for table
    const columns: Array<ColumnType> = [
        {
            key: 'name',
            title: t('common.name'),
            width: 3,
        },
        {
            key: 'image',
            title: t('common.image'),
            type: Celltype.IMAGE,
            width: 1,
        },
        {
            key: 'url',
            title: t('common.link'),
            width: 6,
        },
    ]

    //fetchung data
    const load = async () => {
        setLoading(true)
        try {
            const result = await CatalogsPagesService.getCatalogsPages({
                limit: 30,
                offset: offset.current,
            })

            offset.current = offset.current + result.length
            const rows = result
                .map((item) => {
                    return {
                        id: item.id,
                        name: item.name || '',
                        image: item.photo?.uuid || '',
                        url: item.url || ''
                    }
                })
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
        navigate(`${Routes.CATALOG_EDIT}?id=${id}`)
    }

    const goToCreate = () => {
        navigate(Routes.CATALOG_CREATE)
    }

    const onDeleteScreen = useCallback(async () => {
        const response = await CatalogsPagesService.deleteCatalogsPages(del)
        if (response) {
            setList((prev) => prev.filter((i) => i.id !== del))
            setDel(0)
        } else {
            message.error(t('errors.delete'))
        }
    }, [del, t])

    return (
        <MainLayout
        onEndReached={load}
            onAdd={goToCreate}
            isLoading={loading}
            title={t('screen_title.catalogs')}
        >
            <>
                <div className={styles.BannerScreen}>
                    <MainTable
                        columns={columns}
                        data={list}
                        onDelete={setDel}
                        onEdit={(id) => goToRedact(id)}
                    />
                </div>
                <DeleteModal
                    onSubmit={onDeleteScreen}
                    onCancel={() => setDel(0)}
                    visible={!!del}
                />
            </>
        </MainLayout>
    )
}
export default Catalog
