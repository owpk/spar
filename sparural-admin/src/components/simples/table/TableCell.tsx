import React, { FC } from 'react'
import styles from './Table.module.scss'
import { RouteProps } from 'react-router-dom'

type Props = RouteProps & {
    width: number
    withBorder?: boolean
}

const TableCell: FC<Props> = ({ children, width, withBorder = true }) => {
    return (
        <div style={{
            flex: width,
            borderRight: withBorder ? '2px solid $color-grey' : 'none'

        }} className={styles.rowItemBlock}>
            {children}
        </div>
    )
}

export default TableCell