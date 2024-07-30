import { message } from 'antd'
import React, { FC, useCallback, useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useNavigate } from 'react-router-dom'
import { Routes } from '../../../../config'
import { GroupUsersService } from '../../../../services/GroupUsersService'
import { DeleteModal } from '../../../complexes/DeleteModal'
import { MainLayout } from '../../../complexes/MainLayout'
import { MainTable } from '../../../complexes/MainTable'
import { DataGroupUsers } from '../../../complexes/MainTable/MainTable'
import { ColumnType } from '../../../complexes/MainTable/TableBody'

const GroupUsers: FC = () => {
    const { t } = useTranslation()
    const navigate = useNavigate()

    const [list, setList] = useState<Array<DataGroupUsers>>([])
    const [loading, setLoading] = useState(false)
    const [search, setSearch] = useState<string>('')
    const offset = useRef(0)
    const has = useRef(true)
    const data = useRef<DataGroupUsers[]>([])
    data.current = list

    const [del, setDel] = useState<number>(0)

    /**
     * column data for table
     */
    const columns: Array<ColumnType> = [
        {
            key: 'group_name',
            title: t('table.group_name'),
            width: 10,
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
            const result = await GroupUsersService.getGroupUsers({
                offset: offset.current,
                search: search,
            })
            if (!result.length) {
                has.current = false
                setLoading(false)
                return
            }
            // prepare data for Table
            const rows: DataGroupUsers[] = result.map((item, index) => {
                return {
                    id: item.id,
                    group_name: item.name,
                }
            })
            offset.current = offset.current + result.length
            setList([...data.current, ...rows])
        } catch (error) {
            message.error(t('errors.get_data'))
        }
        setLoading(false)
    }
    useEffect(() => {
        clear()
        load().then()
    }, [search])

    /**
     * navigate to create page
     */
    const goToCreatePage = useCallback(() => {
        navigate(Routes.GROUP_USERS_SCREEN_CREATE)
    }, [navigate])

    /**
     * naviget to edit page
     * @param id - user id
     */
    const goToEditPage = useCallback(
        (id: number) => {
            navigate(`${Routes.GROUP_USERS_SCREEN_EDIT}?userId=${id}`)
        },
        [navigate]
    )

    /**
     * delete group
     */
    const onHandleDeleteUser = useCallback(async () => {
        try {
            const response = await GroupUsersService.deleteUsersGroup(del)
            if (response) {
                setList((prev) => {
                    const filter = prev.filter((user) => user.id !== del)
                    return filter
                })
                setDel(0)
                message.success(t('suсcess_messages.delete_data'))
                
            } else {
                message.warning(t('errors.delete_data'))
            }
        } catch (error) {
            message.success(t('errors.delete_data'))
        }
    }, [del, t])

    const clear = () => {
        has.current = true
        setList([])
        data.current = []
        offset.current = 0
    }
    return (
        <MainLayout
            onAdd={goToCreatePage}
            onEndReached={load}
            onSearch={setSearch}
            title={t('screen_title.user_group')}
            searchPlaceholder={t('forms.name_group')}
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

export default GroupUsers
