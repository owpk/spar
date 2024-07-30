import { Input } from 'antd'
import classNames from 'classnames'
import React, { FC, useState } from 'react'
import styles from './TextField.module.scss'
import InputMask from 'react-input-mask'
import { ReactComponent as Eye } from '../../../assets/icons/eye.svg'
import { ReactComponent as EyeClose } from '../../../assets/icons/close_eye.svg'
import { ReactComponent as SearchIcon } from '../../../assets/icons/search_light.svg'
import { Notifiertype } from '../../../types'

type Props = {
    label: string
    placeholder?: string
    onChange: (val: string) => void
    classes?: {
        label?: string
        input?: string
    }
    value: string | number
    isSecure?: boolean
    validate?: (val: string) => string
    error?: string
    isSearch?: boolean
    multyline?: boolean
    maxLength?: number
    disabled?: boolean
    mask?: 'mask'
    notification?: string | number
    readOnly?: boolean
    isNumber?: boolean
}

const TextField: FC<Props> = ({
    label,
    value,
    placeholder,
    classes,
    isSecure = false,
    validate,
    onChange,
    error,
    isSearch = false,
    multyline = false,
    maxLength,
    disabled = false,
    mask,
    notification,
    readOnly,
    isNumber = false,
}) => {
    const [pass, setPass] = useState<boolean>(isSecure)
    return (
        <div
            style={{
                width: '100%',
            }}
        >
            {!!label && (
                <div className={classNames(styles.label, classes?.label)}>
                    {label}
                </div>
            )}
            <div className={styles.root}>
                <div
                    className={classNames(styles.wrapper, {
                        [styles.errorField]: !!error,
                    })}
                >
                    {mask && notification === Notifiertype.PHONE ? (
                        <InputMask
                            mask="+7(999)999-99-99"
                            onChange={(e) => onChange(e.target.value)}
                            value={!!value ? value : ''}
                            disabled={disabled}
                            maxLength={maxLength}
                            multiple={multyline}
                            type={pass ? 'password' : 'text'}
                            className={styles.inputMask}
                            placeholder={placeholder}
                        ></InputMask>
                    ) : (
                        <Input
                            disabled={disabled}
                            maxLength={maxLength}
                            multiple={multyline}
                            onChange={(e) => onChange(e.target.value)}
                            type={
                                pass && !isNumber
                                    ? 'password'
                                    : !pass && isNumber
                                    ? 'number'
                                    : 'text'
                            }
                            className={classNames(
                                styles.input,
                                classes?.input,
                                {
                                    [styles.multyline]: multyline,
                                    [styles.çß]: disabled,
                                }
                            )}
                            value={!!value ? value : ''}
                            placeholder={placeholder}
                            readOnly={readOnly}
                            style={
                                !!readOnly
                                    ? {
                                          color: 'rgba(0,0,0,0.25)',
                                          cursor: 'not-allowed',
                                          background: '#f5f5f5',
                                      }
                                    : {}
                            }
                        />
                    )}

                    {isSearch && (
                        <div className={styles.extraIcon}>
                            <SearchIcon />
                        </div>
                    )}
                    {!!error && <div className={styles.error}>{error}</div>}
                </div>

                {isSecure && (
                    <div
                        className={styles.iconEye}
                        onClick={() => setPass(!pass)}
                    >
                        {!pass ? <Eye /> : <EyeClose />}
                    </div>
                )}
            </div>
        </div>
    )
}

export default TextField
