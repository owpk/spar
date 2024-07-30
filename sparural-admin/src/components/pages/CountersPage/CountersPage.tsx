import { message } from 'antd'
import React, { FC, useCallback, useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useNavigate } from 'react-router-dom'
import { Routes } from '../../../config'
import { DeleteModal } from '../../complexes/DeleteModal'
import { MainLayout } from '../../complexes/MainLayout'
import { MainTable } from '../../complexes/MainTable'
import { ColumnType } from '../../complexes/MainTable/TableBody'
import { GroupUsersService } from '../../../services/GroupUsersService'
import { Counters } from '../../../types'

type Props = {}

const CountersPage: FC<Props> = () => {
    const { t } = useTranslation()
    const navigate = useNavigate()
    const [del, setDel] = useState<number>(0)
    const [loading, setLoading] = useState(false)
    const [search, setSearch] = useState<string>('')
    const offset = useRef(0)
    const [list, setList] = useState<Array<Counters>>([])

    const columns: Array<ColumnType> = [
        {
            key: 'name',
            title: t('common.name'),
            width: 2,
        },
        {
            key: 'loymaxId',
            title: t('forms.counter_identificator'),
            width: 2,
        },
    ]

    const load = async () => {
        setLoading(true)
        try {
            const result = await GroupUsersService.getAllCounters({
                offset: offset.current,
            })

            offset.current = offset.current + result.length
            const rows = result.map((item: Counters) => {
                return {
                    id: item.id,
                    loymaxId: item.loymaxId,
                    name: item.name,
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
        navigate(`${Routes.COUNTERS_EDIT}?counterId=${id}`)
    }
    const onCreateNew = () => {
        navigate(Routes.COUNTERS_CREATE)
    }

    const onDeleteScreen = useCallback(async () => {
        try {
            const response = await GroupUsersService.deleteCounter(del)

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
            title={t('screen_title.counters')}
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
export default CountersPage
