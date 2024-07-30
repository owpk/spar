import React, { FC, useMemo } from 'react'
import styles from './Sidebar.module.scss'
import { useNavigate, useLocation } from 'react-router-dom'
import classNames from 'classnames'
import { Routes } from '../../../config'

export type MenuItemType = {
    route: Routes
    label: string
    icon: JSX.Element
    activRoutes?: Array<Routes>
}
type Props = {
    data: MenuItemType
}

/**
 * @param data - one Menu item Object as MenuItemType
 * @returns - one menu item
 */
const MenuItem: FC<Props> = ({ data }) => {
    const pathname: any = useLocation().pathname
    const navigate = useNavigate()

    /**
     * is active or no item
     */
    const activeItem = useMemo(() => {
        if (
            pathname === data.route ||
            (data.activRoutes && data.activRoutes.includes(pathname))
        ) {
            return true
        } else {
            return false
        }
    }, [pathname, data])

    /**
     * when we click on menu item
     */
    const goToNavigate = () => {
        navigate(data.route)
    }

    return (
        <div
            onClick={goToNavigate}
            className={!activeItem ? styles.menuItem : styles.menuItemActive}
        >
            <div className={classNames(styles.menuItemIcon)}>{data.icon}</div>
            <span className={styles.menuItemLabel}>{data.label}</span>
        </div>
    )
}

export default React.memo(MenuItem)
