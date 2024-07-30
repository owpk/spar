import React from 'react'
import { MessageType } from '../../../../types'
import styles from './FeedBackChat.module.scss'
import { ReactComponent as UnreadMessagesIcon } from '../../../../assets/icons/unreadenMessageIcon.svg'
import { ReactComponent as ReadenMessagesIcon } from '../../../../assets/icons/readenMessageIcon.svg'
import { ReactComponent as AgentAvatarSvg } from '../../../../assets/icons/techAgentIcon.svg'
import NoAvatar from '../../../../assets/icons/noavatar.png'
import { formatTime } from '../../../../utils/helpers'

type Props = {
    message: MessageType
}

export function isObjectEmpty(obj: object | null): boolean {
    if (obj === null || Object.keys(obj).length === 0) {
        return true
    } else {
        return false
    }
}

const MessageItem = ({ message }: Props) => {
    return (
        <div
            className={styles.MessageItem}
            style={{
                flexDirection: isObjectEmpty(message?.sender)
                    ? 'row'
                    : 'row-reverse',
            }}
        >
            <div
                className={styles.MessageItem__avatar}
                style={{
                    backgroundImage: isObjectEmpty(message?.sender)
                        ? 'none'
                        : message.sender?.photo?.url
                        ? `url(${message.sender?.photo?.url})`
                        : `url(${NoAvatar})`,
                    backgroundColor: isObjectEmpty(message?.sender)
                        ? '#007C45'
                        : 'transparent',
                }}
            >
                {isObjectEmpty(message?.sender) && (
                    <AgentAvatarSvg className={styles.agentSvg} />
                )}
            </div>
            <div
                className={styles.MessageItem__text}
                style={{
                    background: !isObjectEmpty(message?.sender)
                        ? '#F0F0F0'
                        : '#007C45',
                    color: !isObjectEmpty(message?.sender) ? '#000' : '#fff',
                    borderRadius: 10,
                    borderTopLeftRadius: isObjectEmpty(message?.sender)
                        ? 0
                        : 10,
                    borderTopRightRadius: !isObjectEmpty(message?.sender)
                        ? 0
                        : 10,
                }}
            >
                {message.messageType === 'text'
                    ? message?.text
                    : message?.file?.url}
                <div className={styles.MessageItem__timeAndStatus}>
                    <span className={styles.MessageItem__timeAndStatus_time}>
                        {formatTime(message.createdAt)}
                    </span>
                    {isObjectEmpty(message?.sender) && message?.isRead && (
                        <ReadenMessagesIcon />
                    )}
                    {isObjectEmpty(message?.sender) && !message?.isRead && (
                        <UnreadMessagesIcon />
                    )}
                </div>
            </div>
        </div>
    )
}

export default MessageItem
