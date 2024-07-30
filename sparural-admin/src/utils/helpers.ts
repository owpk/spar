import moment from 'moment'
import { IntervalTimeType } from '../config'

export const showTimer = (time: number) => {
    const min = Math.floor(time / 60)
    const sec = time % 60
    return `${min < 10 ? '0' + min : min}:${sec < 10 ? '0' + sec : sec} сек`
}

export const setCookie = (name: string, value: string, options: any = {}) => {
    options = {
        path: '/',
        ...options,
    }

    if (options.expires instanceof Date) {
        options.expires = options.expires.toUTCString()
    }

    let updatedCookie =
        encodeURIComponent(name) + '=' + encodeURIComponent(value)

    for (let optionKey in options) {
        updatedCookie += '; ' + optionKey
        let optionValue = options[optionKey]
        if (optionValue !== true) {
            updatedCookie += '=' + optionValue
        }
    }

    document.cookie = updatedCookie
}

export const createFormDataFile = (file: File) => {
    const fd: FormData = new FormData()
    fd.append('file', file)
    return fd
}

// transform timestamp into date
export const printDate = (date: number) => {
    const d = new Date(date < 10000000000 ? date * 1000 : date)
    const day = d.getDate() < 10 ? `0${d.getDate()}` : d.getDate()
    const month =
        d.getMonth() + 1 < 10 ? `0${d.getMonth() + 1}` : d.getMonth() + 1
    const year = d.getFullYear()

    return `${day}.${month}.${year}`
}

