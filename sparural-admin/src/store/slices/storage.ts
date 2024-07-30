import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit'
import { RootState } from '..'
import { SelectOption } from '../../components/simples/Selector/OptionItem'
import { AppService } from '../../services/AppService'
import { ShopsService } from '../../services/ShopsService'
import { FetchCityType, MerchantFormatType, UserRoleType } from '../../types'

/**
 * Начальное состояние редьюсера EDIT/
 */
export const initialState = {
    cities: [] as FetchCityType[],
    citiesOptions: [] as SelectOption[],
    merchantFormats: [] as MerchantFormatType[]
}

/**
 * получаем города
 */

export const loadCities = createAsyncThunk('storage/loadCities', async () => {
    const limit = 100
    let offset = 0

    let cities: Array<FetchCityType> = []
    try {
        const result = await AppService.fetchCities({ offset, limit })
        cities = [...cities, ...result]
    } catch (error) {}

    return cities
})

/**
 * fetch merchant formats
 */
export const fetchMerchantFormats = createAsyncThunk('storage/fetchMerchantFormats', async () => {
    const limit = 100
    let offset = 0

        const response = await ShopsService.fetchMerchantFormats({offset, limit})
        return response

})

export const storageSlice = createSlice({
    name: 'storage',
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder.addCase(loadCities.fulfilled, (state, action) => {
            state.cities = action.payload
            state.citiesOptions = action.payload.map((c) => {
                return {
                    value: c.id,
                    label: c.name,
                }
            })
        })
        builder.addCase(fetchMerchantFormats.fulfilled, (state, action) => {
            state.merchantFormats = action.payload
        })
    },
})

/***
 * получаем роли
 */

// export const loadRoles = createAsyncThunk('storage/loadRoles', async () => {
//     let roles: Array<UserRoleType> = []
//     try {
//         const result = await AppService.rolesUser()
//         roles = [...roles, ...result]
//     } catch (error) {}
//     return roles
// })

// export const storageSliceRoles = createSlice({
//     name: 'storage',
//     initialState,
//     reducers: {},
//     extraReducers: (builder) => {
//         builder.addCase(loadRoles.fulfilled, (state, action2) => {
//             state.roles = action2.payload
//             state.rolesOptions = action2.payload.map((r) => {
//                 return {
//                     value: r.id,
//                     label: r.name,
//                 }
//             })
//         })
//     },
// })

export const appCities = (state: RootState) => state.storage.cities
export const appCitiesOptions = (state: RootState) =>
    state.storage.citiesOptions

export const selectShopFormats = (state: RootState) => state.storage.merchantFormats
// export const appRoles = (state: RootState) => state.storage.roles
// export const appRolesOptions = (state: RootState) => state.storage.rolesOptions

export default storageSlice.reducer
