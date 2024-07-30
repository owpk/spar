import { message } from 'antd'
import { FC, useCallback, useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useNavigate } from 'react-router-dom'
import { Routes } from '../../../../config'
import { useAppDispatch } from '../../../../hooks/store'
import { OutsideDocsService } from '../../../../services/OutsideDocsService'
import { DeleteModal } from '../../../complexes/DeleteModal'
import { MainLayout } from '../../../complexes/MainLayout'
import { MainTable } from '../../../complexes/MainTable'
import { ColumnType } from '../../../complexes/MainTable/TableBody'


type DataExternalType = {
    id: number,
    alias: string,
    doc_name: string,
    link: string
}

const OutsideDocsScreen: FC = () => {
    const navigate = useNavigate()
    const { t } = useTranslation()

    const [del, setDel] = useState<number>(0)

    const [list, setList] = useState<Array<DataExternalType>>([])
    const [loading, setLoading] = useState(false)
    const offset = useRef(0)
    const has = useRef(true)

    const goToEdit = (id: number) => {
        const idAlias = list.find(i => i.id === id)?.alias
        navigate(`${Routes.OUTSIDE_DOCS_EDIT_SCREEN}?id=${idAlias}`)
    }
    const onCreateNew = () => {
        navigate(Routes.OUTSIDE_DOCS_CREATE_SCREEN)
    }

    const load = async () => {
        if (!has.current || loading) {
            return
        }

        setLoading(true)
        try {
            const result = await OutsideDocsService.getOutsideDocs({
                offset: offset.current,
            })
            if (!result.length) {
                has.current = false
                setLoading(false)
                return
            }
            const rows = result.map((item) => {
                return ({
                    id: item.id,
                    alias: item.alias,
                    doc_name: item.title,
                    link: item.url
                })
            })

            offset.current = offset.current + result.length
            setList([...list, ...rows])
        } catch (error) {
            message.error(t('errors.get_data'))
        }
        setLoading(false)
    }
    useEffect(() => {
        load().then()
    }, [])

    const OnClickDelete = useCallback(async () => {
        const idAlias = list.find(i => i.id === del)?.alias
        try {
            if(idAlias){
                const response = await OutsideDocsService.deleteOutsideDocs(idAlias)

                if(response){
                    message.success(t("suсcess_messages.delete_data"))
                    setDel(0)
                    setList(prev => prev.filter(i => i.id !== del))
                }
            }
        } catch (error) {
            message.error(t('errors.delete_data'))
        }
    },[del, list, t])

    const columns: Array<ColumnType> = [
        {
            key: 'alias',
            title: t('table.alias'),
            width: 2,
        },
        {
            key: 'doc_name',
            title: t('table.doc_name'),
            width: 5,
        },
        {
            key: 'link',
            title: t('table.link'),
            width: 2,
        },
    ]

    return (
        <MainLayout onEndReached={load} isLoading={loading} onAdd={onCreateNew} title={t('screen_title.outside_docs')}>
            <>
                <MainTable
                    columns={columns}
                    data={list}
                    onEdit={goToEdit}
                    onDelete={setDel}
                />
                <DeleteModal
                    onSubmit={OnClickDelete}
                    onCancel={() => setDel(0)}
                    visible={!!del}
                />
            </>
        </MainLayout>
    )
}
export default OutsideDocsScreen
