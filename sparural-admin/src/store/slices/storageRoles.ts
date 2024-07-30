import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit'
import { RootState } from '..'
import { SelectOption } from '../../components/simples/Selector/OptionItem'
import { AppService } from '../../services/AppService'
import { UserRoleType } from '../../types'

/**
 * Начальное состояние редьюсера EDIT/
 */
export const initialState = {
    roles: [] as UserRoleType[],
    rolesOptions: [] as SelectOption[],
}

/***
 * получаем роли
 */

export const loadRoles = createAsyncThunk('storage/loadRoles', async () => {
    let roles: Array<UserRoleType> = []
    try {
        const result = await AppService.rolesUser()
        roles = [...roles, ...result]
    } catch (error) {}
    return roles
})

export const storageSliceRoles = createSlice({
    name: 'storageRoles',
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder.addCase(loadRoles.fulfilled, (state, action2) => {
            state.roles = action2.payload
            state.rolesOptions = action2.payload.map((r) => {
                return {
                    value: r.id,
                    label: r.name,
                }
            })
        })
    },
})

export const appRoles = (state: RootState) => state.storageRoles.roles
export const appRolesOptions = (state: RootState) =>
    state.storageRoles.rolesOptions

export default storageSliceRoles.reducer
