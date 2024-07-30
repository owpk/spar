import api from '../utils/api'
import { Endpoints } from '../constants'
import { BlockType } from "../types";
import { BlocksDemo } from '../demoData';
import { Versions } from '../config';



export class BlocksService {
    static async getBlocks(data: {
        offset?: number
        limit?: number
    }): Promise<Array<BlockType>> {
        const { offset = 0, limit = 30 } = data

        const query: Array<string> = []

        query.push(`offset=${offset}`)
        query.push(`limit=${limit}`)

        // !TODO connect back
        const response = await api.get(
            `${Endpoints.BLOCKS}?${query.join('&')}`
        )
        return response.data.data
        return BlocksDemo
    }

    static async updateBlock(data: BlockType): Promise<BlockType> {
        const sendData = {
            data,
            vertion: Versions.BLOCKS
        }

        const response = await api.put(`${Endpoints.BLOCKS}/${data.code}`, sendData)
        return response.data.data
    }
}