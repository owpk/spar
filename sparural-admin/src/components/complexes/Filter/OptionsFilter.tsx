import moment, { Moment } from 'moment'
import React, { FC, useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Checkbox } from '../../simples/Checkbox'
import { DatePickerComponent } from '../../simples/DatePickerComponent'
import { TextField } from '../../simples/TextField'
import styles from './Filter.module.scss'
import { ReactComponent as ArrowUp } from '../../../assets/icons/arrow_up.svg'
import { ReactComponent as ArrowDown } from '../../../assets/icons/arrow_down.svg'

export type FilterOptionType = {
    label: string
    value: FilterOption
}

type Props = {
    option: FilterOptionType
    isChecked: boolean
    setActiveCheckbox: (option: FilterOption) => void
    onChangeDate: (date: { start?: number; end?: number }) => void
    onChangeShop: (shop: string) => void
}

export enum FilterOption {
    FULL_NAME_ASC = 'full_name_asc',
    FULL_NAME_DESC = 'full_name_desc',
    RATE_ASC = 'rate_asc',
    RATE_DESC = 'rate_desc',
    DATE = 'date',
    SHOP = 'shop',
}

const OptionsFilter: FC<Props> = ({
    option,
    isChecked,
    setActiveCheckbox,
    onChangeDate,
    onChangeShop,
}) => {
    const { t } = useTranslation()
    const [name, setName] = useState<string>('')

    const [date, setDate] = useState<{
        start?: number
        end?: number
    }>({})

    /**
     * function for disable date after end date
     * @param date - Moment from datePicker
     * @returns boolean
     */
    const onHandleDisebleStartDate = (d?: Moment) => {
        let disable = false
        if (date.end && d) {
            if (moment(d).valueOf() > date.end) {
                disable = true
            } else {
                disable = false
            }
        }
        return disable
    }
    /**
     * function for disable date before start date
     * @param date - Moment from datePicker
     * @returns boolean
     */
    const onHandleDisebleEndDate = (d?: Moment) => {
        let disable = false
        if (date.start && d) {
            if (moment(d).valueOf() < date.start) {
                disable = true
            } else {
                disable = false
            }
        }
        return disable
    }

    useEffect(() => {
        onChangeDate(date)
    }, [date])

    useEffect(() => {
        onChangeShop(name)
    }, [name])

    return (
        <div className={styles.optionItem}>
            <div className={styles.blockCheckbox}>
                <Checkbox
                    value={option.value}
                    labelPosition={'right'}
                    label={option.label}
                    onClick={(val) => setActiveCheckbox(val as FilterOption)}
                    isChecked={isChecked}
                />
                {option.value === FilterOption.RATE_ASC && (
                    <span className={styles.arrows}>
                        (<ArrowUp />
                        <ArrowDown />)
                    </span>
                )}
                {option.value === FilterOption.RATE_DESC && (
                    <span className={styles.arrows}>
                        (<ArrowDown />
                        <ArrowUp />)
                    </span>
                )}
            </div>
            <div className={styles.blockInput}>
                {option.value === FilterOption.DATE && (
                    <div className={styles.Date}>
                        <DatePickerComponent
                            disabled={!isChecked}
                            onDisableDate={onHandleDisebleStartDate}
                            withoutIcon
                            fontSize={16}
                            value={date.start}
                            onChange={(d) => setDate({ ...date, start: d })}
                            placeholder={t('filter.from')}
                        />
                        <DatePickerComponent
                            disabled={!isChecked}
                            onDisableDate={onHandleDisebleEndDate}
                            withoutIcon
                            fontSize={16}
                            value={date.end}
                            onChange={(d) => setDate({ ...date, end: d })}
                            placeholder={t('filter.to')}
                        />{' '}
                    </div>
                )}
                {option.value === FilterOption.SHOP && (
                    <div style={{ width: '100%' }}>
                        <TextField
                            disabled={!isChecked}
                            value={name}
                            onChange={setName}
                            label={''}
                        />
                    </div>
                )}
            </div>
        </div>
    )
}
export default OptionsFilter
