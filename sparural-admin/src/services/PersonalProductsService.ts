import api from '../utils/api'
import { Endpoints } from '../constants'
import { PersonalProductsType, CreatePersonalProductsType } from '../types'
import { Versions } from '../config'

export class PersonalProductsService {
    /**
     *
     * Request for get Catalogs Pages
     * @param data { offset, limit}
     */
    static async getPersonalProducts(data: {
        offset?: number
        limit?: number
        search?: string
    }): Promise<Array<PersonalProductsType>> {
        const { offset = 0, limit = 30, search = data.search } = data

        const query: Array<string> = []

        query.push(`offset=${offset}`)
        query.push(`limit=${limit}`)
        query.push(`search=${search}`)

        const response = await api.get(`${Endpoints.GOODS}?${query.join('&')}`)
        return response.data.data
    }
    /**
     * Request for get Catalogs Pages on id
     * @param id numder
     */
    static async getPersonalProductsById(
        id: number
    ): Promise<PersonalProductsType> {
        const response = await api.get(`${Endpoints.GOODS}/${id}`)
        return response.data.data
    }

    /**
     *  Request for post Catalogs Pages
     * @param data {alias, title, url}
     */

    static async createPersonalProducts(
        data: CreatePersonalProductsType
    ): Promise<PersonalProductsType> {
        const sendData = {
            data,
            version: Versions.PERSONAL_PRODUCTS,
        }
        const response = await api.post(Endpoints.GOODS, sendData)
        return response.data.data
    }

    /**
     * Request for update Outside Docs on id
     * @param id number
     * @param data { alias, title, url}

     */
    static async updatePersonalProducts(
        id: number,
        data: CreatePersonalProductsType
    ): Promise<PersonalProductsType> {
        const sendData = {
            data,
            version: Versions.PERSONAL_PRODUCTS,
        }
        const response = await api.put(`${Endpoints.GOODS}/${id}`, sendData)
        return response.data.data
    }

    /**
     * Reques for delete Outsude Docs on id
     * @param id number
     */

    static async deletePersonalProducts(id: number): Promise<boolean> {
        const response = await api.delete(`${Endpoints.GOODS}/${id}`)
        return response.data.success
    }

    /**
     * Request for upload photo
     */
    static async uploadPhoto(id: number, file: FormData): Promise<boolean> {
        try {
            const response = await api.post(
                `${Endpoints.GOODS}/${id}/photo`,
                file
            )

            return true
        } catch (error) {
            return false
        }
    }

    /**
     * Request for upload photo
     */
    static async uploadPreview(id: number, file: FormData): Promise<boolean> {
        try {
            const response = await api.post(
                `${Endpoints.GOODS}/${id}/preview`,
                file
            )

            return true
        } catch (error) {
            return false
        }
    }
}
