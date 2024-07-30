import { FC, useCallback, useEffect, useRef, useState } from 'react'
import { MainLayout } from '../../../complexes/MainLayout'
import { useNavigate } from 'react-router'
import { Routes } from '../../../../config'
import { MainTable } from '../../../complexes/MainTable'
import { useTranslation } from 'react-i18next'
import { DeleteModal } from '../../../complexes/DeleteModal'
import { Celltype, ColumnType } from '../../../complexes/MainTable/TableBody'
import { InfoScreenService } from '../../../../services/InfoScreenService'
import { CitySelectType, CityType, InfoScreenType } from '../../../../types'
import { message } from 'antd'
import { DataTableType } from '../../../complexes/MainTable/MainTable'
import produce from 'immer'

type Props = {
    header?: Array<string>
}

const InfoScreen: FC<Props> = ({ header }) => {
    const navigate = useNavigate()
    const { t } = useTranslation()

    const [list, setList] = useState<Array<DataTableType>>([])
    const [loading, setLoading] = useState(false)
    const offset = useRef(0)
    const has = useRef(true)
    const InfoRef = useRef<Array<InfoScreenType>>([])

    const [del, setDel] = useState<number>(0)
    const goToRedact = (id: number) => {
        navigate(`${Routes.INFO_SCREEN_REDACT}?id=${id}`)
    }
    const onCreateNew = () => {
        navigate(Routes.INFO_SCREEN_ADD)
    }

    const cities = (data: Array<CityType>) => {
        const val: string = data.map((i) => i.name).join(' / ')
        return val
    }

    const ctitesTitile = (data: string) => {
        if (data === CitySelectType.NOWHERE) {
            return 'Нигде'
        } else {
            return 'Все города'
        }
    }

    const load = async () => {
        if (!has.current || loading) {
            return
        }

        setLoading(true)
        try {
            const result = await InfoScreenService.getScreens({
                offset: offset.current,
            })
            if (!result.length) {
                has.current = false
                setLoading(false)
                return
            }
            try {
                const rows = result
                    .map((item) => {
                        return {
                            id: item.id,
                            image: item.photo?.uuid || '',
                            button: !!item.isPublic,
                            city: item.cities
                                ? cities(item.cities)
                                : ctitesTitile(item.citySelect),
                        }
                    })
                    // времено
                    // .sort((a, b) => b.id - a.id)

                setList([...list, ...rows])
                InfoRef.current = [...InfoRef.current, ...result]
            } catch (error) {
                message.error('wrong data')
            }

            offset.current = offset.current + result.length
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

    const columns: Array<ColumnType> = [
        {
            key: 'image',
            title: t('common.image'),
            type: Celltype.IMAGE,
            width: 1,
        },
        {
            key: 'city',
            title: t('common.city'),
            width: 5,
        },
        {
            key: 'button',
            title: t('common.button'),
            width: 2,
            type: Celltype.BUTTON,
        },
    ]

    const onDeleteScreen = useCallback(async () => {
        const response = await InfoScreenService.deleteScreen(del)
        if (response) {
            setList((prev) => prev.filter((i) => i.id !== del))
            setDel(0)
        } else {
            message.error(t('errors.delete'))
        }
    }, [del, t])

    /**
     * publish infoscreen
     */

    const onPublish = useCallback(
        async (id: number, Public: boolean) => {
            try {
                const updateInfo = InfoRef.current.find((i) => i.id === id)
                if (updateInfo) {
                    const response = await InfoScreenService.updateScreen(id, {
                        ...updateInfo,
                        isPublic: Public,
                    })
                    setList(
                        produce((draft) => {
                            const find = draft.find((i) => i.id === id)
                            if (find) {
                                find.button = !find.button
                            }
                        })
                    )
                }
            } catch (error) {
                message.warning(t('errors.update_data'))
            }
        },
        [t, InfoRef]
    )

    return (
        <MainLayout
            onEndReached={handleEndReached}
            onAdd={onCreateNew}
            isLoading={loading}
            title={t('screen_title.info_screen')}
        >
            <>
                <DeleteModal
                    onSubmit={onDeleteScreen}
                    onCancel={() => setDel(0)}
                    visible={!!del}
                />
                <MainTable
                    columns={columns}
                    data={list}
                    onEdit={goToRedact}
                    onDelete={setDel}
                    onBtnClick={onPublish}
                />
            </>
        </MainLayout>
    )
}

export default InfoScreen
