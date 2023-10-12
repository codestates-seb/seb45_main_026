import {
  combineReducers,
  configureStore,
  getDefaultMiddleware,
} from "@reduxjs/toolkit";
import { FLUSH, PERSIST, PURGE, persistReducer } from "redux-persist";
import storage from "redux-persist/lib/storage";
import uiSettingSlice from "./createSlice/uiSettingSlice";
import loginInfoSlice from "./createSlice/loginInfoSlice";
import customerInfoSlice from "./createSlice/customerInfoSlice";

const reducers = combineReducers({
  uiSetting: uiSettingSlice.reducer,
  loginInfo: loginInfoSlice.reducer,
  customerInfo: customerInfoSlice.reducer,
});

const persistConfig = {
  key: "root",
  storage,
  whitelist: ["uiSetting", "loginInfo", "customerInfo"],
};

const persistedReducer = persistReducer(persistConfig, reducers);

const store = configureStore({
  reducer: persistedReducer,
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: [FLUSH, PERSIST, PURGE],
      },
    }),
});

type loginInfoType = {
  email: string;
  nickname: string;
  grade: string;
  imgUrl: string;
  reward: string;
  authority: string;
};

type accessTokenType = {
  authorization: string;
  refresh: string;
};

export type RootState = {
  uiSetting: {
    isDark: boolean;
  };
  loginInfo: {
    isLogin: boolean;
    myid: string;
    loginInfo: loginInfoType;
    accessToken: accessTokenType;
  };
  customerInfo: {
    roomId: string;
  };
};

export default store;
