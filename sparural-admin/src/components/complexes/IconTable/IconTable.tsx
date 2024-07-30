import React from 'react';
import { FC } from 'react'
import { useImageURL } from '../../../hooks/useImageURL';
import { ReactComponent as Icon } from "../../../assets/icons/Icon.svg";
import styles from './IconTable.module.scss'

type Props = {
    photo: string
    size?: number
    
}

const IconTable: FC<Props> = ({ photo, size }) => {

    const imageUrl = useImageURL(photo)

    return (
        <div style={{
            width: size ? size + 'px' : undefined,
            height: size ? size + 'px' : undefined,
        }} className={styles.imgBlock}>
            {photo ? (
                <img
                style={{
                    width: size ? size + 'px' : undefined,
                    height: size ? size + 'px' : undefined,
                }}
                src={imageUrl}
                    // ref={imageRef}
                    alt="img"
                    className={styles.image}
                />
            ) : (
                <Icon />
            )}
        </div>
    )
}


export default React.memo(IconTable)