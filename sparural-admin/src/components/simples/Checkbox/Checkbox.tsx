import classNames from 'classnames'
import React, { FC, useCallback } from 'react'
import styles from './Checkbox.module.scss'


type Props = {
    value: number | string
    label?: string
    onClick: (option: number | string) => void
    isChecked: boolean
    labelPosition?: 'left' | 'right'
    upperOne?: boolean
}

const Checkbox: FC<Props> = ({ isChecked, onClick, value, upperOne, label, labelPosition }) => {
    const onHandleClick = useCallback(() => {
        onClick(value)
    }, [value, onClick])
    return (
        <div className={styles.optionItem}>

            <label style={{
                flexDirection: labelPosition === 'right' ? 'row' : 'row-reverse'
            }} className={styles.inputLabel}>
                <>
                    <span className={classNames(styles.checkbox, {
                        [styles.checked]: isChecked
                    })}></span>
                    <input
                        className={styles.input}
                        onChange={onHandleClick}
                        type={'checkbox'}
                        checked={isChecked}
                    />
                </>

                <span
                    style={{
                        marginLeft: labelPosition === 'left' ? 0 : 10,
                        marginRight: labelPosition === 'left' ? 10 : 0,
                        textTransform: upperOne ? 'capitalize' : 'none'
                    }}
                    className={styles.checkboxLabel}>{label}</span>
            </label>
        </div>
    )
}

export default React.memo(Checkbox)