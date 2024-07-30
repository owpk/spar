import React, { FC } from 'react'
import styles from './Table.module.scss'
import { RouteProps } from 'react-router-dom'
import { BlockWrapper } from '../BlockWrapper'

type Props = RouteProps

const TableRow:FC<Props> =({children}) => {
   return (
    <BlockWrapper>
        <div className={styles.row}>{children}</div>
    </BlockWrapper>
   )
}

export default TableRow