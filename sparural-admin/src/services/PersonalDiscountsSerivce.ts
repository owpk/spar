import api from '../utils/api'
import { Endpoints } from '../constants'
import { PersonalDiscountsType } from '../types'
import { Versions } from '../config'

export type CreatePersonalDiscountType = {
    id?: number
    isPublic: boolean
    loymaxCounterId: string
    loymaxOfferId: string
    maxValue: number
    offerId?: number
    draft?: boolean
}

export class PersonalDiscountsSerivce {
    /**
     *
     * Request for get Catalogs Pages
     * @param data { offset, limit}
     */
    static async getPersonalDiscounts(data: {
        offset?: number
        limit?: number
    }): Promise<Array<PersonalDiscountsType>> {
        const { offset = 0, limit = 30 } = data

        const query: Array<string> = []

        query.push(`offset=${offset}`)
        query.push(`limit=${limit}`)

        const response = await api.get(
            `${Endpoints.PERSONAL_DISCOUNTS}?${query.join('&')}`
        )
        return response.data.data
    }

    /**
     * Request for get Catalogs Pages on id
     * @param id numder
     */
    static async getPersonalDiscountsById(
        id: number
    ): Promise<PersonalDiscountsType> {
        const response = await api.get(`${Endpoints.PERSONAL_DISCOUNTS}/${id}`)
        return response.data.data
    }

    /**
     *  Request for post Catalogs Pages
     * @param data {alias, title, url}
     */

    static async createPersonalDiscount(
        data: CreatePersonalDiscountType
    ): Promise<PersonalDiscountsType> {
        const sendData = {
            data,
            version: Versions.PROFILE_OFFERS,
        }
        const response = await api.post(Endpoints.PERSONAL_DISCOUNTS, sendData)
        return response.data.data
    }

    /**
     * Request for update Outside Docs on id
     * @param id number
     * @param data { alias, title, url}

     */
    static async updatePersonalDiscount(
        id: number,
        data: CreatePersonalDiscountType
    ): Promise<PersonalDiscountsType> {
        const sendData = {
            data,
            version: Versions.PROFILE_OFFERS,
        }
        const response = await api.put(
            `${Endpoints.PERSONAL_DISCOUNTS}/${id}`,
            sendData
        )
        return response.data.data
    }

    /**
     * Reques for delete Outsude Docs on id
     * @param id number
     */

    static async deletePersonalDiscount(id: number): Promise<boolean> {
        const response = await api.delete(
            `${Endpoints.PERSONAL_DISCOUNTS}/${id}`
        )
        return response.data.success
    }
}
