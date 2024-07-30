import { configureStore } from "@reduxjs/toolkit";
import { filesApi } from "../services/FileService";
import { appSlice } from "./slices/appSlice";
import { authSlice } from "./slices/authSlice";
import { storageSlice } from "./slices/storage";
import { storageSliceRoles } from "./slices/storageRoles";

export const store = configureStore({
  reducer: {
    app: appSlice.reducer,
    storage: storageSlice.reducer,
    storageRoles: storageSliceRoles.reducer,
    auth: authSlice.reducer,

    [filesApi.reducerPath]: filesApi.reducer,
  },
  middleware: (getDefaultMiddleware) => {
    return getDefaultMiddleware({
        serializableCheck: false
    }).concat(filesApi.middleware)
    
}
});

// Вывод типов `RootState` и `AppDispatch` из стора.
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
