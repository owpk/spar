import message from 'antd/lib/message';
import React, { FC, useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { GroupUsersService } from '../../../services/GroupUsersService';
import { Selector } from '../../simples/Selector';
import { SelectOption } from '../../simples/Selector/OptionItem';
import styles from './UsersGroupDropdown.module.scss'



type Props = {
    onChange: (item: SelectOption) => void
    values: Array<SelectOption>
}

const UsersGroupDropdown: FC<Props> = ({
    onChange,
    values
 }) => {
    const { t } = useTranslation()
    const [list, setList] = useState<Array<SelectOption>>([])
    const [loading, setLoading] = useState(false)
    const [search, setSearch] = useState<string>('')
    const offset = useRef(0)
    const has = useRef(true)
    const data = useRef<SelectOption[]>([])
    data.current = list

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
            const rows: SelectOption[] = result.map((item, index) => {
                return {
                    value: item.id,
                    label: item.name,
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
        load().then()
    }, [])
    return (
        <Selector
            onRechedEnd={load}
            label={t('forms.choose_users_group')}
            options={list}
            onChange={onChange}
            value={values}
            placeholder={t("forms.users_group")}
        />
    )
}
export default UsersGroupDropdown