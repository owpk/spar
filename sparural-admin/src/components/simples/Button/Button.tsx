import classNames from 'classnames'
import React, { FC } from 'react'
import styles from './Button.module.scss'
import { ReactComponent as ArrowIcon } from '../../../assets/icons/arrow.svg'

export enum ButtonType {
    PRIMARY = 'primary',
    SECOND = 'second',
}

type Props = {
    onClick: () => void
    label: string
    type?: 'submit' | 'button'
    backgroundColor?: '#007C45' | '#ffffff'
    colorText?: '#007C45' | '#ffffff'
    textUp?: 'uppercase' | 'capitalize'
    disabled?: boolean
    typeStyle?: ButtonType
    classes?: {
        root?: string
    }
    isArrow?: boolean
    isArrowUp?: boolean
}
/**
 *
 * @param onClick
 * @param label
 * @param textUp 'uppercase' | 'capitalize'
 * @param colorText "#007C45" | "#ffffff"
 * @param backgroundColor '#007C45' | "#ffffff"
 * @param disabled
 * @param typeStyle
 * @returns
 */

const Button: FC<Props> = ({
    onClick,
    label,
    type,
    backgroundColor,
    colorText,
    textUp = 'capitalize',
    disabled = false,
    typeStyle = ButtonType.PRIMARY,
    classes,
    isArrow = false,
    isArrowUp = false,
}) => {
    return (
        <button
            disabled={disabled}
            type={type}
            className={classNames(
                styles.btn,
                {
                    [styles.disabledBtn]: disabled,
                    [styles.primary]: typeStyle === ButtonType.PRIMARY,
                    [styles.second]: typeStyle === ButtonType.SECOND,
                },
                classes?.root
            )}
            onClick={onClick}
            style={{ background: `${backgroundColor}` }}
        >
            <span
                className={classNames(styles.label, {
                    [styles.primaryLabel]: typeStyle === ButtonType.PRIMARY,
                    [styles.secondLabel]: typeStyle === ButtonType.SECOND,
                })}
                style={{ textTransform: `${textUp}`, color: `${colorText}` }}
            >
                {label}
            </span>
            {isArrow && (
                <ArrowIcon
                    style={{
                        transform: !isArrowUp ? 'rotate(180deg)' : 'none',
                        marginLeft: 12,
                    }}
                />
            )}
        </button>
    )
}

export default Button
