import { Endpoints } from '../constants'
import { FeedBackType, FeedbackChatsList, MessageType } from '../types'
import api from '../utils/api'
import { toQueryString } from '../utils/helpers'

export type ResponsePostMessageType = {
    data: MessageType
    success: boolean
}

export class FeedBackService {
    /**
     * Request for fetch cities
     */
    static async getFeedBack(data: {
        offset?: number
        limit?: number
        search?: string
    }): Promise<Array<FeedBackType>> {
        const { offset = 0, limit = 30 } = data

        const query: Array<string> = []

        query.push(`offset=${offset}`)
        query.push(`limit=${limit}`)

        const response = await api.get(
            `${Endpoints.FEEDBACK}${toQueryString(data)}`
        )
        return response.data.data
    }

    static async getFeedBackById(id: number): Promise<FeedBackType> {
        const response = await api.get(`${Endpoints.FEEDBACK}/${id}`)
        return response.data.data
    }

    /**
     * DELETE user
     */
    static async deleteFeedBack(id: number): Promise<boolean> {
        const response = await api.delete(`${Endpoints.FEEDBACK}/${id}`)
        return response.data.success
    }

    static async getChatList(data: {
        offset?: number
        limit?: number
        search?: string
    }): Promise<Array<FeedbackChatsList>> {
        const { offset = 0, limit = 30 } = data

        const query: Array<string> = []

        query.push(`offset=${offset}`)
        query.push(`limit=${limit}`)

        const response = await api.get(
            `${Endpoints.FEEDBACK_CHATS}${toQueryString(data)}`
        )

        return response.data.data
    }

    static async getChat(id: number): Promise<FeedbackChatsList> {
        const response = await api.get(`${Endpoints.FEEDBACK_CHATS}/${id}`)

        return response.data.data
    }

    static async getChatMessages(data: {
        id: number
        offset?: number
        limit?: number
    }): Promise<Array<FeedbackChatsList>> {
        const { offset = 0, limit = 30, id } = data

        const query: Array<string> = []

        query.push(`offset=${offset}`)
        query.push(`limit=${limit}`)

        const response = await api.get(
            `${Endpoints.FEEDBACK_CHATS}/${id}/messages?limit=${limit}`
        )

        return response.data.data
    }

    static async sendChatMessage(
        data: {
            draft: boolean
            messageType: string
            text: string
        },
        id: number
    ): Promise<ResponsePostMessageType> {
        const response = await api.post(
            `${Endpoints.FEEDBACK_CHATS}/${id}/messages`,
            { data }
        )

        return response.data
    }

    static async updateChatMessage(
        data: {
            messageType: string
            draft: boolean
            text?: string
        },
        id: number,
        messageId: number
    ): Promise<ResponsePostMessageType> {
        const { messageType, draft, text } = data

        const response = await api.put(
            `${Endpoints.FEEDBACK_CHATS}/${id}/messages/${messageId}`,
            {data}
        )

        return response.data
    }

    static async uploadFileToMessage(data: {
        chatId: number
        messageId: number
        file: File
    }): Promise<any> {
        const { chatId, messageId, file } = data

        const response = await api.post(
            `${Endpoints.FEEDBACK_CHATS}/${chatId}/messages/${messageId}/file`,
            { file }
        )

        return response.data
    }

    static async readMessages(
        data: {
            messagesIds: number[]
        },
        chatId: number
    ): Promise<any> {
        const { messagesIds } = data

        const response = await api.post(
            `${Endpoints.FEEDBACK_CHATS}/${chatId}/messages/read-messages`,
            { messagesIds }
        )

        return response.data
    }
}