//url validate
export function isValidUrl(value: string) {
    let expression =
        /[-a-zA-Z0-9@:%_\+.~#?&//=]{2,256}\.[a-z]{2,4}\b(\/[-a-zA-Z0-9@:%_\+.~#?&//=]*)?/gi
    let regexp = new RegExp(expression)
    return regexp.test(value)
}

/**
 *
 * @param date
 * @returns
 */
export const getNumbers = (e: string) => {
    return e.replace(/^\.|[^\d\.]|\.(?=.*\.)|^0+(?=\d)/g, '')
}
/**
 * get time from timestamp
 * @param date - timestamp
 * @returns - string as 00:00
 */
export const getTimeFromTimestamp = (date?: number): string => {
    const time = date ? moment(date).format('HH:mm') : ''
    return time
}

/**
 *
 * add zero
 */
export function addZero(num: number) {
    return num < 10 ? `0${num}` : num
}

/**
 * from time to timestamp
 * @param time - time as string "00:00"
 * @returns - timestamp in ms
 */
export const fromTimeToTimestamp = (time?: string): number => {
    const now = moment()
    const date = `${now.year()}-${addZero(now.month() + 1)}-${addZero(
        now.date()
    )}T${time ? time : ''}`

    return new Date(date).getTime()
}

export const arrayToQuery = (data: Array<number | string>) => {
    let query = ''
    if (data.length > 0) {
        query = '%5B' + data.join('%2C') + '%5D'
    }
    return query
}

export const toQueryString = (data: Object) => {
    const query: Array<string> = []

    let aData: any = data

    for (let key in data) {
        if (aData[key] !== undefined) {
            if (Array.isArray(aData[key]) && aData[key].length > 0) {
                query.push(`${key}=${arrayToQuery(aData[key])}`)
            } else if (!Array.isArray(aData[key])) {
                query.push(`${key}=${aData[key]}`)
            }
        }
    }

    return `?${query.join('&')}`
}

function getExtension(filename: string) {
    var parts = filename.split('.')
    return parts[parts.length - 1]
}

function isImage(filename: string) {
    var ext = getExtension(filename)
    switch (ext.toLowerCase()) {
        case 'jpg':
        case 'gif':
        case 'bmp':
        case 'png':
        case 'jpeg':
            //etc
            return true
    }
    return false
}

export const sendFormData = (data: any): FormData => {
    const sendData = new FormData()
    for (let key in data) {
        if (data[key] !== undefined && data[key] !== null) {
            if (Array.isArray(data[key])) {
                sendData.append(
                    `${key}`,
                    new Blob([JSON.stringify(data[key])], {
                        type: 'application/json',
                    })
                )
            } else if (isImage(data[key]?.name || '')) {
                // sendData.append(`${key}`, new Blob([data[key]], {
                //   type: 'multipart/form-data',
                // }))
                sendData.append(`${key}`, data[key])
            } else if (key === 'source' || key === 'source-parameters') {
                sendData.append(
                    `${key}`,
                    new Blob([data[key]], {
                        type: 'application/json',
                    })
                )
            } else {
                sendData.append(`${key}`, data[key])
            }
        }
    }

    return sendData
}

export const getToken = () => {
    return document.cookie.match('accessToken')
}

export const getSecondsFromPeriod = (
    periodCount?: number,
    period?: IntervalTimeType
) => {
    if (!periodCount) return 0
    switch (period) {
        case IntervalTimeType.MINUTES:
            return periodCount * 60
        case IntervalTimeType.HOURS:
            return periodCount * 60 * 60
        case IntervalTimeType.DAYS:
            return periodCount * 60 * 60 * 24
        case IntervalTimeType.WEEKS:
            return periodCount * 60 * 60 * 24 * 7
        case IntervalTimeType.MONTHS:
            return periodCount * 60 * 60 * 24 * 7 * 30

        default:
            return 0
    }
}

export const getPeriodFromSeconds = (
    seconds: number
): { period: IntervalTimeType; value: number } => {
    let dataType: IntervalTimeType = IntervalTimeType.MINUTES
    let timeCount: number = 0
    if (seconds > 0 && seconds < 3600) {
        dataType = IntervalTimeType.MINUTES
        timeCount = seconds / 60
    } else if (seconds > 3599 && seconds < 3600 * 24) {
        dataType = IntervalTimeType.HOURS
        timeCount = seconds / 3600
    } else if (seconds > 3599 * 24 && seconds < 3600 * 24 * 7) {
        dataType = IntervalTimeType.DAYS
        timeCount = seconds / 3600 / 24
    } else if (seconds > 3599 * 24 * 7 && seconds < 3600 * 24 * 7 * 30) {
        dataType = IntervalTimeType.WEEKS
        timeCount = seconds / 3600 / 24 / 7
    } else if (seconds > 3599 * 24 * 7 * 30) {
        dataType = IntervalTimeType.MONTHS
        timeCount = seconds / 3600 / 24 / 7 / 30
    }
    return {
        period: dataType,
        value: +timeCount.toFixed(2),
    }
}

export const getPeriod = (
    timeUnit: string
): { period: IntervalTimeType; value: number } => {
    let dataType: IntervalTimeType = IntervalTimeType.MINUTES
    let timeCount: number = 0
    if (timeUnit === 'm') {
        dataType = IntervalTimeType.MINUTES
    } else if (timeUnit === 'h') {
        dataType = IntervalTimeType.HOURS
    } else if (timeUnit === 'd') {
        dataType = IntervalTimeType.DAYS
    } else if (timeUnit === 'w') {
        dataType = IntervalTimeType.WEEKS
    } else if (timeUnit === 'M') {
        dataType = IntervalTimeType.MONTHS
    }
    return {
        period: dataType,
        value: +timeCount.toFixed(2),
    }
}

export function formatTime(timestamp: number): string {
    const date = new Date(timestamp * 1000)
    const hours = date.getHours().toString().padStart(2, '0')
    const minutes = date.getMinutes().toString().padStart(2, '0')
    return `${hours}:${minutes}`
}

export function formatDateTime(date: number): string {
    const d = new Date(date * 1000)
    const day = d.getDate().toString().padStart(2, '0')
    const month = (d.getMonth() + 1).toString().padStart(2, '0')
    const hours = d.getHours().toString().padStart(2, '0')
    const minutes = d.getMinutes().toString().padStart(2, '0')
    return `${day}-${month} ${hours}:${minutes}`
}
