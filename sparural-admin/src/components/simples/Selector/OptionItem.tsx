import { Checkbox } from 'antd'
import classNames from 'classnames'
import React, { FC, useCallback, useMemo, useRef } from 'react'
import StarIcon from '../StarIcon/StarIcon'
import styles from './Selector.module.scss'

export type SelectOption = {
    value: number | string
    label: string
    isStar?: boolean
}

type Props = {
    data: SelectOption
    onClick: (option: SelectOption) => void
    values: Array<SelectOption>
    multiple?: boolean
}

/**
 *
 * @param data
 * @param onClick
 * @param values
 * @returns
 */

const OptionItem: FC<Props> = ({ data, onClick, values, multiple = false }) => {
    const isChecked: boolean = useMemo(() => {
        const find = values.find((i) => i.value === data.value)
        return !!find
    }, [data.value, values])

    const onHandleClick = useCallback(() => {
        onClick(data)
    }, [data, onClick])
    return (
        <div className={styles.optionItem}>
            <label className={styles.inputLabel}>
                {multiple && (
                    <>
                        <span
                            className={classNames(styles.checkbox, {
                                [styles.checked]: isChecked,
                            })}
                        ></span>
                        <input
                            className={styles.input}
                            onChange={onHandleClick}
                            type={'checkbox'}
                            checked={isChecked}
                        />
                    </>
                )}

                <span
                    onClick={() => !multiple && onHandleClick()}
                    className={styles.checkboxLabel}
                >
                    {!data.isStar ? data.label : <StarIcon starCount={Number(data.label)} />}
                </span>
            </label>
        </div>
    )
}

export default React.memo(OptionItem)
