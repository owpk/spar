import React, { FC, useMemo } from 'react'
import styles from './DownloadButton.module.scss'
import {ReactComponent as DownloadIcon} from '../.././../assets/icons/dowload.svg'
import { useTranslation } from 'react-i18next'
import classNames from 'classnames'

type Props = {
    label?: string
    onClick: () => void
    disable?: boolean
}

const DownloadButton:FC<Props> = ({label, onClick, disable}) => {
    const { t } = useTranslation()
    const innerLabel: string = useMemo(() => {
        let text = t("common.download_files")
        if(label){
            text = label
        }
        return text
    },[label, t])
    return (
        <div onClick={onClick} className={classNames(styles.root, {
            [styles.disable]: disable
        })}>

            <div className={classNames(styles.label,{
                [styles.disable]: disable
            })}>{innerLabel}</div>
            <DownloadIcon fill={!disable ? '#007C45' : '#e2e2e2' } stroke={!disable ? '#007C45' : '#e2e2e2' } />
        </div>
    )
}

export default React.memo(DownloadButton)