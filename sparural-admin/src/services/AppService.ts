import api from '../utils/api'
import { Endpoints } from '../constants'
import { FetchCityType, UserRoleType } from '../types'

/**
 * Токен.
 */
export type Token = string

export class AppService {
    /**
     * Request for fetch cities
     */
    static async fetchCities(data: {
        offset?: number
        limit?: number
    }): Promise<Array<FetchCityType>> {
        const { offset = 0, limit = 30 } = data

        const query: Array<string> = []

        query.push(`offset=${offset}`)
        query.push(`limit=${limit}`)

        const response = await api.get(
            `${Endpoints.FETCH_CITIES}?${query.join('&')}`
        )
        return response.data.data
    }
    static async rolesUser(): Promise<Array<UserRoleType>> {
        const response = await api.get(`${Endpoints.ROLES_USER}`)
        return response.data.data
    }
}
