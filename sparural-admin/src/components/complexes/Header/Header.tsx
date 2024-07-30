import React, { FC, useCallback } from 'react'
import styles from './Header.module.scss'
import {ReactComponent as Logo} from '../../../assets/icons/Logo.svg'
import {ReactComponent as Exit} from '../../../assets/icons/exit.svg'
import { useAppDispatch } from '../../../hooks/store'
import { logout } from '../../../store/slices/authSlice'


const Header:FC = () => {
    const dispatch = useAppDispatch()

    const onHandleExit = useCallback( async () => {
      await  dispatch(logout())
    },[dispatch])
    return (
        <div className={styles.root}>
            <div className={styles.logo}>
                <Logo />
            </div>
            <div className={styles.infoBlock}>
                {/* <div className={styles.menuItem}>
                    <Search />
                </div>
                <div className={styles.menuItem}>
                    <Question />
                </div>
                <div className={styles.menuItem}>
                    <div className={styles.alarmLabel}>
                        <span>2</span>
                    </div>
                    <Alarm />
                </div> */}
                <div 
                onClick={onHandleExit}
                className={styles.menuItem}>
                    <Exit />
                </div>
            </div>
        </div>
    )
}

export default React.memo(Header)