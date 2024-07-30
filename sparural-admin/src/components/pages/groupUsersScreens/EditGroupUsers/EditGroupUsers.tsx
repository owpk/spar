import { Col, message, Row } from 'antd'
import produce from 'immer'
import { FC, useCallback, useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useLocation, useNavigate } from 'react-router-dom'
import { GroupUsersService } from '../../../../services/GroupUsersService'
import { MainLayout } from '../../../complexes/MainLayout'
import { EditWrapper } from '../../../simples/EditWrapper'
import { InputHolder } from '../../../simples/InputHolder'
import { TextField } from '../../../simples/TextField'
import UsersBlock from './UsersBlock'
import UserSearchBlock, { UsersFilterType } from './UserSearchBlock'
import { ReactComponent as DeleteUsers } from '../../../../assets/icons/delete_users.svg'
import { ReactComponent as AddUsers } from '../../../../assets/icons/add_users.svg'
import styles from './EditGroupUsers.module.scss'
import { UsersService } from '../../../../services/UsersService'
import {
    Counters,
    StatusBuyersType,
    UserAtributes,
    UserType,
    Currency as CurrencyType
} from '../../../../types'
import { Routes } from '../../../../config'
import { Button } from '../../../simples/Button'
import { StatusBuyersService } from '../../../../services/StatusBuyersService'
import { CurrenciesService } from '../../../../services/CurrenciesService'

