import { message } from 'antd'
import React, { FC, useCallback, useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useNavigate } from 'react-router-dom'
import { Routes } from '../../../../config'
import { FeedBackService } from '../../../../services/FeedBackService'
import { FeedbackChatsList, FeedbackItemType } from '../../../../types'
import { MainLayout } from '../../../complexes/MainLayout'
import { MainTable } from '../../../complexes/MainTable'
import { ColumnType } from '../../../complexes/MainTable/TableBody'
import { formatDateTime } from '../../../../utils/helpers'
type Props = {}

const testFeedBackData = [
    {
        id: 1,
        userName: 'Alex',
        user: {
            name: 'ahahaha',
        },
        lastMessage: 'Тут типа последнее сообщение',
        lastMessageTime: '17.08.1997 16:20',
        unreadMessagesCount: 8,
    },
    {
        id: 2,
        userName: 'Julia',
        lastMessage: 'Тут типа последнее сообщение',
        lastMessageTime: '17.08.1997 16:20',
        unreadMessagesCount: 0,
    },

    {
        id: 3,
        userName: 'Ivan',
        lastMessage: 'Тут типа последнее сообщение',
        lastMessageTime: '17.08.1997 16:20',
        unreadMessagesCount: 5,
    },
]

type FeedBackChatsTableType = {
    userName: string
    lastMessage: string
    lastMessageTime: string | number
}

const FeedBack: FC<Props> = () => {
    const { t } = useTranslation()
    const navigate = useNavigate()

    const [search, setSearch] = useState<string>('')

    const [list, setList] = useState<Array<FeedBackChatsTableType>>([])
    console.log(list)
    const [loading, setLoading] = useState(false)
    const [isFirstRender, setIsFirstRender] = useState(true)
    const offset = useRef(0)
    const has = useRef(true)
    const feedBackRef = useRef<Array<FeedbackChatsList>>([])

    const columns: Array<ColumnType> = [
        {
            key: 'userName',
            title: t('table.user'),
            width: 1,
        },
        {
            key: 'lastMessage',
            title: t('table.last_message'),
            width: 2,
        },
        {
            key: 'lastMessageTime',
            title: t('table.last_message_date'),
            width: 1,
        },
    ]

    const goToRedact = (id: number) => {
        navigate(`${Routes.FEEDBACK_EDIT}?id=${id}`)
    }

    const load = async (isFilter?: boolean) => {
        if (!has.current || loading) {
            return
        }

        setLoading(true)
        offset.current = list.length

        try {
            const result = await FeedBackService.getChatList({
                offset: offset.current,
                search,
            })
            const rows = result.map((item: FeedbackChatsList) => {
                return {
                    id: item.id,
                    userName: `${item.user.user.lastName} ${item.user.user.firstName}`,
                    lastMessage: item.lastMessage.text,
                    lastMessageTime: item.lastMessage.createdAt
                        ? formatDateTime(item.lastMessage.createdAt)
                        : 'Время не указано',
                    unreadMessagesCount: item.unreadMessagesCount,
                }
            })
            setList((prev: any) => (isFilter ? rows : [...prev, ...rows]))
        } catch (error) {
            message.error(t('errors.get_data'))
        }
        setLoading(false)
    }

    useEffect(() => {
        if (isFirstRender) {
            load().then()
        }
    }, [])

    const clearFunction = async () => {
        setIsFirstRender(false)
        has.current = true
        offset.current = 0
        setList([])
        await load(true)
    }

    useEffect(() => {
        if (!isFirstRender) {
            clearFunction().then()
        }
    }, [search])

    return (
        <MainLayout
            title={t('screen_title.feedback')}
            onSearch={setSearch}
            searchPlaceholder={t('feedbacks.find_feedback')}
            onEndReached={load}
        >
            <>
                <MainTable
                    columns={columns}
                    data={list}
                    onEdit={(id) => goToRedact(id)}
                    onDelete={() => null}
                    withoutEdit={false}
                    isDelete={false}
                    isMessage
                />
            </>
        </MainLayout>
    )
}
export default FeedBack
