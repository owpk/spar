import React, { FC } from 'react'
import styles from './FieldWithLabel.module.scss'

type Props = {
    label?: string
    children: React.ReactNode
}

const FieldWithLabel: FC<Props> = ({ label, children }) => {
    return (
        <div className={styles.root}>
            <label className={styles.label}>{label}</label>
            <div className={styles.info}>{children}</div>
        </div>
    )
}

export default React.memo(FieldWithLabel)