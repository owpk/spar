import classNames from 'classnames'
import { Field } from 'formik'
import React, { FC, useState } from 'react'
import styles from './FormField.module.scss'
import { ReactComponent as Eye } from '../../../assets/icons/eye.svg'
import { ReactComponent as EyeClose } from '../../../assets/icons/close_eye.svg'
import { ReactComponent as Error } from '../../../assets/icons/error.svg'
import { ReactComponent as Ok } from '../../../assets/icons/ok.svg'

export enum ValidStatus {
    VALID = 'valid',
    NOT_VALID = 'not_valid',
    EMPTY = 'empty'
}

type Props = {
    id?: string
    label: string
    name?: string
    placeholder?: string
    classes?: {
        label?: string
        input?: string
    }
    isSecure?: boolean
    validate?: (val: string) => string
    error?: string
    isValide?: ValidStatus

}

const FormField: FC<Props> = ({
    label,
    name,
    id,
    placeholder,
    classes,
    isSecure = false,
    validate,
    isValide = ValidStatus.EMPTY,
    error }) => {
    const [pass, setPass] = useState<boolean>(isSecure)
    return (
        <>
            <label className={classNames(styles.label, classes?.label)} htmlFor={id}>{label}</label>
            <div className={styles.root}>
                <div className={styles.wrapper}>
                    <Field
                        validate={validate}
                        type={pass ? 'password' : 'text'}
                        className={classNames(styles.input, classes?.input)}
                        id={id}
                        name={name}
                        placeholder={placeholder}
                    />
                    {(isValide && isValide !== ValidStatus.EMPTY) &&
                        <div className={styles.icon}>
                            {!!error ?
                                <Error /> :
                                <Ok />
                            }
                        </div>}


                </div>
                {isSecure &&
                    <div
                        className={styles.iconEye}
                        onClick={() => setPass(!pass)}
                    >
                        {!pass ? <Eye /> : <EyeClose />}
                    </div>
                }
            </div>
            {!!error && <div className={styles.error}>{error}</div>}
        </>
    )
}

export default FormField