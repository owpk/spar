import api from '../utils/api'
import {Currency as CurrencyType} from '../types'
import {Endpoints} from '../constants'

type GetListParams = {
    offset?: number
    limit?: number
}

export class CurrenciesService {
    static async getList(params: GetListParams = {}): Promise<Array<CurrencyType>> {
        const {offset = 0, limit = 30} = params

        const query: Array<string> = []

        query.push(`offset=${offset}`)
        query.push(`limit=${limit}`)

        const response = await api.get(`${Endpoints.CURRENCIES}?${query.join('&')}`)

        return response.data.data
    }
}