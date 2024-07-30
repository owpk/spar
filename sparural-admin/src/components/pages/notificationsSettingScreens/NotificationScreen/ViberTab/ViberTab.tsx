import { FC, useCallback, useContext } from 'react'
import { useTranslation } from 'react-i18next'
import { useNavigate } from 'react-router-dom'
import { Routes } from '../../../../../config'
import { NotificationSortType } from '../../../../../types'
import { MainTable } from '../../../../complexes/MainTable'
import { ColumnType } from '../../../../complexes/MainTable/TableBody'
import { DeleteTemplateContext } from '../contexts'
import { DataTable } from '../NotificationScreen'

type Props = {
    data: Array<DataTable>
}

const ViberTab: FC<Props> = ({ data }) => {
    const onDelete = useContext(DeleteTemplateContext)
    const { t } = useTranslation()
    const navigate = useNavigate()

    const columns: Array<ColumnType> = [
        {
            key: 'name',
            title: t('common.name'),
            width: 2,
        },
        {
            key: 'trigger',
            title: t('table.trigger'),
            width: 1,
        },
        {
            key: 'start_date',
            title: t('table.start_date'),
            width: 1,
        },
        {
            key: 'frenquency',
            title: t('table.frenquency'),
            width: 1,
        },
        {
            key: 'text',
            title: t('table.text'),
            width: 3,
        },
        {
            key: 'recipients',
            title: t('table.recipients'),
            width: 1.5,
        },
    ]

    const goToEdit = useCallback(
        (id: number): void => {
            navigate(
                `${Routes.NOTIFICATIONS_EDIT}?${NotificationSortType.VIBER}=${id}`
            )
        },
        [navigate]
    )

    return (
        <div className="">
            <MainTable
                columns={columns}
                data={data}
                onEdit={goToEdit}
                onDelete={onDelete}
            />
        </div>
    )
}

export default ViberTab
