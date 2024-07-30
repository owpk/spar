import api from '../utils/api'
import { Endpoints } from '../constants'
import { CreateShopsType, ShopsType } from '../types'
import { Versions } from '../config'

export class ShopsService {
    /**
     *
     * Request for get Shops
     * @param data { offset, limit}
     */
    static async getShops(data: {
        offset?: number
        limit?: number
    }): Promise<Array<ShopsType>> {
        const { offset = 0, limit = 30 } = data

        const query: Array<string> = []

        query.push(`offset=${offset}`)
        query.push(`limit=${limit}`)

        const response = await api.get(`${Endpoints.SHOPS}?${query.join('&')}`)
        return response.data.data
    }
    /**
     * Request for get Shops on id
     * @param id numder
     */
    static async getShopsById(id: number): Promise<ShopsType> {
        const response = await api.get(`${Endpoints.SHOPS}/${id}`)
        return response.data.data
    }

    /**
     *  Request for post Shops
     * @param data {alias, title, url}
     */

    static async createShops(data: CreateShopsType): Promise<ShopsType> {
        const sendData = {
            data,
            version: Versions.SHOPS,
        }
        const response = await api.post(Endpoints.SHOPS, sendData)
        return response.data.data
    }

    /**
     * Request for update Shops on id
     * @param id number
     * @param data { alias, title, url}

     */
    static async updateShops(
        id: number,
        data: CreateShopsType
    ): Promise<ShopsType> {
        const sendData = {
            data,
            version: Versions.SHOPS,
        }
        const response = await api.put(`${Endpoints.SHOPS}/${id}`, sendData)
        return response.data.data
    }

    /**
     * Reques for delete Shops on id
     * @param id number
     */

    static async deleteShops(id: number): Promise<boolean> {
        const response = await api.delete(`${Endpoints.SHOPS}/${id}`)
        return response.data.success
    }

    /**
     * fetch shops formats
     */
    static async fetchMerchantFormats(data: {
        offset?: number
        limit?: number
    }):Promise<any>{
        const { offset = 0, limit = 30 } = data

        const query: Array<string> = []

        query.push(`offset=${offset}`)
        query.push(`limit=${limit}`)

        const response = await api.get(`${Endpoints.SHOPS_FORMATS}?${query.join('&')}`)
        return response.data.data
    }
}
