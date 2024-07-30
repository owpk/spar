import React, { FC } from 'react'
import { ReactComponent as CloseIcon } from '../../../assets/icons/close.svg'
import styles from './CloseBtn.module.scss'

type Props = {
    onClick: () => void
}

/**
 * 
 * @param onClick - callback when we ckick in the button
 * @returns circle close btn
 */

const CloseBtn: FC<Props> = ({ onClick }) => {
    return (
        <div
            onClick={onClick}
            className={styles.root}
        >
            <CloseIcon />
        </div>
    )
}

export default React.memo(CloseBtn);