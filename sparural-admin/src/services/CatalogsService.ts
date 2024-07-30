import api from '../utils/api'
import { Endpoints } from '../constants'
import { CatalogsType, CreateCatalogType } from '../types'
import { Versions } from '../config'

export class CatalogsPagesService {
    /**
     *
     * Request for get Catalogs Pages
     * @param data { offset, limit}
     */
    static async getCatalogsPages(data: {
        offset?: number
        limit?: number
    }): Promise<Array<CatalogsType>> {
        const { offset = 0, limit = 30 } = data

        const query: Array<string> = []

        query.push(`offset=${offset}`)
        query.push(`limit=${limit}`)

        const response = await api.get(
            `${Endpoints.CATALOGS_PLACE}?${query.join('&')}`
        )
        return response.data.data
    }
    /**
     * Request for get Catalogs Pages on id
     * @param id numder
     */
    static async getCatalogsPagesById(id: number): Promise<CatalogsType> {
        const response = await api.get(`${Endpoints.CATALOGS_PLACE}/${id}`)
        return response.data.data
    }

    /**
     *  Request for post Catalogs Pages
     *
     */

    static async createCatalogsPages(
        data: CreateCatalogType
    ): Promise<CatalogsType> {
        const sendData = {
            data: data,
            version: Versions.INFO_SCREEN,
        }
        const response = await api.post(Endpoints.CATALOGS_PLACE, sendData)
        return response.data.data
    }

    /**
     * Request for update Catalog pages on id
     */
    static async updateCatalogsPages(
        id: number,
        data: CreateCatalogType
    ): Promise<CatalogsType> {
        const response = await api.put(
            `${Endpoints.CATALOGS_PLACE}/${id}`,
            {data, version: 1}
        )
        return response.data.data
    }

    /**
     * Reques for delete Catalog pages on id
     * @param id number
     */

    static async deleteCatalogsPages(id: number): Promise<boolean> {
        const response = await api.delete(`${Endpoints.CATALOGS_PLACE}/${id}`)
        return response.data.success
    }

       /**
     * Request for upload photo
     */
        static async uploadPhoto(id: number, file: FormData): Promise<boolean> {
            try {
                const response = await api.post(`${Endpoints.CATALOGS_PLACE}/${id}/photo`, file)
                return true
            } catch (error) {
                return false
            }
        }
}
