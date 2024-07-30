import api from '../utils/api'
import { Endpoints } from '../constants'
import { CreateFavoriteCategoriesType, FavoriteCategoriesType } from '../types'
import { FavoritCategoriesData } from '../demoList'
import { createFormDataFile } from '../utils/helpers'
import { Versions } from '../config'

export class FavoriteCategoriesService {
    /**
     * Request for fetch cities
     */
    static async getFavoriteCategories(data: {
        offset?: number
        limit?: number
    }): Promise<Array<FavoriteCategoriesType>> {
        const { offset = 0, limit = 30 } = data

        const query: Array<string> = []

        query.push(`offset=${offset}`)
        query.push(`limit=${limit}`)

        // !TODO connect back
        const response = await api.get(
            `${Endpoints.FAVORITE_CATEGORIES}?${query.join('&')}`
        )
        return response.data.data

        return FavoritCategoriesData
    }

    static async getFavoriteCategoriesById(
        id: number
    ): Promise<FavoriteCategoriesType> {
        // !TODO connect back
        const response = await api.get(`${Endpoints.FAVORITE_CATEGORIES}/${id}`)

        return response.data.data

        // return FavoritCategoriesData[0]
    }

    /**
     * Create user
     */
    static async createFavoriteCategories(
        data: CreateFavoriteCategoriesType
    ): Promise<FavoriteCategoriesType> {
        const sendData = {
            data,
            version: Versions.FAVORITE_CATEGORIES,
        }
        const response = await api.put(Endpoints.FAVORITE_CATEGORIES, sendData)
        return response.data.data
    }
    /**
     * Update user
     */
    static async updateFavoriteCategories(
        id: number,
        data: CreateFavoriteCategoriesType
    ): Promise<FavoriteCategoriesType> {
        const sendData = {
            data,
            version: Versions.FAVORITE_CATEGORIES,
        }
        const response = await api.put(
            `${Endpoints.FAVORITE_CATEGORIES}/${id}`,
            sendData
        )
        return response.data.data
    }

       /**
     * Request for upload photo
     */
        static async uploadPhoto(id: number, file: FormData): Promise<boolean> {
            try {
                const response = await api.post(`${Endpoints.FAVORITE_CATEGORIES}/${id}/photo`, file)

                return true
            } catch (error) {
                return false
            }
        }

    /**
     * DELETE user
     */
    static async deleteFavoriteCategoriesr(id: number): Promise<boolean> {
        const response = await api.delete(
            `${Endpoints.FAVORITE_CATEGORIES}/${id}`
        )
        return response.data.success
    }
}
