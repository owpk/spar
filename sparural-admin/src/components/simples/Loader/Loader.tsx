import React from 'react'
import classNames from 'classnames'

import styles from './Loader.module.scss'

type Props = {
    className?: string
}

const Loader: React.FC<Props> = ({ className }) => (
    <span className={classNames(styles.loader, className)}>
        <div className={styles.loaderInner}>
            <label>●</label>
            <label>●</label>
            <label>●</label>
            <label>●</label>
            <label>●</label>
            <label>●</label>
        </div>
    </span>
)

export default Loader
