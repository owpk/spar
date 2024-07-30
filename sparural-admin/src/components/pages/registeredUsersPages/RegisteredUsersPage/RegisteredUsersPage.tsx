import { message } from 'antd'
import { FC, useCallback, useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useNavigate } from 'react-router-dom'
import { Routes } from '../../../../config'
import { UsersService } from '../../../../services/UsersService'
import { GenderType } from '../../../../types'
import { printDate } from '../../../../utils/helpers'
import { DeleteModal } from '../../../complexes/DeleteModal'
import { MainLayout } from '../../../complexes/MainLayout'
import { MainTable } from '../../../complexes/MainTable'
import { DataUsersRegisteredType } from '../../../complexes/MainTable/MainTable'
import { ColumnType } from '../../../complexes/MainTable/TableBody'

const RegisteredUsersPage: FC = () => {
    const { t } = useTranslation()
    const navigate = useNavigate()

    const [list, setList] = useState<Array<DataUsersRegisteredType>>([])
    const [loading, setLoading] = useState(false)
    const [search, setSearch] = useState<string>('')
    const offset = useRef(0)
    const has = useRef(true)
    const data = useRef<DataUsersRegisteredType[]>([])
    data.current = list
    const [del, setDel] = useState<number>(0)

    /**
     * column data for table
     */
    const columns: Array<ColumnType> = [
        {
            key: 'number_sort',
            title: t('table.number'),
            width: 0.5,
        },
        {
            key: 'full_name',
            title: t('table.full_name'),
            width: 3,
        },
        {
            key: 'gender',
            title: t('table.gender'),
            width: 0.5,
        },
        {
            key: 'birthday',
            title: t('table.birthday'),
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
                search: search,
            })
            if (!result.data.length) {
                has.current = false
                setLoading(false)
                return
            }
            // prepare data for Table
            const rows: DataUsersRegisteredType[] = result.data
                .map((item) => {
                    return {
                        id: item.id,
                        number_sort: item.id,
                        full_name: `${item?.lastName || ''} ${
                            item?.firstName || ''
                        } ${item?.patronymicName || ''}`,
                        phone_number: item?.phoneNumber || '',
                        email: item?.email || '',
                        birthday: item.birthday ? printDate(item.birthday) : '',
                        gender:
                            item.gender === GenderType.MAIL
                                ? t('common.male')
                                : item.gender === GenderType.FEMALE
                                ? t('common.female')
                                : '',
                    }
                })
                //времено
                .sort((a, b) => b.id - a.id)
            offset.current = offset.current + result.data.length
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
        navigate(Routes.REGISTRED_USERS_SCREEN_CREATE)
    }, [navigate])

    /**
     * naviget to edit page
     * @param id - user id
     */
    const goToEditPage = useCallback(
        (id: number) => {
            navigate(`${Routes.REGISTRED_USERS_SCREEN_EDIT}?userId=${id}`)
        },
        [navigate]
    )

    /**
     *
     */
    const onHandleDeleteUser = useCallback(async () => {
        const response = await UsersService.deleteUser(del)

        if (response) {
            setList((prev) => prev.filter((i) => i.id !== del))
            setDel(0)
        } else {
            message.error(t('errors.delete'))
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
            onEndReached={load}
            onAdd={goToCreatePage}
            title={t('screen_title.registered_users_page')}
            onSearch={setSearch}
            searchPlaceholder={t('registered_users_page.search_user_customer')}
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

export default RegisteredUsersPage
