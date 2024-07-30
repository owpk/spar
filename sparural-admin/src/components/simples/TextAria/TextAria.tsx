import { Input } from 'antd'
import classNames from 'classnames'
import { FC, useState } from 'react'
import styles from './TextAria.module.scss'

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
    multyline?: boolean
    maxLength?: number
    height?: number
    maxRows?: number
    maxCount?: number
    minRows?: number
}

const TextAria: FC<Props> = ({
    label,
    value,
    placeholder,
    classes,
    validate,
    onChange,
    error,
    maxLength, 
    height,
    maxRows,
    maxCount,
    minRows
}) => {

    const onHAndleChange = (e: string) => {
        if((maxCount && e.length < maxCount + 1) || !maxCount){
            onChange(e)
        } else {
            return
        }
        
    }
    return (
        <div style={{
            width: '100%',
            position: 'relative'
        }}>
            <div className={classNames(styles.label, classes?.label)}>
                {label}
            </div>
            <div className={styles.root}>
                <div className={classNames(styles.wrapper, {
                    [styles.errorField]: !!error
                })}>
                    <Input.TextArea
                        autoSize={{
                            minRows: minRows ? minRows : undefined,
                            maxRows: maxRows || 6
                        }}
                        style={{
                            height: height,
                            minHeight: height,
                            maxHeight: height
                        }}
                    maxLength={maxLength}
                        onChange={(e) => onHAndleChange(e.target.value)}
                        className={classNames(styles.input, classes?.input)}
                        value={!!value ? value : ''}
                        placeholder={placeholder}
                    />
                </div>
            </div>
            {!!maxCount && <div className={styles.count}><span className={classNames({
                [styles.currentCount]: String(value).length === maxCount
            })}>{String(value).length}</span>/{maxCount}</div>}
            {!!error && <div className={styles.error}>{error}</div>}
        </div>
    )
}

export default TextAria
