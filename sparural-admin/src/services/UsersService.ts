import api from '../utils/api'
import { Endpoints } from '../constants'
import {
    CreateUserType,
    GenderType,
    UserAtributes,
    UserRoleType,
    UserType,
} from '../types'
import { createFormDataFile } from '../utils/helpers'
import { Versions } from '../config'

type CreateResponseType = {
    success: boolean
    data: UserType
    version: number
}

type CreateUserAttribute = {
    attributeName?: string
    name?: string
}

export type ResponseUsersListType = {
    data: UserType[]
    meta: {
        total_count: number
    }
}

export class UsersService {
    /**
     * Request for fetch cities
     */
    static async getUsersList(data: {
        offset?: number
        limit?: number
        role?: number[]
        role_ne?: string
        group?: number
        notInGroup?: number
        search?: string
        minAge?: number
        maxAge?: number
        minRegistrationDate?: number
        maxRegistrationDate?: number
        gender?: GenderType
        alphabetSort?: 'ASC' | 'DESC'
        atributeId?: number
        statusId?: number
        accountTypeId?: number
        accountTypeMin?: number
        accountTypeMax?: number
        hasEmail?: number
        counterId?: number
        counterMin?: number
        counterMax?: number
    }): Promise<ResponseUsersListType> {
        const { offset = 0, limit = 30 } = data
        const queryData: any = data
        const query: Array<string> = []

        query.push(`offset=${offset}`)
        query.push(`limit=${limit}`)

        Object.keys(queryData).forEach((key) => {
            if (
                queryData[key] !== undefined &&
                key &&
                key !== 'limit' &&
                key !== 'offset'
            ) {
                query.push(`${key}=${queryData[key]}`)
            }
        })

        const response = await api.get(`${Endpoints.USERS}?${query.join('&')}`)
        return response.data
    }

    static async getUserById(id: number): Promise<UserType> {
        const response = await api.get(`${Endpoints.USERS}/${id}`)
        return response.data.data
    }

    /**
     * Create user
     */
    static async createUser(data: CreateUserType): Promise<UserType> {
        const dataWithVersion = {
            data,
            version: Versions.USERS,
        }
        const response = await api.post<CreateResponseType>(
            Endpoints.USERS,
            dataWithVersion
        )
        return response.data.data
    }
    /**
     * Update user
     */
    static async updateUser(
        id: number,
        data: CreateUserType
    ): Promise<UserType> {
        const dataWithVersion = {
            data,
            version: Versions.USERS,
        }
        const response = await api.put<CreateResponseType>(
            `${Endpoints.USERS}/${id}`,
            dataWithVersion
        )
        return response.data.data
    }

    /**
     * Upload photo for user
     */
    static async uploadPhoto(id: number, photo: File): Promise<any> {
        const fd = createFormDataFile(photo)
        const response = await api.post(`${Endpoints.USERS}/${id}/photo`, fd)
        return response.data
    }

    /**
     * DELETE user
     */
    static async deleteUser(id: number): Promise<boolean> {
        const response = await api.delete(`${Endpoints.USERS}/${id}`)
        return response.data.success
    }

    static async deleteUserAttribute(id: number): Promise<boolean> {
        const response = await api.delete(`${Endpoints.USERS_ATRIBUTES}/${id}`)
        return response.data.success
    }

    static async createUserAttribute(
        data: CreateUserAttribute
    ): Promise<{ data: UserAtributes }> {
        const dataWithVersion = {
            data,
            version: Versions.USERS,
        }
        const response = await api.post(
            Endpoints.USERS_ATRIBUTES,
            dataWithVersion
        )
        return response.data.data
    }

    static async updateUserAttribute(
        id: number,
        data: CreateUserAttribute
    ): Promise<UserType> {
        const dataWithVersion = {
            data,
            version: Versions.USERS,
        }
        const response = await api.put(
            `${Endpoints.USERS_ATRIBUTES}/${id}`,
            dataWithVersion
        )
        return response.data.data
    }

    static async getUserAtribute(id: number): Promise<UserAtributes> {
        const response = await api.get(`${Endpoints.USERS_ATRIBUTES}/${id}`)
        return response.data.data
    }
}
