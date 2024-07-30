import React, { FC, ReactComponentElement, ReactElement } from 'react'
import style from './InputHolder.module.scss'
import { RouteProps } from 'react-router-dom'
import classNames from 'classnames'

type Props = {
    alert?: boolean
    children?: ReactElement
    classes?: string
}
const InputHolder: FC<Props> = ({ children, alert, classes }) => {
    return (
        <div
            className={classNames(style.root, classes)}
            style={
                alert
                    ? {
                          border: '1px solid red',
                          paddingLeft: '10px',
                          borderRadius: '5px',
                          paddingBottom: '15px',
                      }
                    : {}
            }
        >
            {children}
        </div>
    )
}

export default React.memo(InputHolder)
