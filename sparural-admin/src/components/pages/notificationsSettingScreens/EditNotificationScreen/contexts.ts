import React, { createContext } from 'react'
import {
    NotificationTypesType,
    Phototype,
    PushActionType,
    TriggerType,
} from '../../../../types'
import { SelectOption } from '../../../simples/Selector/OptionItem'

export type Interval = {
    start?: number
    end?: number
}
export type DraftNotification = {
    name: string
    text: string
    subject?: string
    messageHTML?: string
    message?: string
    interval: {
        start?: string
        end?: string
    }
    trigger?: SelectOption
    startDate?: Interval
    period?: SelectOption
    periodCount?: any
    pushTarget?: SelectOption
    notificationType?: SelectOption
    currencyId?: SelectOption
    currencyDaysBeforeBurning?: number
    users: Array<SelectOption>
    group?: SelectOption
    sendAll: boolean
    required: boolean
    file?: File
    photoUrl?: Phototype
    isSystem?: boolean | null
    daysWithoutPurchasing?: number
}

export type NotificationErrors = {
    name?: string
    intervalStart?: string
    intervalEnd?: string
    trigger?: string
    startDateStart?: string
    startDateEnd?: string
    periodCount?: string
    period?: string
    messageHTML?: string
    subject?: string
}

export const TriggersContext = createContext<
    [
        Array<SelectOption>
    ]
>([[]])
export const NotificationsTypeContext = createContext<
    Array<NotificationTypesType>
>([])
export const PushActionsContext = createContext<
    [
        Array<PushActionType>,
        React.Dispatch<React.SetStateAction<Array<PushActionType>>>
    ]
>([[], () => {}])
export const NotificationContext = createContext<
    [DraftNotification, React.Dispatch<React.SetStateAction<DraftNotification>>]
>([{} as DraftNotification, () => {}])
export const ValidateErrorContext = createContext<
    [
        NotificationErrors,
        React.Dispatch<React.SetStateAction<NotificationErrors>>
    ]
>([{} as NotificationErrors, () => {}])

export const CurrenciesContext = createContext<[
    Array<SelectOption>,
    React.Dispatch<React.SetStateAction<Array<SelectOption>>>
]>([[], () => {}])
