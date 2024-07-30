import React, { useCallback, useEffect, useRef, useState } from 'react'
import styles from './FeedBackChat.module.scss'
import { MessageType } from '../../../../types'
import MessageItem from './MessageItem'
import { ReactComponent as ClipFileIcon } from '../../../../assets/icons/clipFile.svg'
import { ReactComponent as SendMessageIcon } from '../../../../assets/icons/sendMessageIcon.svg'
import { Input, message } from 'antd'
import classNames from 'classnames'
import { t } from 'i18next'
import { FeedBackService } from '../../../../services/FeedBackService'
import {
    UploadFileDocType,
    useUploadFileMutation,
} from '../../../../services/FileService'
import { EntitiesFieldName, FileSource } from '../../../../config'

type Props = {
    messagesList: MessageType[]
    chatId: number
    addMessage: (messages: MessageType) => void
}

const Dialog = ({ messagesList, chatId, addMessage }: Props) => {
    const containerRef = useRef<HTMLDivElement>(null)
    const inputRef = useRef<any>()
    const [sendFile, { error }] = useUploadFileMutation()

    const [typingMessageText, setTypingMessageText] = useState('')
    const [fileUrl, setFileUrl] = useState('')
    const [file, setFile] = useState<File>()

    useEffect(() => {
        const container = containerRef.current
        if (container) {
            container.scrollTop =
                container.scrollHeight - container.clientHeight
        }
    }, [typingMessageText, containerRef, messagesList])

    const uploadFile = useCallback(
        async (file: File, messageId: number) => {
            const sendData: UploadFileDocType = {
                source: FileSource.REQUEST,
                'source-parameters': JSON.stringify({}),
                entities: [
                    {
                        field: EntitiesFieldName.SUPPORT_CHATS_MESSAGE_FILE,
                        documentId: messageId ? messageId : 0,
                    },
                ],
                file: file,
            }
            await sendFile(sendData)
        },
        [sendFile]
    )

    const onAddFile = useCallback(
        async (event: React.ChangeEvent<HTMLInputElement>) => {
            if (event.target.files && event.target.files[0]) {
                if (event.target.files[0].size > 10 * 1024 * 1024) {
                    message.error(`${t('errors.file_size')}`)
                    return
                }
                // imageRef.current.src = URL.createObjectURL(event.target.files[0])
                setFileUrl(URL.createObjectURL(event.target.files[0]))
                setFile(event.target.files[0])
                setTypingMessageText(event.target.files[0].name)
            }
        },
        []
    )

    const onSendTextMessage = useCallback(async () => {
        try {
            const response = await FeedBackService.sendChatMessage(
                {
                    messageType: 'text',
                    draft: false,
                    text: typingMessageText,
                },
                chatId
            )

            if (response.success) {
                addMessage(response.data)
                setTypingMessageText('')
            } else {
                message.error(t('errors.send_message'))
            }
        } catch (error) {
            message.error(t('errors.send_message'))
        }
    }, [typingMessageText, chatId])

    const onSendFileMessage = useCallback(async () => {
        console.log('1')
        await FeedBackService.sendChatMessage(
            {
                messageType: 'file',
                draft: true,
                text: '',
            },
            chatId
        )
            .then(async (response) => {
                console.log('2')
                if (response.success && file) {
                    await uploadFile(file, response.data.id).then(async () => {
                        console.log('3')
                        await FeedBackService.updateChatMessage(
                            {
                                messageType: 'file',
                                draft: false,
                            },
                            chatId,
                            response.data.id
                        ).then((response) => {
                            console.log('4')
                            addMessage(response.data)
                        })
                    })
                }
            })
            .finally(() => {
                console.log('5')
                setFile(undefined)
                setTypingMessageText('')
            })
    }, [addMessage, chatId, file, uploadFile])

    const handleFileSelect = () => {
        inputRef.current.click()
    }

    return (
        <div className={styles.dialog}>
            <div className={styles.dialog__messagesBlock} ref={containerRef}>
                {messagesList.map((message, index) => (
                    <MessageItem
                        message={message}
                        key={`${message.id}${index}`}
                    />
                ))}
            </div>
            <div className={styles.dialog__controleBlock}>
                <button
                    onClick={handleFileSelect}
                    className={styles.dialog__fileBtn}
                >
                    <ClipFileIcon />
                    <input
                        style={{
                            width: 0,
                            height: 0,
                        }}
                        ref={inputRef}
                        type={'file'}
                        onChange={onAddFile}
                    />
                </button>
                <Input
                    className={styles.dialog__messageInput}
                    disabled={!!file}
                    size="large"
                    placeholder="Текст сообщения"
                    value={typingMessageText}
                    onChange={(e) => setTypingMessageText(e.target.value)}
                />
                <button
                    onClick={file ? onSendFileMessage : onSendTextMessage}
                    className={styles.dialog__fileBtn}
                    disabled={typingMessageText === ''}
                >
                    <SendMessageIcon
                        className={classNames(
                            styles.dialog__sendMessageIcon,
                            typingMessageText !== '' &&
                                styles.dialog__sendMessageIconFilled
                        )}
                    />
                </button>
            </div>
        </div>
    )
}

export default Dialog
