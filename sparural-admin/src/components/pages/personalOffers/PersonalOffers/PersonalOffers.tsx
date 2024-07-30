import { message } from 'antd'
import produce from 'immer'
import { FC, useCallback, useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useNavigate } from 'react-router-dom'
import { Routes } from '../../../../config'
import { PersonalOffersService } from '../../../../services/PersonalOffersService'
import { PersonalOffersType, Phototype } from '../../../../types'
import { DeleteModal } from '../../../complexes/DeleteModal'
import { MainLayout } from '../../../complexes/MainLayout'
import { MainTable } from '../../../complexes/MainTable'
import { Celltype, ColumnType } from '../../../complexes/MainTable/TableBody'

type Props = {}

type TablePersonalOfferData = {
    id: number
    name_attribut: string
    name: string
    description: string
    photo: string
    preview: string
    button: boolean
}

const PersonalOffers: FC<Props> = () => {
    const navigate = useNavigate()
    const { t } = useTranslation()
    const [del, setDel] = useState<number>(0)
    const [loading, setLoading] = useState(false)
    const offset = useRef(0)

    // list of personal offers
    const [list, setList] = useState<Array<TablePersonalOfferData>>([])
    console.log(list)

    /**
     * data for row table
     */
    const columns: Array<ColumnType> = [
        {
            key: 'name_attribut',
            title: t('forms.name_atribut'),
            width: 2,
        },
        {
            key: 'name',
            title: t('forms.name'),
            width: 2,
        },
        {
            key: 'description',
            title: t('forms.description'),
            width: 3,
        },
        {
            key: 'preview',
            title: t('forms.preview'),
            type: Celltype.IMAGE,
            width: 1.5,
        },
        {
            key: 'photo',
            title: t('forms.iz-nei'),
            type: Celltype.IMAGE,
            width: 1.5,
        },
        {
            key: 'button',
            title: t('common.button'),
            width: 2,
            type: Celltype.BUTTON,
        },
    ]

    /**
     * fetch personals offers
     */
    const load = async () => {
        setLoading(true)
        try {
            const result = await PersonalOffersService.getPersonalOffers({
                offset: offset.current,
            })

            offset.current = offset.current + result.length
            const rows = result.map((item: PersonalOffersType) => {
                return {
                    id: item.id,
                    name_attribut: item?.attribute || '',
                    name: item?.title || '',
                    description: item?.description || '',
                    preview: item?.preview?.uuid || '',
                    photo: item?.photo?.uuid || '',
                    button: item?.isPublic || false,
                }
            })
            setList([...list, ...rows])
        } catch (error) {
            message.error(t('errors.get_data'))
        }
        setLoading(false)
    }

    useEffect(() => {
        load().then()
    }, [])

    /**
     * navigate to edit screen
     * @param id - offer id
     */
    const goToEdit = (id: number) => {
        navigate(`${Routes.PERSONAL_OFFERS_EDIT}?id=${id}`)
    }

    /**
     * navigate to create screen
     */
    const onCreateNew = () => {
        navigate(Routes.PERSONAL_OFFERS_CREATE)
    }

    /**
     * delete offer
     */
    const onDeleteScreen = useCallback(async () => {
        try {
            const response = await PersonalOffersService.deletePersonalOffers(
                del
            )

            if (response) {
                message.success(t('suсcess_messages.delete_data'))
                setList((prev) => prev.filter((i) => i.id !== del))
                setDel(0)
            } else {
                message.error(t('errors.delete_data'))
            }
        } catch (error) {
            message.error(t('errors.delete_data'))
        }
    }, [del, t])

    /**
     * togle public status
     */
    const onHandlePublic = useCallback(async (id: number, pub: boolean) => {
        try {
            const response = await PersonalOffersService.updatePersonalOffers(
                id,
                {
                    isPublic: pub,
                }
            )
            setList(
                produce((draft) => {
                    const find = draft.find((i) => i.id === id)
                    console.log(find)
                    if (find) {
                        find.button = response.isPublic
                    }
                })
            )
        } catch (error) {}
    }, [])
    return (
        <MainLayout
            title={t('screen_title.personal_offers')}
            onAdd={onCreateNew}
            isLoading={loading}
            onEndReached={load}
        >
            <>
                <MainTable
                    columns={columns}
                    onEdit={goToEdit}
                    data={list}
                    onDelete={setDel}
                    onBtnClick={onHandlePublic}
                />
                <DeleteModal
                    onSubmit={onDeleteScreen}
                    onCancel={() => setDel(0)}
                    visible={!!del}
                />
            </>
        </MainLayout>
    )
}
export default PersonalOffers
