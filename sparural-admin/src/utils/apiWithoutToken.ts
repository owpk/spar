import axios, { AxiosRequestConfig } from 'axios'

const config: AxiosRequestConfig = {
    headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
        'x-client-type':'web'
    },
}
/**
 * _PREFIX_URL
 */

 const _PREFIX_URL = '/api/v1'
 config.baseURL = _PREFIX_URL

const instance = axios.create(config)

export default instance
