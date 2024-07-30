import React, { FC } from 'react'
import { useTranslation } from 'react-i18next'
import styles from './MainTable.module.scss'
import classNames from 'classnames'

type Props = {
    data: Array<{
        title: string
        width: number
    }>
    withoutEdit?: boolean
    iconsInfo: boolean | undefined
    isDelete?: boolean
    isMessage?: boolean
}

/**
 * @data - Array of object {title, width} where title - column title and width - column width
 * @returns - header for table
 */

const TableHeader: FC<Props> = ({
    data,
    withoutEdit,
    iconsInfo,
    isDelete = true,
    isMessage = false,
}) => {
    const { t } = useTranslation()
    return (
        <div className={styles.tableHeaderWrapper}>
            {data?.map((item, index) => (
                <div
                    style={{
                        flex: item.width,
                        textAlign: !index ? 'start' : 'center',
                    }}
                    className={styles.tableHeaderLabel}
                    key={index}
                >
                    {item.title}
                </div>
            ))}

            {!withoutEdit && (
                <div
                    className={classNames(
                        styles.editBlock,
                        isMessage && styles.noFlex
                    )}
                >
                    <span
                        style={{ flex: 1 }}
                        className={styles.tableHeaderLabel}
                    >
                        {iconsInfo
                            ? t('common.in-cia')
                            : isMessage
                            ? t('table.indication')
                            : t('common.edit')}
                    </span>
                    {isDelete && (
                        <span
                            style={{ flex: 1 }}
                            className={styles.tableHeaderLabel}
                        >
                            {t('common.delete')}
                        </span>
                    )}
                </div>
            )}
        </div>
    )
}

export default React.memo(TableHeader)
