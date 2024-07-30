import api from '../utils/api'
import { Endpoints } from '../constants'
import { CreateStatusBuyersType, StatusBuyersType } from '../types'
import { Versions } from '../config'

export class StatusBuyersService {
    /**
     *
     * Request for get StatusBuyers
     * @param data { offset, limit}
     */
    static async getStatusBuyers(data: {
        offset?: number
        limit?: number
    }): Promise<Array<StatusBuyersType>> {
        const { offset = 0, limit = 30 } = data

        const query: Array<string> = []

        query.push(`offset=${offset}`)
        query.push(`limit=${limit}`)

        const response = await api.get(
            `${Endpoints.STATUS_BUYERS}?${query.join('&')}`
        )
        return response.data.data
    }
    /**
     * Request for get StatusBuyers on id
     * @param id numder
     */
    static async getStatusBuyersById(id: number): Promise<StatusBuyersType> {
        const response = await api.get(`${Endpoints.STATUS_BUYERS}/${id}`)
        return response.data.data
    }

    /**
     *  Request for post StatusBuyers
     *
     */

    static async createStatusBuyers(
        data: CreateStatusBuyersType
    ): Promise<StatusBuyersType> {
        const sendData = {
            data: data,
            version: Versions.STATUS_BUYERS,
        }
        const response = await api.post(Endpoints.STATUS_BUYERS, sendData)
        return response.data.data
    }

    /**
     * Request for update StatusBuyers on id
     */
    static async updateStatusBuyers(
        id: number,
        data: CreateStatusBuyersType
    ): Promise<StatusBuyersType> {
        const response = await api.put(`${Endpoints.STATUS_BUYERS}/${id}`, {
            data,
            version: 1,
        })
        return response.data.data
    }

    /**
     * Reques for delete StatusBuyerss on id
     * @param id number
     */

    static async deleteStatusBuyers(id: number): Promise<boolean> {
        const response = await api.delete(`${Endpoints.STATUS_BUYERS}/${id}`)
        return response.data.success
    }

    /**
   * Request for upload photo
   */
     static async uploadPhoto(id: number, file: FormData): Promise<boolean> {
        try {
            const response = await api.post(`${Endpoints.STATUS_BUYERS}/${id}/icon`, file)

            return true
        } catch (error) {
            return false
        }
    }
}
