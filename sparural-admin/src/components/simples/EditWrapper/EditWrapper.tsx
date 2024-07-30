import React, { FC } from 'react'
import { useTranslation } from 'react-i18next'
import { BlockWrapper } from '../BlockWrapper'
import { Button } from '../Button'
import styles from './EditWrapper.module.scss'

type Props = {
    title: string
    children: JSX.Element
    onSave?: () => void
    disabled?: boolean
}

const EditWrapper: FC<Props> = ({ title, children, onSave, disabled = false }) => {
    const { t } = useTranslation()
    return (
        <BlockWrapper minWidth={1030}>
            <div>
                <div className={styles.root}>
                    <h2 className={styles.title}>{title}</h2>
                    <div className={styles.line}></div>
                </div>
                <div className={styles.content}>{children}</div>
                {onSave && (
                    <div className={styles.button}>
                        <Button
                            disabled={disabled}
                            onClick={onSave}
                            label={t('common.save')}
                            textUp={'capitalize'}
                            colorText={'#ffffff'}
                            backgroundColor={'#007C45'}
                        />
                    </div>
                )}
            </div>
        </BlockWrapper>
    )
}

export default React.memo(EditWrapper)
