import api from '../utils/api'
import { Endpoints } from '../constants'
import {  PersonalOffersType } from '../types'
import { Versions } from '../config'

export type CreatePersonalOffersType = {
    attribute?: string;
    title?: string;
    draft?: boolean;
    description?: string;
    begin?: number | undefined;
    end?: number | undefined;
    isPublic?: boolean
  };

export class PersonalOffersService {
    /**
     *
     * Request for get Catalogs Pages
     * @param data { offset, limit}
     */
    static async getPersonalOffers(data: {
        offset?: number
        limit?: number
    }): Promise<Array<PersonalOffersType>> {
        const { offset = 0, limit = 30 } = data

        const query: Array<string> = []

        query.push(`offset=${offset}`)
        query.push(`limit=${limit}`)

        const response = await api.get(
            `${Endpoints.PERSONAL_OFFERS}?${query.join('&')}`
        )
        return response.data.data
    }
    /**
     * Request for get Catalogs Pages on id
     * @param id numder
     */
    static async getPersonalOffersById(
        id: number
    ): Promise<PersonalOffersType> {
        const response = await api.get(`${Endpoints.PERSONAL_OFFERS}/${id}`)
        return response.data.data
    }

    /**
     *  Request for post Catalogs Pages
     * @param data {alias, title, url}
     */

    static async createPersonalOffers(
        data: CreatePersonalOffersType
    ): Promise<PersonalOffersType> {
        const sendData = {
            data,
            version: Versions.PROFILE_OFFERS,
        }
        const response = await api.post(Endpoints.PERSONAL_OFFERS, sendData)
        return response.data.data
    }

    /**
     * Request for update Outside Docs on id
     * @param id number
     * @param data { alias, title, url}

     */
    static async updatePersonalOffers(
        id: number,
        data: CreatePersonalOffersType
    ): Promise<PersonalOffersType> {
        const sendData = {
            data,
            version: Versions.PROFILE_OFFERS,
        }
        const response = await api.put(
            `${Endpoints.PERSONAL_OFFERS}/${id}`,
            sendData
        )
        return response.data.data
    }

    /**
     * Reques for delete Outsude Docs on id
     * @param id number
     */

    static async deletePersonalOffers(id: number): Promise<boolean> {
        const response = await api.delete(`${Endpoints.PERSONAL_OFFERS}/${id}`)
        return response.data.success
    }

      /**
     * Request for upload photo
     */
       static async uploadPhoto(id: number, file: FormData): Promise<boolean> {
        try {
            const response = await api.post(`${Endpoints.PERSONAL_OFFERS}/${id}/photo`, file)

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
            const response = await api.post(`${Endpoints.PERSONAL_OFFERS}/${id}/preview`, file)

            return true
        } catch (error) {
            return false
        }
    }
}
