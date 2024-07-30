/* eslint-disable array-callback-return */
import React, { FC, useState } from 'react'
import styles from './MainTable.module.scss'
import { ReactComponent as EditIcon } from '../../../assets/icons/pensil.svg'
import { ReactComponent as DeleteIcon } from '../../../assets/icons/delet.svg'
import { ReactComponent as MessagesIcon } from '../../../assets/icons/messagesIcon.svg'
import { ReactComponent as UnreadMessagesIcon } from '../../../assets/icons/unreadMessagesIcon.svg'
import { ReactComponent as Info } from '../../../assets/icons/info.svg'
import { Button } from '../../simples/Button'
import { ReactComponent as EmptyImage } from '../../../assets/icons/emptyImg.svg'
import { ReactComponent as Icon } from '../../../assets/icons/Icon.svg'
import classNames from 'classnames'
import { Checkbox } from '../../simples/Checkbox'
import Input from './elements/Input'
import TableRow from '../../simples/table/TableRow'
import TableCell from '../../simples/table/TableCell'
import StarIcon from '../../simples/StarIcon/StarIcon'
import { printDate } from '../../../utils/helpers'
import { Image } from '../Image'
import { IconTable } from '../IconTable'

export enum Celltype {
    BUTTON = 'button',
    ICON = 'icon',
    IMAGE = 'image',
    STRING = 'string',
    ARRAY = 'array',
    CHECKBOX = 'checkbox',
    INPUT = 'input',
    RATE = 'rate',
    DATE_SHORT = 'dateshort',
    PLACE_SHOP = 'place_shop',
}

export type ColumnType = {
    title: string
    key: string
    width: number
    type?: Celltype
    objectKey?: string
    isChecked?: boolean
}

type Props = {
    data: Array<any>
    columns: Array<ColumnType>
    onEdit: (id: number) => void
    onDelete: (id: number) => void
    onBtnClick?: (id: number, Public: boolean) => void
    onLabel?: string
    offLabel?: string
    isPublic?: boolean
    onCheckboxClick?: (id: number | string, value: string) => void
    onInputChange?: (
        id: number | string,
        objectKey: string,
        value: string
    ) => void
    onEndInputChange?: () => void
    buttonIsActive?: boolean
    withoutEdit?: boolean
    iconsInfo?: boolean
    isDelete?: boolean
    isMessage?: boolean
}

/**
 *
 * @param data - data
 * @param columns - columns for table header and body
 * @param onEdit -
 * @param onDelete
 * @param onBtnClick
 * @returns - table body
 */
