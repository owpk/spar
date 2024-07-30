import { ResponseType, ErrorsTypes } from '../utils/api'
import apiWithoutToken from '../utils/apiWithoutToken'
import api from '../utils/api'
import { Endpoints } from '../constants'

/**
 * Токен.
 */
export type Token = string

export class AuthService {
    /**
     * Request for authorisation
     */
    static async login(data: {
        phoneNumber: string
        password: string
    }): Promise<any> {
        const response = await apiWithoutToken.post(Endpoints.LOGIN, data)
        return response
    }
    /**
     * Request for authorisation
     */
    static async logout(): Promise<any> {
        const response = await apiWithoutToken.post(Endpoints.LOGOUT)
        return response
    }

    /**
     *  Recovery password
     */

    static async RecoveryPassword(data: {
        notifier: string | number
        notifierIdentity: string
    }): Promise<any> {
        const response = await api.post(Endpoints.RECOVERY_PASSWORD, data)
        return response
    }


    /**
     * Request gets active userData
     */
    static async getUserData(): Promise<any> {
        const response = await api.get(Endpoints.USERS)
        return response.data.data
    }

    /**
     * Check active User
     */
    static async checkUser() {
        const response = await api.get(Endpoints.USER_CHECK)
        return response.data.data
    }

    /**
     * refresh token
     */
    static async refreshToken() {
        const response = await apiWithoutToken.post(Endpoints.REFRESH_TOKEN)
        return response.data.data
    }
}
