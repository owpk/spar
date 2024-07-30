import api from '../utils/api'
import { Endpoints } from '../constants'
import { FetchCityType, UserRoleType } from '../types'
import {
    FileSource,
    SourceParametrType,
    UploadFileEntitiesType,
} from '../config'
import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'
import { sendFormData } from '../utils/helpers'
import { message } from 'antd'
import { fileURLToPath } from 'url'

export type UploadFileDocType = {
    source: FileSource
    ['source-parameters']?: SourceParametrType | {}
    entities: Array<UploadFileEntitiesType>
    file: File
    mime?: string
}

export const filesApi = createApi({
    reducerPath: 'filesApi',
    baseQuery: fetchBaseQuery({
        baseUrl: '/',
        prepareHeaders: (headers, { getState }) => {
            // headers.set('Content-type', `multipart/form-data`)
            headers.set('x-client-type', 'web')
            return headers
        },
    }),
    tagTypes: ['Files'],

    endpoints: (builder) => ({
        uploadFile: builder.mutation({
            query: (file: UploadFileDocType) => ({
                url: 'files/',
                method: 'POST',
                body: sendFormData(file),
            }),
            invalidatesTags: ['Files'],
            transformResponse: (response) => {
                if (!response) {
                    message.error('error')
                }
            },
        }),
        deleteFile: builder.mutation({
            query: (id: string) => ({
                url: `files/${id}`,
                method: 'DELETE',
            }),
            invalidatesTags: ['Files'],
        }),
    }),
})

export const { useUploadFileMutation, useDeleteFileMutation } = filesApi
export class FileService {
    static async uploadImage(file: UploadFileDocType) {
        const response = await api.post(
            'https://admin.sparural.ru:40443/files/',
            file
        )
        return response.data
    }
}
