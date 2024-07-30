import api from '../utils/api'
import { Endpoints } from '../constants'
import {
    Counters,
    CreateCounters,
    CreateUsersGroup,
    UserAtributes,
    UsersGroup,
} from '../types'
import { UsersGroupDemo } from '../demoData'
import { Versions } from '../config'

export class GroupUsersService {
    /**
     *
     * @param data for pagination and search
     * @returns
     */
    static async getGroupUsers(data: {
        offset?: number
        limit?: number
        search?: string
    }): Promise<Array<UsersGroup>> {
        const { offset = 0, limit = 30, search = '' } = data

        const query: Array<string> = []

        query.push(`offset=${offset}`)
        query.push(`limit=${limit}`)
        query.push(`search=${search}`)

        // !TODO - connect BACK
        const response = await api.get(
            `${Endpoints.USERS_GROUP}?${query.join('&')}&city=1`
        )
        return response.data.data
        // return UsersGroupDemo
    }
    /**
     *
     * @param data for pagination and search
     * @returns
     */
    static async getOneGroupUsers(id: number): Promise<UsersGroup> {
        // !TODO - connect BACK
        const response = await api.get(`${Endpoints.USERS_GROUP}/${id}`)
        return response.data.data
        // return UsersGroupDemo.filter((i) => i.id === id)[0]
    }
    /**
     * create one group
     * @param data - name of group
     * @returns one group
     */
    static async createGroup(data: CreateUsersGroup): Promise<UsersGroup> {
        const dataWithVersion = {
            data,
            version: Versions.USERS,
        }
        const response = await api.post(Endpoints.USERS_GROUP, dataWithVersion)
        return response.data.data
    }
    /**
     * update one group
     * @param data - name of the group
     * @param id - id of the group
     * @returns one group
     */
    static async editGroup(
        id: number,
        data: CreateUsersGroup
    ): Promise<UsersGroup> {
        const dataWithVersion = {
            data,
            version: Versions.USERS,
        }
        const response = await api.put(
            `${Endpoints.USERS_GROUP}/${id}`,
            dataWithVersion
        )
        return response.data.data
    }
    /**
     * added users into group
     */
    static async addUsersIntoGroup(
        id: number,
        data: number[]
    ): Promise<boolean> {
        const sendData = {
            users: data,
            version: 1,
        }
        const response = await api.post(
            `${Endpoints.USERS_GROUP}/${id}/add-users`,
            sendData
        )
        return response.data.success
    }
    /**
     * delete users from group
     */
    static async deleteUsersIntoGroup(
        id: number,
        data: number[]
    ): Promise<boolean> {
        const sendData = {
            users: data,
            version: 1,
        }
        const response = await api.post(
            `${Endpoints.USERS_GROUP}/${id}/delete-users`,
            sendData
        )
        return response.data.success
    }
    /**
     * delete group
     */
    static async deleteUsersGroup(id: number): Promise<boolean> {
        const response = await api.delete(`${Endpoints.USERS_GROUP}/${id}`)
        return response.data.success
    }

    static async getAllUserAtributes(data: {
        offset?: number
        limit?: number
    } = {}): Promise<UserAtributes[]> {
        const { offset = 0, limit = 30 } = data
        const query: Array<string> = []
        query.push(`offset=${offset}`)
        query.push(`limit=${limit}`)

        const response = await api.get(`${Endpoints.USERS_ATRIBUTES}?${query.join('&')}`)
        return response.data.data
    }

    static async getAllCounters(data: {
        offset?: number
        limit?: number
    } = {}): Promise<Counters[]> {

        const { offset = 0, limit = 30 } = data
        const query: Array<string> = []
        query.push(`offset=${offset}`)
        query.push(`limit=${limit}`)

        const response = await api.get(`${Endpoints.COUNTERS}?${query.join('&')}`)
        console.log(response)
        return response.data.data
    }

    static async deleteCounter(id: number): Promise<boolean> {
        const response = await api.delete(`${Endpoints.COUNTERS}/${id}`)
        return response.data.success
    }

    static async getOneCounterById(id: number): Promise<Counters> {
        const response = await api.get(`${Endpoints.COUNTERS}/${id}`)
        return response.data.data
    }

    static async createCounter(data: CreateCounters): Promise<Counters> {
        const dataWithVersion = {
            data,
            version: Versions.COUNTERS,
        }
        const response = await api.post(Endpoints.COUNTERS, dataWithVersion)
        return response.data.data
    }

    static async updateCounter(
        id: number,
        data: CreateCounters
    ): Promise<Counters> {
        const dataWithVersion = {
            data,
            version: Versions.COUNTERS,
        }
        const response = await api.put(
            `${Endpoints.COUNTERS}/${id}`,
            dataWithVersion
        )
        return response.data.data
    }
}
