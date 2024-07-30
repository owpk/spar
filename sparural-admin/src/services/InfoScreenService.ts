import api from '../utils/api'
import { Endpoints } from '../constants'
import { CreateInfoScreen, InfoScreenType } from '../types'
import { infoScreensDemo } from '../demoData'
import { Versions } from '../config'

export class InfoScreenService {
    /**
     * Request for get info screens
     */
    static async getScreens(data: {
        offset?: number
        limit?: number
    }): Promise<Array<InfoScreenType>> {
        const { offset = 0, limit = 30 } = data

        const query: Array<string> = []

        query.push(`offset=${offset}`)
        query.push(`limit=${limit}`)

        const response = await api.get(
            `${Endpoints.INFO_SCREEN}?${query.join('&')}`
        )
        return response.data.data
        // return infoScreensDemo
    }
    /**
     * Request for get info screen by id
     */
    static async getScreenById(id: number): Promise<InfoScreenType> {
        const response = await api.get(`${Endpoints.INFO_SCREEN}/${id}`)
        return response.data.data
    }
    /**
     * Request for update Info screen
     */
    static async updateScreen(
        id: number,
        data: CreateInfoScreen
    ): Promise<InfoScreenType> {
        const sendData = {
            data: data,
            version: Versions.INFO_SCREEN,
        }
        const response = await api.put(
            `${Endpoints.INFO_SCREEN}/${id}`,
            sendData
        )
        return response.data.data
    }
    /**
     * Request for create Info screen
     */
    static async createScreen(data: CreateInfoScreen): Promise<InfoScreenType> {
        const sendData = {
            data: data,
            version: Versions.INFO_SCREEN,
        }
        const response = await api.post(Endpoints.INFO_SCREEN, sendData)
        return response.data.data
        // return {} as InfoScreenType
    }
    /**
     * Request for delete Info screen
     */
    static async deleteScreen(id: number): Promise<boolean> {
        const response = await api.delete(`${Endpoints.INFO_SCREEN}/${id}`)
        return response.data.success
    }

    /**
     * Request for upload photo
     */
    static async uploadPhoto(id: number, file: FormData): Promise<boolean> {
        try {
            const response = await api.post(`${Endpoints.INFO_SCREEN}/${id}/photo`, file)

            return true
        } catch (error) {
            return false
        }
    }
}
