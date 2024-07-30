import { TimePicker } from 'antd'
import LocaleProvider from 'antd/lib/locale-provider'
import moment, { Moment } from 'moment'
import React, { FC, useState } from 'react'
import { ReactComponent as Chevron } from '../../../assets/icons/chevron.svg'
import style from './TimePickerComponent.module.scss'
import 'moment/locale/ru'
import locale from 'antd/lib/locale/ru_RU'
import classNames from 'classnames'

type Props = {
    onChange: (dayestamp: number | undefined) => void
    value?: number
    label?: string
    placeholder?: string
    onDisableDate?: (date?: Moment) => boolean
    error?: string
}

/**
 *
 */
const TimePickerComponent: FC<Props> = ({
    onChange,
    value,
    label,
    placeholder,
    onDisableDate,
    error
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
        <div className="">
            <LocaleProvider locale={locale}>
                {!!label && <div className={style.label}>{label}</div>}

                <TimePicker
                    onOpenChange={() => setOpen(!open)}
                    showNow={false}
                    placeholder={placeholder}
                    className={classNames(style.input,{
                        [style.error]: error
                    })}
                    value={!!value ? moment(value) : undefined}
                    format={'HH:mm'}
                    suffixIcon={
                        <div
                            style={{
                                transform: `rotate(${open ? '180deg' : 0})`,
                            }}
                        >
                            <Chevron className={style.chevron} />
                        </div>
                    }
                    style={{
                        borderRadius: 5,
                        fontSize: 18,
                    }}
                    onChange={onHandleChangeDate}
                />
            </LocaleProvider>
            {error && <span className={style.errorText}>{error}</span>}
        </div>
    )
}

export default React.memo(TimePickerComponent)
