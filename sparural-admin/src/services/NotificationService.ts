import api from '../utils/api'
import { Endpoints } from '../constants'
import {
    NotificationSortType,
    updateNotificationType,
    NotificationType,
    PushActionType,
    NotificationTypesType,
} from '../types'
import { Versions } from '../config'

export class NotificationService {
    /**
     * get notifications list
     */
    static async getNotifications(data: {
        offset?: number
        limit?: number
        messageType: NotificationSortType
    }): Promise<Array<NotificationType>> {
        const { offset = 0, limit = 30, messageType } = data

        const query: Array<string> = []

        query.push(`offset=${offset}`)
        query.push(`limit=${limit}`)
        query.push(`messageType=${messageType}`)

        const response = await api.get(
            `${Endpoints.NOTIFICATIONS}?${query.join('&')}`
        )

        return response.data.data

        // return NotificationsDemo;
    }

    /**
     *
     * get triggers
     *
     */
    static async getTriggerTypes(): Promise<any> {
        const response = await api.get(`${Endpoints.TRIGGERS_TYPE_GET}`)
        return response.data.data
    }
    /**
     *
     * get push actions
     *
     */
    static async getPushActions(): Promise<PushActionType[]> {
        const response = await api.get(`${Endpoints.PUSH_ACTIONS}`)
        return response.data.data
    }

    /**
     *
     * get notifications types
     */
    static async getNotificationsTypes(): Promise<NotificationTypesType[]> {
        const response = await api.get(`${Endpoints.NOTIFICATIONS_TYPES}`)
        return response.data.data
    }
    /**
     * Request for update  notifications list
     */
    static async updateNotifications(
        id: number,
        data: updateNotificationType
    ): Promise<NotificationType> {
        const sendData = {
            data,
            version: Versions.QUESTION_RATING_STORE,
        }

        const response = await api.put(
            `${Endpoints.NOTIFICATIONS}/${id}`,
            sendData
        )
        return response.data.data
    }

    /**
     *
     * @param data
     * @returns
     */
    static async getNotificationById(id: number): Promise<NotificationType> {
        const response = await api.get(`${Endpoints.NOTIFICATIONS}/${id}`)
        return response.data.data
    }
    /**
     * Request for update  notifications list
     */
    static async createNotifications(
        data: updateNotificationType
    ): Promise<NotificationType> {
        const sendData = {
            data,
            version: Versions.QUESTION_RATING_STORE,
        }

        const response = await api.post(`${Endpoints.NOTIFICATIONS}`, sendData)
        return {
            ...response.data.data,
            id: 5,
        }
    }

    /**
     * Request for upload photo
     */
    static async uploadPhoto(id: number, file: FormData): Promise<boolean> {
        try {
            const response = await api.post(
                `${Endpoints.NOTIFICATIONS}/${id}/icon`,
                file
            )

            return true
        } catch (error) {
            return false
        }
    }

    static async deleteTeplate(id: number): Promise<boolean> {
        try {
            await api.delete(`${Endpoints.NOTIFICATIONS}/${id}`)
            return true
        } catch (error) {
            return false
        }
    }

    /**
     * fetch notifications types
     */
}
