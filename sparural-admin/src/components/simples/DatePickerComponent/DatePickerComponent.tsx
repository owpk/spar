import { DatePicker, Space } from 'antd'
import LocaleProvider from 'antd/lib/locale-provider'
import moment, { Moment } from 'moment'
import React, { FC, useState } from 'react'
import classNames from 'classnames'
import { ReactComponent as Chevron } from '../../../assets/icons/chevron.svg'
import { ReactComponent as CalendarIcon } from '../../../assets/icons/calendar.svg'
import style from './DatePickerComponent.module.scss'
import 'moment/locale/ru'
import locale from 'antd/lib/locale/ru_RU'
import { boolean } from 'yup/lib/locale'

type Props = {
    onChange: (dayestamp: number | undefined) => void
    value?: number
    label?: string
    placeholder?: string
    onDisableDate?: (date?: Moment) => boolean
    fontSize?: number
    withoutIcon?: boolean
    withCalencarIcon?: boolean
    withClearIcon?: boolean
    disabled?: boolean
    classes?: {
        input?: string
        root?: string
        label?: string
    }
    error?: string
}

/**
 *
 */
const DatePickerComponent: FC<Props> = ({
    onChange,
    value,
    label,
    placeholder,
    onDisableDate,
    fontSize = 18,
    withoutIcon = false,
    withCalencarIcon = false,
    withClearIcon = true,
    disabled,
    classes,
                                            error,
}) => {
    const [open, setOpen] = useState<boolean>(false)
    /**
     *
     * @param date - date in moment
     * @param dateString
     *
     */
    const onHandleChangeDate = (date: any, dateString: string) => {
        //get timestamp
        onChange(date ? moment(date).valueOf() : undefined)
    }

    return (
        <div className={classes?.root}>
            <LocaleProvider locale={locale}>
                {!!label && (
                    <div className={`${style.label} ${classes?.label}`}>
                        {label}
                    </div>
                )}

                <DatePicker
                    disabled={disabled}
                    disabledDate={onDisableDate}
                    onOpenChange={() => setOpen(!open)}
                    showNow={false}
                    showToday={false}
                    placeholder={placeholder}
                    className={classNames(style.input, classes?.input, {
                        [style.error]: error
                    })}
                    value={!!value ? moment(value) : undefined}
                    format={'DD.MM.YYYY'}
                    suffixIcon={
                        !withoutIcon ? (
                            <div
                                style={{
                                    transform: `rotate(${open ? '180deg' : 0})`,
                                }}
                            >
                                <Chevron className={style.chevron} />
                            </div>
                        ) : withCalencarIcon ? (
                            <CalendarIcon />
                        ) : null
                    }
                    clearIcon={withClearIcon}
                    style={{
                        borderRadius: 5,
                        fontSize: fontSize,
                    }}
                    onChange={onHandleChangeDate}
                />
            </LocaleProvider>
            {error && <span className={style.errorText}>{error}</span>}
        </div>
    )
}

export default React.memo(DatePickerComponent)
