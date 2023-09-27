import { combineReducers, configureStore, getDefaultMiddleware } from "@reduxjs/toolkit";
import uiSettingSlice from "./createSlice/uiSettingSlice";
import storage from "redux-persist/lib/storage";
import { FLUSH, PERSIST, PURGE, persistReducer } from 'redux-persist';

const reducers = combineReducers({
    uiSetting : uiSettingSlice.reducer,
});

const persistConfig = {
    key: "root",
    storage,
    whitelist: [ 'uiSetting' ]
};

const persistedReducer = persistReducer(persistConfig, reducers);

const store = configureStore({
    reducer: persistedReducer,
    middleware: (getDefaultMiddleware) => 
        getDefaultMiddleware({
            serializableCheck: {
                ignoredActions: [FLUSH,PERSIST,PURGE],
            }
        })
})

export type RootState = {
    uiSetting: {
        isDark: boolean;
    }
}

export default store;