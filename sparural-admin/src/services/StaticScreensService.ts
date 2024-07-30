import api from '../utils/api'
import { Endpoints } from '../constants'
import { StaticScreensType } from '../types'
import { staticDemo } from '../demoData'
import { Versions } from '../config'

export class StaticScreensService {
    /**
     *
     * Request for get Static screens
     * @param data { offset, limit}
     */
    static async getStaticScreens(data: {
        offset?: number
        limit?: number
    }): Promise<Array<StaticScreensType>> {
        const { offset = 0, limit = 30 } = data

        const query: Array<string> = []

        query.push(`offset=${offset}`)
        query.push(`limit=${limit}`)


        const response = await api.get(
            `${Endpoints.STATIC_PAGES}?${query.join('&')}`
        )
        return response.data.data
    }
    /**
     * Request for get Static screens on id
     * @param id numder
     */
    static async getStaticScreensById(alias: string): Promise<StaticScreensType> {
         // !TODO connect real date
        //  return staticDemo.find((i) => i.id === id) || {} as StaticScreensType
        const response = await api.get(`${Endpoints.STATIC_PAGES}/${alias}`)
        return response.data.data
    }

    /**
     *  Request for post Static screens
     * @param data {alias, title, content}
     */

    static async createStaticScreens(data: {
        alias: string
        title: string
        content: string
    }): Promise<boolean> {
        const sendData = {
            data,
            version: Versions.STATIC_SCREEN
        }
        const response = await api.post(Endpoints.STATIC_PAGES, sendData)
        return response.data.success
    }

    /**
     * Request for update Static screens on id
     * @param id number
     * @param data { alias, title, content}

     */
    static async updateStaticScreens(
        idAlias: string,
        data: {
            alias: string
            title: string
            content: string
        }
    ): Promise<boolean> {
        const sendData = {
            data,
            version: Versions.STATIC_SCREEN
        }
        const response = await api.put(`${Endpoints.STATIC_PAGES}/${idAlias}`, sendData)
        return response.data.success
    }

    /**
     * Reques for delete Static screens on id
     * @param id number
     */

    static async deleteStaticScreen(alias: string): Promise<boolean> {
        const response = await api.delete(`${Endpoints.STATIC_PAGES}/${alias}`)
        return response.data.success
    }
}
