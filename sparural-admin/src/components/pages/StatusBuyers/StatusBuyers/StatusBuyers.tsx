import { FC, useCallback, useEffect, useRef, useState } from 'react'
import { MainLayout } from '../../../complexes/MainLayout'
import styles from './StatusBuyers.module.scss'
import { MainTable } from '../../../complexes/MainTable'
import { DeleteModal } from '../../../complexes/DeleteModal'
import { useTranslation } from 'react-i18next'
import { useNavigate } from 'react-router-dom'
import { Routes } from '../../../../config'
import { Celltype, ColumnType } from '../../../complexes/MainTable/TableBody'
import { StatusBuyersType } from '../../../../types'
import { message } from 'antd'
import { StatusBuyersService } from '../../../../services/StatusBuyersService'

type TableStatusType = {
    id: number
    name: string
    threshold: number
    icon: string
}

const StatusBuyers: FC = () => {
    const { t } = useTranslation()
    const navigate = useNavigate()
    const [del, setDel] = useState<number>(0)
    const [list, setList] = useState<Array<TableStatusType>>([])
    const [loading, setLoading] = useState(false)
    const offset = useRef(0)

    // prepare columns for table
    const columns: Array<ColumnType> = [
        {
            key: 'name',
            title: t('common.name'),
            width: 7,
        },
        {
            key: 'threshold',
            title: t('common.threshold'),
            width: 3,
        },
        {
            key: 'icon',
            title: t('common.icon'),
            type: Celltype.ICON,
            width: 3,
        },
    ]


    // fetchung data
    const load = async () => {
        setLoading(true)
        try {
            const result = await StatusBuyersService.getStatusBuyers({
                offset: offset.current,
            })

            offset.current = offset.current + result.length
            const rows = result.map((i) => ({
                id: i.id,
                name: i.name || '',
                threshold: i.threshold,
                icon: i.icon.uuid || ''
            }))
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
        navigate(`${Routes.STATUS_BUYERS_EDIT}?id=${id}`)
    }

    // const goToCreate = () => {
    //     navigate(Routes.STATUS_BUYERS_CREATE)
    // }

    const onDeleteScreen = useCallback(async () => {
        const response = await StatusBuyersService.deleteStatusBuyers(del)
        if (response) {
            setList((prev) => prev.filter((i) => i.id !== del))
            setDel(0)
        } else {
            message.error(t('errors.delete'))
        }
    }, [del, t])
    return (
        <MainLayout
            // onAdd={goToCreate} // from Loymax
            isLoading={loading}
            title={t('screen_title.status_buyers')}
            onEndReached={load}
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
export default StatusBuyers
