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

export type Recipe = {
    attributes: AttributesType[]
    calories: number
    carbohydrates: number
    description: string
    draft: boolean
    fats: number
    id: number
    photo: {
        ext: string
        mime: string
        name: string
        size: number
        url: string
        uuid: string
    }
    preview: {
        ext: string
        mime: string
        name: string
        size: number
        url: string
        uuid: string
    }
    proteins: number
    title: string
    goods: {
        goodsId: string
        name: string
        description: string
        photo: {
            ext: string
            mime: string
            name: string
            size: number
            url: string
            uuid: string
        } | null
        preview: {
            ext: string
            mime: string
            name: string
            size: number
            url: string
            uuid: string
        } | null
    }[]
}

export type CreateRecipe = {
    title: string
    description?: string
    calories?: number
    proteins?: number
    fats?: number
    carbohydrates?: number
    draft?: boolean
    attributes?: number[]
    goods?: number[]
}

export type UpdateRecipe = {
    title?: string
    description?: string
    calories?: number
    proteins?: number
    fats?: number
    carbohydrates?: number
    draft?: boolean
    attributes?: number[]
    goods?: number[]
}

/// ПОЧИСТИТЬ МЕТОДЫ

export class RecipesService {
    /**
     *
     * @param data for pagination
     * @returns array of AttributesType
     */
    static async getRecipes(data: {
        offset?: number
        limit?: number
    }): Promise<Array<Recipe>> {
        const { offset = 0, limit = 30 } = data

        const query: Array<string> = []

        query.push(`offset=${offset}`)
        query.push(`limit=${limit}`)

        const response = await api.get(
            `${Endpoints.RECIPES}?${query.join('&')}`
        )
        return response.data.data
    }

    static async getRecipe(id: number): Promise<Recipe> {
        const response = await api.get(`${Endpoints.RECIPES}/${id}`)
        return response.data.data
    }

    /**
     * create Recipe
     */
    static async createRecipe(data: CreateRecipe): Promise<Recipe> {
        const response = await api.post(Endpoints.RECIPES, {
            data,
            version: Versions.RECIPES,
        })
        return response.data.data
    }

    /**
     * update Recipe
     */
    static async updateRecipe(id: number, data: UpdateRecipe): Promise<Recipe> {
        const response = await api.put(`${Endpoints.RECIPES}/${id}`, {
            data: data,
        })
        return response.data.data
    }
    /**
     * upload photo Attributes
     */
    // static async uploadPhoto(id: number, photo: FormData): Promise<Phototype> {
    //     const response = await api.post(
    //         `${Endpoints.ATTRIBUTES}/${id}/icon`,
    //         photo
    //     )
    //     return response.data.data
    // }

    /**
     * delete attribute
     */
    static async deleteRecipe(id: number): Promise<boolean> {
        try {
            await api.delete(`${Endpoints.RECIPES}/${id}`)
            return true
        } catch (error) {
            return false
        }
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