const EditGroupUsers: FC = () => {
    const { t } = useTranslation()
    const id = useLocation().search.split('=')[1]
    const navigation = useNavigate()
    const inGroupUsersList = useRef<UserType[]>([])
    const notInGroupUsersList = useRef<UserType[]>([])

    const [isShowAll, setIsShowAll] = useState<boolean>(false)

    const [groupName, setGroupName] = useState<string>('')

    const [allUserAtributes, setAllUserAtributes] = useState<UserAtributes[]>()
    const [allUserStatuses, setAllUserStatuses] = useState<StatusBuyersType[]>()
    const [allUserCounters, setAllUserCounters] = useState<Counters[]>()
    const [allCurrencies, setAllCurrencies] = useState<Array<CurrencyType>>([])

    const [groupUsers, setGroupUsers] = useState<number[]>([])
    const [allGroupUsers, setAllGroupUSers] = useState<boolean>(false)
    const [usersInTheGroup, setUsersInTheGroup] = useState<UserType[]>([])
    const [usersInTheGroupCount, setUsersInTheGroupCount] = useState<number>(0)

    inGroupUsersList.current = usersInTheGroup

    const [loading, setLoading] = useState(false)
    const [deletedUsers, setDeletedUsers] = useState<number[]>([])
    const [idGroup, setIdGroup] = useState<number>()

    const currentId = useRef(idGroup)

    const [users, setUsers] = useState<number[]>([])
    const [usersCount, setUsersCount] = useState<number>(0)
    const [allUsers, setAllUsers] = useState<boolean>(false)
    const [usersNotInTheGroup, setUsersNotInTheGroup] = useState<UserType[]>([])

    notInGroupUsersList.current = usersNotInTheGroup
    const [error, setError] = useState<string>('')

    const [groupFilter, setGroupFilter] = useState<UsersFilterType>({})
    const [usersFilter, setUsersFilter] = useState<UsersFilterType>({})
    const offset = useRef(0)
    const hasGroup = useRef(true)
    const offset2 = useRef(0)
    const has = useRef(true)

    const [groupLoading, setGroupLoading] = useState<boolean>(false)
    const [notGroupLoading, setNotGroupLoading] = useState<boolean>(false)

    /**
     * fetch users into group
     */
    const loadUsersIntoGroup = async (isNew?: boolean) => {
        if (!hasGroup.current || groupLoading) {
            return
        }
        setGroupLoading(true)

        const result = await UsersService.getUsersList({
            offset: offset.current,
            group: currentId.current,
            ...groupFilter,
            limit: 50,
            role: [2, 3, 4],
        })

        setUsersInTheGroupCount(result?.meta?.total_count)

        if (!result.data.length) {
            hasGroup.current = false
            setGroupLoading(false)
        }
        offset.current = offset.current + result.data.length
        setUsersInTheGroup(
            isNew ? result.data : [...inGroupUsersList.current, ...result.data]
        )
        setGroupLoading(false)
        // } catch (error) {
        //     message.error(t('errors.get_data'))
        //     setGroupLoading(false)
        // }
        setGroupLoading(false)
    }

    const loadUsersNotIntoGroup = useCallback(
        async (isFiltered?: boolean) => {
            if (!has.current || notGroupLoading) {
                return
            }
            setNotGroupLoading(true)

            const result = await UsersService.getUsersList({
                offset: offset2.current,
                ...usersFilter,
                notInGroup: currentId.current,
                limit: 50,
                role: [2, 3, 4],
            })
            console.log('EditGroupUsers@loadUsersNotIntoGroup', {result})
            if (result.data.length === 0) {
                has.current = false
                setNotGroupLoading(false)
            }
            offset2.current = offset2.current + result.data.length

            setUsersCount(result?.meta?.total_count)

            setUsersNotInTheGroup((prev) =>
                isFiltered ? result.data : [...prev, ...result.data]
            )

            setNotGroupLoading(false)
        },
        [notGroupLoading, usersFilter]
    )

    /**
     * fetch one screen
     */

    const getOneScreenById = useCallback(async () => {
        try {
            setLoading(true)
            const response = await GroupUsersService.getOneGroupUsers(
                Number(id)
            )
            setGroupName(response.name)
        } catch (error) {
            message.error(t('errors.get_data'))
            setLoading(false)
        }
    }, [id, t])

    const getAllCurrencies = useCallback(async () => {
        try {
            setLoading(true)
            const currencies = await CurrenciesService.getList()
            setAllCurrencies(currencies)
        } catch (error) {
            message.error(t('errors.get_data'))
            setLoading(false)
        }
    }, [setAllCurrencies])

    const getAllUserAtributes = useCallback(async () => {
        try {
            setLoading(true)
            const response = await GroupUsersService.getAllUserAtributes({limit: 1000})
            setAllUserAtributes(response)
        } catch (error) {
            message.error(t('errors.get_data'))
            setLoading(false)
        }
    }, [])

    const getAllUserStatuses = async () => {
        setLoading(true)
        try {
            const result = await StatusBuyersService.getStatusBuyers({})

            setAllUserStatuses(result)
        } catch (error) {
            message.error(t('errors.get_data'))
        }
        setLoading(false)
    }

    const getAllUserCounters = async () => {
        setLoading(true)
        try {
            const result = await GroupUsersService.getAllCounters({limit: 1000})

            setAllUserCounters(result)
        } catch (error) {
            message.error(t('errors.get_data'))
        }
        setLoading(false)
    }

    const createOneScreenById = useCallback(async () => {
        try {
            setLoading(true)
            const response = await GroupUsersService.createGroup({ name: '' })
            setIdGroup(response.id)
            currentId.current = response.id
            try {
                await UsersService.getUsersList({
                    limit: 1000,
                    group: response.id,
                })
            } catch (error) {
                console.log('error get users not in the group')
            }

            setLoading(false)
        } catch (error) {
            message.error(t('errors.get_data'))
            setLoading(false)
        }
    }, [t])

    /**
     * save function
     */
    const onHandleSave = useCallback(async () => {
        if (!groupName) {
            setError(t('errors.required_field'))
            message.warning(t('errors.require_message'))
            return
        }
        if (idGroup)
            try {
                await GroupUsersService.editGroup(idGroup, {
                    name: groupName,
                })
                message.success(t('suсcess_messages.save_data'))
                navigation(Routes.GROUP_USERS_SCREEN)
            } catch (error) {
                message.success(t('errors.save_data'))
            }
    }, [groupName, idGroup, navigation, t])

    /**
     * click on group user
     */
    const onClickGroupUser = (userId: number) => {
        if (groupUsers.includes(userId)) {
            setGroupUsers((prev) => prev.filter((i) => i !== userId))
        } else {
            setGroupUsers(
                produce((draft) => {
                    draft.push(userId)
                })
            )
        }
    }

    /**
     * click on user
     */
    const onClickUser = (userId: number) => {
        if (users.includes(userId)) {
            setUsers((prev) => prev.filter((i) => i !== userId))
        } else {
            setUsers(
                produce((draft) => {
                    draft.push(userId)
                })
            )
        }
    }

    /**
     * fetching data if we edit Info screen
     */
    useEffect(() => {
        if (!!id) {
            setIdGroup(+id)
            currentId.current = +id
            getOneScreenById().then()
            getAllUserAtributes().then()
            getAllUserStatuses().then()
            getAllUserCounters().then()
            getAllCurrencies().then()
            loadUsersNotIntoGroup().then()
            loadUsersIntoGroup().then()
        } else {
            createOneScreenById().then()
            getAllUserAtributes().then()
            getAllUserStatuses().then()
            getAllUserCounters().then()
            getAllCurrencies().then()
        }
    }, [id])

    /**
     *
     */
    const clear = async () => {
        hasGroup.current = true
        setUsersInTheGroup([])
        setGroupLoading(false)
        inGroupUsersList.current = []
        offset.current = 0
        // if(!!id)
        await loadUsersIntoGroup(true)
    }
    /**
     *
     */
    const clearNotInGroup = async () => {
        has.current = true
        setNotGroupLoading(false)
        setUsersNotInTheGroup([])
        notInGroupUsersList.current = []
        offset2.current = 0
        await loadUsersNotIntoGroup(true)
    }

    const inGroupTimer = useRef<NodeJS.Timeout>()
    useEffect(() => {
        if (inGroupTimer.current) {
            clearTimeout(inGroupTimer.current)
        }

        inGroupTimer.current = setTimeout(() => {
            if (!!idGroup) {
                clear().then()
            }
        }, 500)
    }, [
        idGroup,
        groupFilter,
    ])

    const notInGroupTimer = useRef<NodeJS.Timeout>()
    useEffect(() => {
        if (notInGroupTimer.current) {
            clearTimeout(notInGroupTimer.current)
        }

        notInGroupTimer.current = setTimeout(() => {
            if (!!idGroup) {
                clearNotInGroup().then()
            }
        }, 500)

    }, [
        idGroup,
        usersFilter,
    ])
    //
    const onHandleDeleteUsers = useCallback(async () => {
        if (!idGroup) {
            return
        }

        try {
            await GroupUsersService.deleteUsersIntoGroup(idGroup, groupUsers)

            setUsersInTheGroup((prev) =>
                prev.filter((i) => !groupUsers.includes(i.id))
            )
            setGroupUsers((prev) => [])

            const newUsers = usersInTheGroup.filter((i) =>
                groupUsers.includes(i.id)
            )
            setUsersNotInTheGroup((prev) => [...prev, ...newUsers])
            setDeletedUsers((prev) => [
                ...prev,
                ...Array.from(newUsers, (i) => i.id),
            ])
            setAllGroupUSers(false)
            setAllUsers(false)
            message.success(t('suсcess_messages.users_removed'))
        } catch (error) {
            message.error(t('errors.users_removed'))
        }
    }, [groupUsers, idGroup, t, usersInTheGroup])

    const onHandleAddUsers = useCallback(async () => {
        if (idGroup) {
            try {
                await GroupUsersService.addUsersIntoGroup(idGroup, users)

                // await loadUsersNotIntoGrou1p()
                setUsersNotInTheGroup((prev) => {
                    notInGroupUsersList.current = prev.filter(
                        (i) => !users.includes(i.id)
                    )
                    return prev.filter((i) => !users.includes(i.id))
                })
                setUsers([])

                await clear()

                message.success(t('success_messages.users_added'))
            } catch (error) {
                message.error(t('errors.users_added'))
            }
        }
    }, [idGroup, t, users])

    const checkAllUsers = () => {
        setAllUsers((prev) => {
            if (prev) {
                setUsers([])
            } else {
                setUsers(Array.from(usersNotInTheGroup, (i) => i.id))
            }
            return !prev
        })
    }
    const checkAllGroupUsers = () => {
        setAllGroupUSers((prev) => {
            if (prev) {
                setGroupUsers([])
            } else {
                setGroupUsers(Array.from(usersInTheGroup, (i) => i.id))
            }
            return !prev
        })
    }

    /**
     * change group name
     */
    const onChangeGroupName = useCallback((e: string) => {
        setGroupName(e)
        setError('')
    }, [])


    console.log('EditGroupUsers', {usersNotInTheGroup})

    return (
        <MainLayout isLoading={false} title={t('screen_title.user_group')}>
            <EditWrapper
                onSave={onHandleSave}
                title={t(!id ? 'common.add' : 'common.edit_full')}
            >
                <div
                    style={{
                        minWidth: 1100,
                        flexWrap: 'wrap',
                    }}
                >
                    <InputHolder>
                        <TextField
                            error={error}
                            label={t('forms.group_name')}
                            onChange={onChangeGroupName}
                            value={groupName}
                        />
                    </InputHolder>
                    <div className={styles.titleName}>
                        <Col span={8}>
                            <div className={styles.title}>
                                {t('common.group_group')}
                            </div>
                        </Col>

                        <Col span={3}></Col>
                        <Col span={8}>
                            <div className={styles.title}>
                                {t('common.group_pocup')}
                            </div>
                        </Col>
                    </div>
                    <Row>
                        <Col span={8}>
                            <UserSearchBlock
                                onFilter={setGroupFilter}
                                isShowAll={isShowAll}
                                allUserAtributes={allUserAtributes ?? []}
                                allUserStatuses={allUserStatuses ?? []}
                                allUserCounters={allUserCounters ?? []}
                                allCurrencies={allCurrencies}
                            />
                        </Col>
                        <Col span={3}></Col>
                        <Col span={8}>
                            <UserSearchBlock
                                onFilter={setUsersFilter}
                                isShowAll={isShowAll}
                                allUserAtributes={allUserAtributes ?? []}
                                allUserStatuses={allUserStatuses ?? []}
                                allUserCounters={allUserCounters ?? []}
                                allCurrencies={allCurrencies}
                            />
                        </Col>
                    </Row>
                    <Button
                        onClick={() => setIsShowAll(!isShowAll)}
                        label={isShowAll ? 'Свернуть' : 'Все фильтры'}
                        backgroundColor="#ffffff"
                        colorText="#007C45"
                        classes={{ root: styles.showAllBtn }}
                        isArrow
                        isArrowUp={!isShowAll}
                    />
                    <Row>
                        <Col span={8}>
                            <UsersBlock
                                onEndReached={loadUsersIntoGroup}
                                isAll={allGroupUsers}
                                users={usersInTheGroup}
                                values={groupUsers}
                                onCheck={onClickGroupUser}
                                onCheckAll={checkAllGroupUsers}
                                usersCount={usersInTheGroupCount}
                            />
                        </Col>
                        <Col span={3}>
                            <div className={styles.addDeleteBlock}>
                                <div
                                    onClick={onHandleDeleteUsers}
                                    className={styles.delUsersIcon}
                                >
                                    <DeleteUsers />
                                </div>
                                <div
                                    onClick={onHandleAddUsers}
                                    className={styles.addUsersIcon}
                                >
                                    <AddUsers />
                                </div>
                            </div>
                        </Col>
                        <Col span={8}>
                            <UsersBlock
                                upperOne
                                onEndReached={loadUsersNotIntoGroup}
                                isAll={allUsers}
                                users={usersNotInTheGroup}
                                values={users}
                                onCheck={onClickUser}
                                onCheckAll={checkAllUsers}
                                usersCount={usersCount}
                            />
                        </Col>
                    </Row>
                </div>
            </EditWrapper>
        </MainLayout>
    )
}

export default EditGroupUsers
