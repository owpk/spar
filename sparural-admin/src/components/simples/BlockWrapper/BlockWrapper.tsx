import React, { FC } from 'react'
import { RoutesProps } from 'react-router-dom'
import styles from './BlockWrapper.module.scss'


type Props = RoutesProps & {
    minWidth?: number
}

/**
 * 
 * @param children - JSX element
 * @returns wrapper for block with shadow
 */
const BlockWrapper:FC<Props> = ({minWidth ,children}) => {
    return (
        <div 
        style={{
            minWidth: minWidth
        }}
        className={styles.wrapper}>
            {children}
        </div>
    )
}

export default React.memo(BlockWrapper);