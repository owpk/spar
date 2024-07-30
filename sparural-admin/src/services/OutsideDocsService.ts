import api from '../utils/api'
import { Endpoints } from '../constants'
import { OutsideDocType } from '../types'
import { Versions } from '../config'
import { _DEMO_EXTERNAL } from '../demoData'

export class OutsideDocsService {
    /**
     *
     * Request for get Outside Docs
     * @param data { offset, limit}
     */
    static async getOutsideDocs(data: {
        offset?: number
        limit?: number
    }): Promise<Array<OutsideDocType>> {
        const { offset = 0, limit = 30 } = data

        const query: Array<string> = []

        query.push(`offset=${offset}`)
        query.push(`limit=${limit}`)

        const response = await api.get(
            `${Endpoints.EXTERNAL_DOCS}?${query.join('&')}`
        )
        // return _DEMO_EXTERNAL
        return response.data.data
    }
    /**
     * Request for get Outside Docs on id
     * @param id numder
     */
    static async getOutsideDocsById(idAlias: string): Promise<OutsideDocType> {
        const response = await api.get(`${Endpoints.EXTERNAL_DOCS}/${idAlias}`)
        return response.data.data
    }

    /**
     *  Request for post Outside Docs
     * @param data {alias, title, url}
     */

    static async createOutsideDocs(data: {
        alias: string
        title: string
        url: string
    }): Promise<OutsideDocType> {
        const sendData = {
            data: data,
            version: Versions.OUTSIDE_DOCS,
        }
        const response = await api.post(Endpoints.EXTERNAL_DOCS, sendData)
        return response.data.data
    }

    /**
     * Request for update Outside Docs on id
     * @param id number
     * @param data { alias, title, url}

     */
    static async updateOutsideDocs(
        idAlias: string,
        data: {
            alias: string
            title: string
            url: string
        }
    ): Promise<OutsideDocType> {
        const sendData = {
            data: data,
            version: Versions.OUTSIDE_DOCS,
        }
        const response = await api.put(
            `${Endpoints.EXTERNAL_DOCS}/${idAlias}`,
            sendData
        )
        return response.data.data
    }

    /**
     * Reques for delete Outsude Docs on id
     * @param id number
     */

    static async deleteOutsideDocs(alias: string): Promise<boolean> {
        const response = await api.delete(`${Endpoints.EXTERNAL_DOCS}/${alias}`)
        return response.data.success
    }
}
