import React, { FC } from 'react'
import { UserRoleType } from '../../../types'
import styles from './MainTable.module.scss'
import TableBody, { ColumnType } from './TableBody'
import TableHeader from './TableHeader'

export type DataTableType = {
    id: number
    image?: string
    city?: string
    button?: boolean
}
export type DataBannerType = {
    id: number
    banner: string
    name: string
    link: string
    button: boolean
}
export type DataStaticType = {
    id: number
    alias: string
    doc_name: string
}
export type DataUsersType = {
    id: number
    full_name: string
    position: Array<UserRoleType>
    phone_number: string
    email: string
}
export type DataUsersRegisteredType = {
    id: number
    number_sort: number
    full_name: string
    gender: string
    birthday: string
    phone_number: string
    email: string
}
export type DataGroupUsers = {
    id: number
    group_name: string
}
export type DataBlocks = {
    id: string
    name?: string
    order: number
    showCounter: boolean
    showEndDate: boolean
    showPercents: boolean
    showBillet: boolean
}

type Props = {
    columns: Array<ColumnType>
    data: Array<any>
    onEdit: (id: number) => void
    onDelete: (id: number) => void
    onBtnClick?: (id: number, Public: boolean) => void
    onLabel?: string
    offLabel?: string
    isPublic?: boolean
    withoutEdit?: boolean
    onCheckboxClick?: (id: number | string, value: string) => void
    onInputChange?: (
        id: number | string,
        objectKey: string,
        value: string
    ) => void
    onEndInputChange?: () => void
    iconsInfo?: boolean | undefined
    isDelete?: boolean
    isMessage?: boolean
}

/**
 *
 * @param data - data Array
 * if we need show button we add key button (value cans be true or false) in data item and type = 'button' in the columns
 * @param onEdit - callback when we click on pencil
 * @param onDelete - call when we click on delete
 * @param onBtnClick - if we have button calls when we click on button
 * @param columns - array ColumnsType for formotion table
 * @returns table
 */
const MainTable: FC<Props> = ({
    data,
    onEdit,
    onDelete,
    onBtnClick,
    onLabel = 'Опубликовано',
    offLabel = 'Опубликовать',
    isPublic,
    onCheckboxClick,
    columns,
    withoutEdit,
    onInputChange,
    onEndInputChange,
    iconsInfo,
    isDelete = true,
    isMessage = false,
}) => {
    return (
        <div className={styles.tableWrapper}>
            <TableHeader
                withoutEdit={withoutEdit}
                data={columns}
                iconsInfo={iconsInfo}
                isDelete={false}
                isMessage={isMessage}
            />
            <TableBody
                onInputChange={onInputChange}
                onEndInputChange={onEndInputChange}
                onCheckboxClick={onCheckboxClick}
                withoutEdit={withoutEdit}
                columns={columns}
                data={data}
                onEdit={onEdit}
                onDelete={onDelete}
                onBtnClick={onBtnClick}
                isPublic={isPublic}
                iconsInfo={iconsInfo}
                offLabel={offLabel}
                onLabel={onLabel}
                isDelete={isDelete}
                isMessage={isMessage}
            />
        </div>
    )
}

export default React.memo(MainTable)
