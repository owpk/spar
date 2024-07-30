import React, { FC } from 'react'
import styles from './AuthLayout.module.scss'
import Title from 'antd/lib/typography/Title';
import { ReactComponent as Logo } from '../../../assets/icons/Logo.svg'
import { useNavigate } from 'react-router-dom';
import { Routes } from '../../../config';


type Props = {
    children: JSX.Element
    title: string
}

/**
 * 
 * @param title 
 * @returns 
 */

const AuthLayout: FC<Props> = ({ children, title }) => {
    const navigate = useNavigate()

    const goHome = () => {
        navigate(Routes.AUTH)
    }
    return (
        <div className={styles.container}>
            <div className={styles.leftSide}>
                <div className={styles.bg}>
                </div>
            </div>
            <div className={styles.rightSide}></div>

            <div className={styles.center}>
                <div className={styles.leftContrast}>
                    <div onClick={goHome} className={styles.logo}>
                        <Logo />
                    </div>
                </div>
                <div className={styles.rightForm}>
                    <div className={styles.form}>
                        <div className={styles.flex}>
                            <Title level={2} className={styles.title}>{title}</Title>
                        </div>
                        {children}
                    </div>
                </div>

            </div>

        </div>
    )
}

export default AuthLayout;