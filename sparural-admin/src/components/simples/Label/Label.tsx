import React, { FC } from 'react'
import { RouteProps } from 'react-router-dom'
import style from './Label.module.scss'

type Props = RouteProps

const Label:FC <Props> = ({children}) => {
    return (
        <div className={style.label}>{children}</div>
    )
}

export default React.memo(Label)