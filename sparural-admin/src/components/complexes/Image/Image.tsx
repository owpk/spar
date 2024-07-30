import { FC, useCallback, useEffect, useRef, useState } from 'react'
import { ReactComponent as EmptyImage } from "../../../assets/icons/emptyImg.svg";
import { useImageURL } from '../../../hooks/useImageURL';
import styles from './Image.module.scss'
type Props = {
    photo: string
    size?: number

}

const Image: FC<Props> = ({ photo, size }) => {

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
                <EmptyImage />
            )}
        </div>
    )
}

export default Image