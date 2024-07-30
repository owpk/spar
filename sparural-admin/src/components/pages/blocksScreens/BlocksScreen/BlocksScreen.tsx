import { message } from 'antd'
import produce from 'immer'
import { FC, useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { BlocksService } from '../../../../services/BlocksService'
import { BlockType } from '../../../../types'
import { MainLayout } from '../../../complexes/MainLayout'
import { MainTable } from '../../../complexes/MainTable'
import { DataBlocks } from '../../../complexes/MainTable/MainTable'
import { Celltype, ColumnType } from '../../../complexes/MainTable/TableBody'

const BlocksScreen: FC = () => {
    const { t } = useTranslation()

    const [blocks, setBlocks] = useState<BlockType[]>([])
    const [list, setList] = useState<Array<DataBlocks>>([])
    const [loading, setLoading] = useState(false)
    const offset = useRef(0)
    const has = useRef(true)

    // fetching blocks
    const load = async () => {
        if (!has.current || loading) {
            return
        }

        setLoading(true)
        try {
            const result = await BlocksService.getBlocks({
                offset: offset.current,
            })
            if (!result.length) {
                has.current = false
                setLoading(false)
                return
            }

            const row = result.sort((a, b) => a.order - b.order)
            offset.current = offset.current + result.length
            setBlocks([...row])
        } catch (error: any) {
            message.error(t('errors.get_data'))
        }
        setLoading(false)
    }
    useEffect(() => {
        load().then()
    }, [])

    const handleEndReached = async () => {
        await load()
    }

    // prepare data for table
    useEffect(() => {
        const rows = blocks.map((item) => {
            return {
                id: item.code,
                name: item.name,
                order: item.order,
                showCounter: item.showCounter,
                showEndDate: item.showEndDate,
                showPercents: item.showPercents,
                showBillet: item.showBillet,
            }
        })

        setList(rows)
    }, [blocks])

    const columns: Array<ColumnType> = useMemo(() => {
        return [
            {
                key: 'name',
                title: t('table.block_name'),
                width: 4,
            },
            {
                key: 'order',
                title: t('table.order'),
                type: Celltype.INPUT,
                width: 2,
            },
            {
                key: 'showCounter',
                title: t('table.couter_on_main'),
                width: 2,
                type: Celltype.CHECKBOX,
            },
            {
                key: 'showEndDate',
                title: t('table.end_date'),
                width: 2,
                type: Celltype.CHECKBOX,
            },
            {
                key: 'showPercents',
                title: t('table.percents'),
                width: 1,
                type: Celltype.CHECKBOX,
            },
            {
                key: 'showBillet',
                title: t('table.bill'),
                width: 1,
                type: Celltype.CHECKBOX,
            },
        ]
    }, [t])

    // callback when we changing checkboxes
    const chageCheckbox = useCallback(
        async (blockId: string | number, key: string) => {
            const oneKey = key as keyof BlockType
            let findBlock: any = blocks.find(
                (i: BlockType) => i.code === blockId
            )
            if (findBlock) {
                let sendBlock = {
                    ...findBlock,
                    [oneKey]: !findBlock[oneKey],
                }
                try {
                    await BlocksService.updateBlock(sendBlock)
                } catch (error) {
                    message.warning(t('errors.update_data'))
                    return
                }
            }
            setBlocks(
                produce((draft) => {
                    const oneKey = key as keyof BlockType
                    const find: any =
                        draft.find((i: BlockType) => i.code === blockId) ||
                        ({} as BlockType)
                    let option = find[oneKey]
                    find[key] = !option
                })
            )
        },
        [blocks, t]
    )
    // callback when we changing order
    const onOrderChange = useCallback(
        async (blockId: string | number, key: string, inputValue: string) => {
            const oneKey = key as keyof BlockType
            let findBlock: any = blocks.find(
                (i: BlockType) => i.code === blockId
            )
            if (findBlock) {
                let sendBlock = {
                    ...findBlock,
                    [oneKey]: +inputValue,
                }
                try {
                    await BlocksService.updateBlock(sendBlock)
                } catch (error) {
                    message.warning(t('errors.update_data'))
                    return
                }
            }
            setBlocks(
                produce((draft) => {
                    const find: any =
                        draft.find((i: BlockType) => i.code === blockId) ||
                        ({} as BlockType)
                    find[key] = +inputValue
                })
            )
        },
        [blocks, t]
    )

    return (
        <MainLayout
            onEndReached={handleEndReached}
            title={t('screen_title.blocks')}
        >
            <MainTable
                onInputChange={onOrderChange}
                onCheckboxClick={chageCheckbox}
                withoutEdit
                data={list}
                columns={columns}
                onEdit={() => {}}
                onDelete={() => {}}
            />
        </MainLayout>
    )
}

export default BlocksScreen
