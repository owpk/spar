import React, { FC, useEffect, useState } from 'react'
import styles from '../MainTable.module.scss'
import { ReactComponent as EditIcon } from '../../../../assets/icons/pensil.svg'
import { ReactComponent as CloseIcon } from '../../../../assets/icons/close.svg'
import { ReactComponent as CheckMark } from '../../../../assets/icons/CheckMark.svg'
import classNames from 'classnames'

type Props = {
    value: string
    onChange: (value: string) => void
}

const Input: FC<Props> = ({ onChange, value }) => {
    const [currentValue, setCurrentValue] = useState<string>(value)
    const [onEdit, setOnEdit] = useState<boolean>(false)

    useEffect(() => {
        if (!onEdit) {
            onChange(currentValue)
        }
    }, [currentValue, onEdit])

    return (
        <div className={styles.inputWrapper}>
            <input
                className={classNames(styles.input, {
                    [styles.inputEditor]: !onEdit,
                })}
                disabled={!onEdit}
                value={currentValue}
                onChange={(e) => setCurrentValue(e.target.value)}
            />
            <div
                onClick={() => setOnEdit(!onEdit)}
                className={styles.iconInput}
            >
                {!onEdit ? <EditIcon /> : <CheckMark />}
            </div>
        </div>
    )
}

export default React.memo(Input)
