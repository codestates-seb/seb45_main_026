import { combineReducers, configureStore, getDefaultMiddleware } from "@reduxjs/toolkit";
import uiSettingSlice from "./createSlice/uiSettingSlice";
import storage from "redux-persist/lib/storage";
import { FLUSH, PERSIST, PURGE, persistReducer } from 'redux-persist';
import loginInfoSlice from "./createSlice/loginInfoSlice";

const reducers = combineReducers({
    uiSetting : uiSettingSlice.reducer,
    loginInfo : loginInfoSlice.reducer,
});

const persistConfig = {
    key: "root",
    storage,
    whitelist: [ 'uiSetting', 'loginInfo' ]
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

type loginInfoType = {
    email: string;
    nickname: string;
    grade: string;
    imgUrl: string;
    reward: string;
    authority: string;
}

export type RootState = {
    uiSetting: {
        isDark: boolean;
    },
    loginInfo: {
        isLogin: boolean;
        myid: string;
        loginInfo: loginInfoType;
    }
}

export default store;