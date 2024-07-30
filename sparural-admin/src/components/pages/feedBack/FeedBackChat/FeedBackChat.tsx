import { FC, useCallback, useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { MainLayout } from '../../../complexes/MainLayout'
import { EditWrapper } from '../../../simples/EditWrapper'
import styles from './FeedBackChat.module.scss'
import { useLocation } from 'react-router-dom'
import { FeedBackService } from '../../../../services/FeedBackService'
import { message } from 'antd'
import { FeedbackChatsList, MessageType } from '../../../../types'
import FeedBackChatUserInfo from './FeedBackChatUserInfo'
import Dialog from './Dialog'
import { Loader } from '../../../simples/Loader'
import { isObjectEmpty } from './MessageItem'

type Props = {}

const FeedBackChat: FC<Props> = () => {
    const { t } = useTranslation()
    const id = useLocation().search.split('=')[1]
    const limit = 1000
    const offset = useRef(0)
    const has = useRef(true)

    const [loading, setLoading] = useState<boolean>(false)
    const [chatData, setChatData] = useState<FeedbackChatsList>()
    const [messagesList, setMessagesList] = useState<MessageType[]>([])

    const addMessage = (message: MessageType) => {
        setMessagesList((prev) => [...prev, message])
    }

    const getOneScreenById = useCallback(async () => {
        try {
            setLoading(true)
            const response = await FeedBackService.getChat(Number(id))
            setChatData(response)
            setLoading(false)
        } catch (error) {
            message.error(t('errors.get_data'))
            setLoading(false)
        }
    }, [id, t])

    const loadMessages = async () => {
        if (!has.current || loading) {
            return
        }

        setLoading(true)
        offset.current = messagesList.length

        try {
            const result = await FeedBackService.getChatMessages({
                id: +id,
                offset: offset.current,
                limit: limit,
            })

            setMessagesList((prev: any) => [...prev, ...result])
        } catch (error) {
            message.error(t('errors.get_data'))
        }
        setLoading(false)
    }

    useEffect(() => {
        if (!!id) {
            getOneScreenById().then()
            loadMessages().then()
        }
    }, [id])

    useEffect(() => {
        const unreadMessagesId: number[] = messagesList
            .filter((message) => !isObjectEmpty(message.sender))
            .map((item) => item.id)
        const data = {
            messagesIds: Array.from(new Set(unreadMessagesId)),
        }
        if (id && unreadMessagesId.length) {
            FeedBackService.readMessages(data, +id)
        }
    }, [id, messagesList])

    return (
        <MainLayout title={t('screen_title.dialog')}>
            <EditWrapper title={t('forms.user_chat').toUpperCase()}>
                <div className={styles.feedBackChat__wrapper}>
                    {chatData ? (
                        <>
                            <FeedBackChatUserInfo
                                user={chatData.user.user}
                                cards={chatData.user.card}
                            />
                            <Dialog
                                messagesList={messagesList}
                                chatId={+id}
                                addMessage={addMessage}
                            />
                        </>
                    ) : (
                        <Loader />
                    )}
                </div>
            </EditWrapper>
        </MainLayout>
    )
}
export default FeedBackChat
