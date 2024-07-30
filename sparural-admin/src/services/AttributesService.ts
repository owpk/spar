import api from '../utils/api'
import { Endpoints } from '../constants'
import { AttributesType, Phototype } from '../types'
import { createFormDataFile } from '../utils/helpers'
import { Versions } from '../config'
import { RecipeAttributesType } from '../components/pages/RecipeAttributes/RecipeAttributCard'

export type UpdateAttribute = {
    name?: string
    showOnPreview?: boolean
    draft?: boolean
}
export class AttributesService {
    /**
     *
     * @param data for pagination
     * @returns array of AttributesType
     */
    static async getAttributes(data: {
        offset?: number
        limit?: number
    }): Promise<Array<AttributesType>> {
        const { offset = 0, limit = 30 } = data

        const query: Array<string> = []

        query.push(`offset=${offset}`)
        query.push(`limit=${limit}`)

        const response = await api.get(
            `${Endpoints.ATTRIBUTES}?${query.join('&')}&city=1`
        )
        return response.data.data

        // return AttributesDemo
    }

    /**
     * create Attributes
     */
    static async createAttributes(data: {
        name?: string
        draft?: boolean
    }): Promise<AttributesType> {
        const response = await api.post(Endpoints.ATTRIBUTES, {
            data,
            version: Versions.ATTRIBUTES,
        })
        return response.data.data
    }
    /**
     * update Attributes
     */
    static async updateAttributes(
        id: number,
        data: UpdateAttribute
    ): Promise<AttributesType> {
        const response = await api.put(`${Endpoints.ATTRIBUTES}/${id}`, {
            data: data,
        })
        return response.data.data
    }
    /**
     * upload photo Attributes
     */
    static async uploadPhoto(id: number, photo: FormData): Promise<Phototype> {
        const response = await api.post(
            `${Endpoints.ATTRIBUTES}/${id}/icon`,
            photo
        )
        return response.data.data
    }

    /**
     * delete attribute
     */
    static async deleteAttribute(id: number): Promise<boolean> {
        try {
            await api.delete(`${Endpoints.ATTRIBUTES}/${id}`)
            return true
        } catch (error) {
            return false
        }
    }

    static async getRecipeAttributes(data: {
        offset?: number
        limit?: number
    }): Promise<Array<RecipeAttributesType>> {
        const { offset = 0, limit = 30 } = data

        const query: Array<string> = []

        query.push(`offset=${offset}`)
        query.push(`limit=${limit}`)

        const response = await api.get(
            `${Endpoints.RECIPE_ATTRIBUTES}?${query.join('&')}`
        )
        return response.data.data

        // return AttributesDemo
    }

    static async createRecipeAttribute(data: {
        name?: string
        draft?: boolean
    }): Promise<RecipeAttributesType> {
        const response = await api.post(Endpoints.RECIPE_ATTRIBUTES, {
            data,
        })
        return response.data.data
    }

    static async updateRecipeAttributes(
        id: number,
        data: UpdateAttribute
    ): Promise<AttributesType> {
        const response = await api.put(`${Endpoints.RECIPE_ATTRIBUTES}/${id}`, {
            data: data,
        })
        return response.data.data
    }

    static async deleteRecipeAttribute(id: number): Promise<boolean> {
        try {
            await api.delete(`${Endpoints.RECIPE_ATTRIBUTES}/${id}`)
            return true
        } catch (error) {
            return false
        }
    }
}
