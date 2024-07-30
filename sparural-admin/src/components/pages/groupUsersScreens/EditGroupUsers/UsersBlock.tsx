import { t } from 'i18next'
import React, { FC, useRef } from 'react'
import { UserType } from '../../../../types'
import { Checkbox } from '../../../simples/Checkbox'
import { InputHolder } from '../../../simples/InputHolder'
import styles from './EditGroupUsers.module.scss'

type Props = {
    users: UserType[]
    usersCount?: number
    values: Array<number>
    onCheck: (userId: number) => void
    onCheckAll: () => void
    isAll: boolean
    onEndReached?: () => void
    upperOne?: boolean
}

/**
 *
 * @param users
 * @param values
 * @param onCheck
 * @param onCheckAll
 * @returns
 */

const UsersBlock: FC<Props> = ({
    users,
    usersCount,
    upperOne,
    values,
    onCheck,
    onCheckAll,
    isAll = false,
    onEndReached,
}) => {
    console.log(users)
    const contentRef = useRef<HTMLDivElement>(null)
    const reached = useRef(false)
    /**
     *
     * dynamic pagination
     */
    const handleScroll = () => {
        if (!contentRef.current) {
            return
        }

        const contentHeight = contentRef.current.offsetHeight
        const scrollHeight = contentRef.current.scrollHeight

        const scrollTop = contentRef.current.scrollTop

        if (scrollHeight <= contentHeight) {
            return
        }

        const afterEndReach =
            scrollHeight - (scrollTop + contentHeight) < contentHeight / 2

        if (afterEndReach && !reached.current) {
            reached.current = true
            onEndReached && onEndReached()
        } else if (!afterEndReach && reached.current) {
            reached.current = false
        }
    }

    return (
        <div
            ref={contentRef}
            onScroll={handleScroll}
            className={styles.usersBlockWrapper}
        >
            <span className={styles.usersCount}>{usersCount}</span>
            <InputHolder classes={styles.inputHolderRoot}>
                <Checkbox
                    value={0}
                    onClick={onCheckAll}
                    isChecked={isAll}
                    label={t('forms.check_all_users')}
                    labelPosition={'right'}
                />
            </InputHolder>
            {users &&
                users.map((user) => (
                    <div key={user.id}>
                        <InputHolder classes={styles.inputHolderRoot}>
                            <Checkbox
                                upperOne
                                labelPosition={'right'}
                                value={user.id}
                                onClick={(e) => onCheck(e as number)}
                                isChecked={values.includes(user.id)}
                                label={`${user.lastName || ''} ${
                                    user.firstName || ''
                                } ${user.patronymicName || ''}`}
                            />
                        </InputHolder>
                    </div>
                ))}
        </div>
    )
}

export default UsersBlock
