import api from '../utils/api'
import { Endpoints } from '../constants'
import {
    SettingsAuthType,
    SettingsGeneralType,
    SettingsKodType,
    SettingsLoymaxType,
    SettingsNotificatonType,
    SettingsPaymentType,
    SettingsSocialType,
} from '../types'
import { Versions } from '../config'

export class SettingsService {
    /**
     *  Request for get Settings Codes
     */
    static async getSettingsCoder(): Promise<SettingsKodType> {
        const response = await api.get(Endpoints.SETTINGS_CODES)
        return response.data.data
    }
    /**
     * Request for update Settings Codes
     * @param data
     */
    static async updateSettingsCodes(data: SettingsKodType): Promise<boolean> {

        const sendData = {
            data,
            version: Versions.CODE_SETTINGS
        }
        const response = await api.put(Endpoints.SETTINGS_CODES, sendData)
        return response.data.success
    }

    /**
     *  Request for get Settings Auth
     */
    static async getSettingsAuth(): Promise<SettingsAuthType> {
        const response = await api.get(Endpoints.SETTINGS_AUTH)
        return response.data.data
    }
    /**
     * Request for update Settings Auth
     * @param data
     */
    static async updateSettingsAuth(data: SettingsAuthType): Promise<SettingsAuthType> {
        const sendData = {
            data,
            version: Versions.LOYMAX_SETTINGS
        }
        const response = await api.put(Endpoints.SETTINGS_AUTH, sendData)
        return response.data.data
    }

    /**
     * Request for get Settings Loymax
     *
     */
    static async getSettingsLoymax(): Promise<SettingsLoymaxType> {
        const response = await api.get(Endpoints.SETTING_LOYMAX)
        return response.data.data
    }
    /**
     * Request for update Settings Loymax
     * @param data
     */
    static async updateSettingsLoymax(data: SettingsLoymaxType): Promise<boolean> {
        const sendData = {
            data,
            version: Versions.LOYMAX_SETTINGS
        }
        const response = await api.put(Endpoints.SETTING_LOYMAX, sendData)
        return response.data.success
    }
    /**
     *  Request for get Settings Notification
     */
    static async getSettingsNotification(): Promise<SettingsNotificatonType> {
        const response = await api.get(Endpoints.SETTING_NOTIFICATION)
        return response.data.data
    }

    /**
     * Request for update Settings Notification
     * @param data
     */
    static async updateSettingsNotification(data: {
        // data: тип поля
    }): Promise<boolean> {
        const sendData = {
            data,
            version: Versions.LOYMAX_SETTINGS
        }
        const response = await api.put(Endpoints.SETTING_NOTIFICATION, sendData)
        return response.data.success
    }

    /**
     * Request for get Settings Social
     *
     */
    static async getSettingsSocial(): Promise<SettingsSocialType[]> {
        const response = await api.get(Endpoints.SETTING_SOCIAL)
        return response.data.data
    }
    /**
     * Request for update Settings Social
     * @param data
     */
    static async updateSettingsSocial(id: number, data: {
        appId: string
        appSecret: string
    }): Promise<boolean> {
        const sendData = {
            data,
            version: Versions.LOYMAX_SETTINGS
        }
        const response = await api.put(`${Endpoints.SETTING_SOCIAL}/${id}`, sendData)
        return response.data.success
    }
    /**
     * Request for get Settings General
     */
    static async getSettingsGeneral(): Promise<SettingsGeneralType> {
        const response = await api.get(Endpoints.SETTING_GENERAL)
        return response.data.data
    }

    /**
     * Request for update Settings General
     * @param data
     *
     */
    static async updateSettingsGeneral(data: SettingsGeneralType): Promise<SettingsGeneralType> {
        const sendData = {
            data,
            version: Versions.LOYMAX_SETTINGS
        }
        const response = await api.put(Endpoints.SETTING_GENERAL, sendData)
        return response.data.data
    }

    static async getSettingsPayment(): Promise<SettingsPaymentType> {
        const response = await api.get(Endpoints.SETTING_PAYMENT)
        return response.data.data
    }
    static async upddateSettingsPayment(data: {
        tinkoffMerchantId: string
    }): Promise<SettingsPaymentType> {
        const sendData = {
            data,
            version: Versions.LOYMAX_SETTINGS
        }
        const response = await api.put(Endpoints.SETTING_PAYMENT, sendData)
        return response.data.success
    }
}
