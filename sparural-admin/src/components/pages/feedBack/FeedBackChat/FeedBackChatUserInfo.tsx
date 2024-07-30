import React from 'react'
import { UserCardType, UserType } from '../../../../types'
import styles from './FeedBackChat.module.scss'
import { t } from 'i18next'
import NoAvatar from '../../../../assets/icons/noavatar.png'

type Props = {
    user: UserType
    cards: UserCardType[]
}

const FeedBackChatUserInfo = ({ user, cards }: Props) => {
    return (
        <div className={styles.FeedBackChatUserInfo__wrapper}>
            <div className={styles.FeedBackChatUserInfo__leftBlock}>
                <div
                    className={styles.FeedBackChatUserInfo__avatarBlock}
                    style={{
                        backgroundImage: user?.photo?.url
                            ? `url(${user.photo.url})`
                            : `url(${NoAvatar})`,
                    }}
                />
                <div className={styles.FeedBackChatUserInfo__infoBlock}>
                    <div
                        className={styles.FeedBackChatUserInfo__infoBlock_item}
                    >
                        <span
                            className={
                                styles.FeedBackChatUserInfo__infoBlock_subtitle
                            }
                        >
                            {t('table.user')}
                        </span>
                        <span
                            className={
                                styles.FeedBackChatUserInfo__infoBlock_value
                            }
                        >
                            {user.firstName} {user.lastName}
                        </span>
                    </div>
                    <div
                        className={styles.FeedBackChatUserInfo__infoBlock_item}
                    >
                        <span
                            className={
                                styles.FeedBackChatUserInfo__infoBlock_subtitle
                            }
                        >
                            {t('common.mail')}
                        </span>
                        <span
                            className={
                                styles.FeedBackChatUserInfo__infoBlock_value
                            }
                        >
                            {user.email}
                        </span>
                    </div>
                    <div
                        className={styles.FeedBackChatUserInfo__infoBlock_item}
                    >
                        <span
                            className={
                                styles.FeedBackChatUserInfo__infoBlock_subtitle
                            }
                        >
                            {t('table.phone')}
                        </span>
                        <span
                            className={
                                styles.FeedBackChatUserInfo__infoBlock_value
                            }
                        >
                            {user.phoneNumber}
                        </span>
                    </div>
                    <div
                        className={styles.FeedBackChatUserInfo__infoBlock_item}
                    >
                        <span
                            className={
                                styles.FeedBackChatUserInfo__infoBlock_subtitle
                            }
                        >
                            {t('table.bonus_card')}
                        </span>
                        <span
                            className={
                                styles.FeedBackChatUserInfo__infoBlock_value
                            }
                        >
                            {
                                cards?.find(
                                    (card: UserCardType) =>
                                        card.imOwner === true
                                )?.number
                            }
                        </span>
                    </div>
                </div>
            </div>
            <div className={styles.FeedBackChatUserInfo__rightBlock}>
                <a
                    href={`/registred-users-screen/edit?userId=${user.id}`}
                    className={styles.FeedBackChatUserInfo__rightBlock_link}
                >
                    {t('table.go_to_profile')}
                </a>
                <div
                    className={
                        styles.FeedBackChatUserInfo__rightBlock_deviceBlock
                    }
                >
                    <span
                        className={
                            styles.FeedBackChatUserInfo__rightBlock_deviceInfo
                        }
                    >
                        ОС: {user.device?.data ?? '---'}
                    </span>
                    <span
                        className={
                            styles.FeedBackChatUserInfo__rightBlock_deviceInfo
                        }
                    >
                        Модель: {user.device?.identifier ?? '---'}
                    </span>
                </div>
            </div>
        </div>
    )
}

export default FeedBackChatUserInfo
