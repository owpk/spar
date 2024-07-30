import { message } from 'antd'
import React, { FC, useCallback, useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useNavigate } from 'react-router-dom'
import { Routes } from '../../../../config'
import { UsersService } from '../../../../services/UsersService'
import { DeleteModal } from '../../../complexes/DeleteModal'
import { MainLayout } from '../../../complexes/MainLayout'
import { MainTable } from '../../../complexes/MainTable'
import { DataUsersType } from '../../../complexes/MainTable/MainTable'
import { Celltype, ColumnType } from '../../../complexes/MainTable/TableBody'

const UsersPage: FC = () => {
    const { t } = useTranslation()
    const navigate = useNavigate()

    const [list, setList] = useState<Array<DataUsersType>>([])
    const [loading, setLoading] = useState(false)
    const offset = useRef(0)
    const has = useRef(true)

    const [del, setDel] = useState<number>(0)

    /**
     * column data for table
     */
    const columns: Array<ColumnType> = [
        {
            key: 'full_name',
            title: t('table.full_name'),
            width: 4,
        },
        {
            key: 'position',
            title: t('table.position'),
            type: Celltype.ARRAY,
            objectKey: 'name',
            width: 2,
        },
        {
            key: 'phone_number',
            title: t('table.phone_number'),
            width: 2,
        },
        {
            key: 'email',
            title: t('table.email'),
            width: 2,
        },
    ]

    /**
     *
     * fetching list of users
     */
    const load = async () => {
        if (!has.current || loading) {
            return
        }

        setLoading(true)
        try {
            const result = await UsersService.getUsersList({
                offset: offset.current,
                role: [3, 4],
            })
            if (!result.data.length) {
                has.current = false
                return
            }
            // prepare data for Table
            const rows = result.data
                .map((item) => {
                    return {
                        id: item.id,
                        full_name: `${item?.lastName || ''} ${
                            item?.firstName || ''
                        } ${item?.patronymicName || ''}`,
                        position: item.roles,
                        phone_number: item.phoneNumber,
                        email: item.email,
                    }
                })
                // временно
                .sort((a, b) => b.id - a.id)

            offset.current = offset.current + result.data.length
            setList([...list, ...rows])
        } catch (error) {
            message.error(t('errors.get_data'))
        }
        setLoading(false)
    }
    useEffect(() => {
        load().then()
    }, [])

    /**
     * navigate to create page
     */
    const goToCreatePage = useCallback(() => {
        navigate(Routes.USERS_SCREEN_CREATE)
    }, [navigate])

    /**
     * naviget to edit page
     * @param id - user id
     */
    const goToEditPage = useCallback(
        (id: number) => {
            navigate(`${Routes.USERS_SCREEN_EDIT}?userId=${id}`)
        },
        [navigate]
    )

    /**
     *
     */
    const onHandleDeleteUser = useCallback(async () => {
        // !TODO enable when back will be ready
        const response = await UsersService.deleteUser(del)

        if (response) {
            setList((prev) => prev.filter((i) => i.id !== del))
            setDel(0)
        } else {
            message.error(t('errors.delete'))
        }
    }, [del, t])
    return (
        <MainLayout
            title={t('screen_title.users_page')}
            onAdd={goToCreatePage}
            onEndReached={load}
        >
            <>
                <MainTable
                    columns={columns}
                    data={list}
                    onEdit={goToEditPage}
                    onDelete={setDel}
                />
                <DeleteModal
                    onSubmit={onHandleDeleteUser}
                    onCancel={() => setDel(0)}
                    visible={!!del}
                />
            </>
        </MainLayout>
    )
}

export default UsersPage
