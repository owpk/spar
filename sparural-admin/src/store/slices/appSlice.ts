import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import { RootState } from '..'
import { QuestionRatingStoreType } from '../../types'
type StateType = {
    isLoading: boolean
}
/**
 * Начальное состояние редьюсера App/
 */
export const initialState: StateType = {
    isLoading: false,
}

export const appSlice = createSlice({
    name: 'app',
    initialState,
    reducers: {
        setLoading: (state, action: PayloadAction<boolean>) => {
            state.isLoading = action.payload
        }
    },

    extraReducers: (builder) => {},
})

export const { setLoading } = appSlice.actions

export const selectAppLoading = (state: RootState) => state.app.isLoading