const TableBody: FC<Props> = ({
    data,
    onEdit,
    onDelete,
    onBtnClick,
    onLabel = 'Опубликовано',
    offLabel = 'Опубликовать',
    isPublic = false,
    columns,
    withoutEdit,
    onCheckboxClick,
    onInputChange,
    onEndInputChange,
    iconsInfo,
    isDelete = true,
    isMessage = false,
}) => {
    /**
     * click on button
     * @param id - item
     */

    const onHandleClickBtn = (id: number, Public: boolean) => {
        if (onBtnClick) {
            onBtnClick(id, Public)
        }
    }

    return (
        <div>
            {data.map((item: any, index) => {
                return (
                    <TableRow key={index}>
                        {columns.map((i: ColumnType, index) => {
                            const f = item[i.key]
                            if (f !== 'key' && f !== 'id') {
                                if (i.type === Celltype.IMAGE) {
                                    return (
                                        <TableCell width={i.width} key={i.key}>
                                            <Image photo={item[i.key]} />
                                        </TableCell>
                                    )
                                }
                                if (i.type === Celltype.ICON) {
                                    return (
                                        <TableCell width={i.width} key={i.key}>
                                            <IconTable photo={item[i.key]} />
                                        </TableCell>
                                    )
                                }
                                if (i.type === Celltype.BUTTON) {
                                    return (
                                        <TableCell width={i.width} key={i.key}>
                                            <div className={styles.btnHolder}>
                                                <Button
                                                    backgroundColor={
                                                        (item.button &&
                                                            item.isPublic ===
                                                                undefined) ||
                                                        (item.isPublic !==
                                                            undefined &&
                                                            !item.isPublic)
                                                            ? '#ffffff'
                                                            : '#007C45'
                                                    }
                                                    onClick={() =>
                                                        isPublic
                                                            ? onHandleClickBtn(
                                                                  item.id,
                                                                  !item.isPublic
                                                              )
                                                            : onHandleClickBtn(
                                                                  item.id,
                                                                  !item.button
                                                              )
                                                    }
                                                    label={
                                                        (item.button &&
                                                            item.isPublic ===
                                                                undefined) ||
                                                        (item.isPublic !==
                                                            undefined &&
                                                            !item.isPublic)
                                                            ? onLabel
                                                            : offLabel
                                                    }
                                                    textUp={'capitalize'}
                                                    colorText={
                                                        (item.button &&
                                                            item.isPublic ===
                                                                undefined) ||
                                                        (item.isPublic !==
                                                            undefined &&
                                                            !item.isPublic)
                                                            ? '#007C45'
                                                            : '#ffffff'
                                                    }
                                                />
                                            </div>
                                        </TableCell>
                                    )
                                }
                                if (i.type === Celltype.ARRAY) {
                                    return (
                                        <TableCell width={i.width} key={i.key}>
                                            <div className="">
                                                {item[i.key].map((j: any) => (
                                                    <div
                                                        className={styles.text}
                                                    >
                                                        {i?.objectKey
                                                            ? j[i?.objectKey]
                                                            : j}
                                                    </div>
                                                ))}
                                            </div>
                                        </TableCell>
                                    )
                                }
                                if (
                                    i.type === Celltype.CHECKBOX &&
                                    !!onCheckboxClick
                                ) {
                                    return (
                                        <TableCell
                                            withBorder={
                                                index < columns.length - 1
                                            }
                                            width={i.width}
                                            key={i.key}
                                        >
                                            <Checkbox
                                                value={f}
                                                onClick={() =>
                                                    onCheckboxClick(
                                                        item.id,
                                                        i.key
                                                    )
                                                }
                                                isChecked={item[i.key]}
                                            />
                                        </TableCell>
                                    )
                                }
                                if (
                                    i.type === Celltype.INPUT &&
                                    !!onInputChange
                                ) {
                                    return (
                                        <TableCell
                                            withBorder={
                                                index < columns.length - 1
                                            }
                                            width={i.width}
                                            key={i.key}
                                        >
                                            <Input
                                                value={item[i.key]}
                                                onChange={(val) =>
                                                    onInputChange(
                                                        item.id,
                                                        i.key,
                                                        val
                                                    )
                                                }
                                            />
                                        </TableCell>
                                    )
                                }
                                if (i.type === Celltype.RATE) {
                                    return (
                                        <TableCell width={i.width} key={i.key}>
                                            <div className="">
                                                <StarIcon
                                                    starCount={item[i.type]}
                                                />
                                            </div>
                                        </TableCell>
                                    )
                                }
                                if (i.type === Celltype.DATE_SHORT) {
                                    const str = printDate(item.data)
                                    const one = str.slice(6, str.length)
                                    return (
                                        <TableCell width={i.width} key={i.key}>
                                            <div className={styles.dataShort}>
                                                <div>{str.slice(0, 5)}</div>
                                                <div>{one}</div>
                                            </div>
                                        </TableCell>
                                    )
                                }
                                if (i.type === Celltype.PLACE_SHOP) {
                                    const str = item.shop
                                    const done = str.split(',')

                                    const first = done[0]
                                    const last = done[done.length - 1]
                                    const avrg = str.slice(
                                        first.length + 1,
                                        str.length - last.length
                                    )

                                    return (
                                        <TableCell width={i.width} key={i.key}>
                                            <div className={styles.dataShort}>
                                                <div>{first}</div>
                                                <div>{avrg}</div>
                                                <div>{last}</div>
                                            </div>
                                        </TableCell>
                                    )
                                }
                                return (
                                    <div
                                        key={i.key}
                                        style={{
                                            flex: i.width,
                                            overflow: 'hidden',
                                            whiteSpace: 'nowrap',
                                            padding: 5,
                                            maxWidth: '100%',
                                            textAlign: !index
                                                ? 'start'
                                                : 'center',
                                            justifyContent: !index
                                                ? 'flex-start'
                                                : 'center',
                                        }}
                                        className={styles.rowItemBlock}
                                    >
                                        <div className={styles.textBlock}>
                                            {item[i.key]}
                                        </div>
                                    </div>
                                )
                            }
                        })}

                        {!!!withoutEdit && (
                            <div
                                className={classNames(
                                    styles.editBlockRow,
                                    isMessage && styles.noFlex
                                )}
                            >
                                {!isMessage && (
                                    <div
                                        style={{
                                            flex: 1,
                                        }}
                                        className={classNames(
                                            styles.rowItemBlock
                                        )}
                                    >
                                        <div
                                            onClick={() => onEdit(item.id)}
                                            className={styles.btn}
                                        >
                                            {iconsInfo ? (
                                                <Info />
                                            ) : (
                                                <EditIcon />
                                            )}
                                        </div>
                                    </div>
                                )}
                                {isDelete && (
                                    <div
                                        style={{
                                            flex: 1,
                                        }}
                                        className={classNames(
                                            styles.rowItemBlockLast
                                        )}
                                    >
                                        <div
                                            onClick={() => onDelete(item.id)}
                                            className={styles.btn}
                                        >
                                            <DeleteIcon />
                                        </div>
                                    </div>
                                )}
                                {isMessage && (
                                    <div
                                        style={{
                                            flex: 1,
                                        }}
                                        className={classNames(
                                            styles.rowItemBlock
                                        )}
                                    >
                                        <div
                                            onClick={() => onEdit(item.id)}
                                            className={styles.btn}
                                        >
                                            {!!item?.unreadMessagesCount ? (
                                                <UnreadMessagesIcon />
                                            ) : (
                                                <MessagesIcon />
                                            )}
                                            {item?.unreadMessagesCount > 0 && (
                                                <span
                                                    className={
                                                        styles.messagesCount
                                                    }
                                                >
                                                    {item?.unreadMessagesCount}
                                                </span>
                                            )}
                                        </div>
                                    </div>
                                )}
                            </div>
                        )}
                    </TableRow>
                )
            })}
        </div>
    )
}

export default TableBody
