import { createAsyncThunk, createSlice } from '@reduxjs/toolkit'
import { message } from 'antd'
import { t } from 'i18next'
import { RootState } from '..'
import { AuthService } from '../../services/AuthService'
import { setCookie } from '../../utils/helpers'

/**
 * Init state 
 */
export const initialState = {
    isAuth: true,
    isCheking: false
}

/**
 * login async thunk
 */

export const login = createAsyncThunk(
    'auth/login',
    async (data: { phoneNumber: string; password: string,  }) => {
        const response = await AuthService.login(data)
    }
)
export const logout = createAsyncThunk(
    'auth/logout',
    async () => {
        const response = await AuthService.logout()
    }
)

/**
 * get user 
 */
// export const getUser = createAsyncThunk(
//     'auth/getUser', 
//     async () => {
//         //TODO doing
//         const response = []
//     }
// )


export const tempCheck = createAsyncThunk(
    'auth/tempCheck',
    async () => {
        const response = await AuthService.checkUser()
        return response
    }
)

export const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        logOut: (state) => {
            state.isAuth = false
            setCookie('accessToken', '', {
                'max-age': -1,
            })
            setCookie('refreshToken', '', {
                'max-age': -1,
            })
        },
        setIsAuth: (state, action) => {
            state.isAuth = action.payload
        },
        setChecking: (state, action) => {
            state.isCheking = action.payload
        }
    },
    extraReducers: (builder) => {
        builder.addCase(logout.fulfilled, (state) => {
            state.isAuth = false  
        })
        builder.addCase(login.rejected, (state) => {
            state.isAuth = false
            message.error(t('authorisation.wrong_login_or_password'))
        })
        builder.addCase(login.fulfilled, (state, action) => {
            state.isAuth = true
            state.isCheking = false
        })
        builder.addCase(tempCheck.pending, (state) => {
            state.isCheking = true
        })
        builder.addCase(tempCheck.fulfilled, (state) => {
            state.isAuth = true
            state.isCheking = false
        })
        builder.addCase(tempCheck.rejected, (state) => {
            state.isAuth = false
            state.isCheking = false
        })
    },
})

export const { setIsAuth, setChecking } = authSlice.actions

export const selectIsAuth = (state: RootState) => state.auth.isAuth
export const selectAuthChecking = (state: RootState) => state.auth.isCheking

export default authSlice.reducer
