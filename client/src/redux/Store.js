import storage from "redux-persist/lib/storage";
// import storageSession from 'redux-persist/lib/storage/session' // 세션스토리지 사용하고 싶을 때.
import { configureStore } from "@reduxjs/toolkit";
import { combineReducers } from "@reduxjs/toolkit";
import { persistReducer, FLUSH, PERSIST, PURGE } from "redux-persist";

import LoginInfoSlice from "./createSlice/LoginInfoSlice";
import UISettingSlice from "./createSlice/UISettingSlice";
import FilterSlice from "./createSlice/FilterSlice";
import VideoInfoSlice from "./createSlice/VideoInfoSlice";
import CartsSlice from "./createSlice/CartsSlice";
import ProblemSlice from "./createSlice/ProblemSlice";

const reducers = combineReducers({
  // 각각의 slice에 저장되어 있는 여러 reducer들을 하나의 reducer로 통합.
  loginInfo: LoginInfoSlice.reducer, // slice에 있는 state들을 사용하고 싶으면 이곳에 추가해야함. key는 원하는 이름(나중에 useSelector 했을때 부를 이름.)
  uiSetting: UISettingSlice.reducer,
  filterSlice: FilterSlice.reducer,
  videoInfo: VideoInfoSlice.reducer,
  cartSlice: CartsSlice.reducer,
  problemSlice: ProblemSlice.reducer,
});

const persistConfig = {
  key: "root",
  storage, // 어떤 스토리지 사용할건지 선택, session storage로도 가능함.
  whitelist: ["loginInfo", "videoInfo", "uiSetting"], // storage에 저장할 reducer를 선택
};

const persistedReducer = persistReducer(persistConfig, reducers); // 기존 reduecr에 persistConfing 내용을 적용시킨 새로운 reducer를 탄생

const store = configureStore({
  reducer: persistedReducer,
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: [FLUSH, PERSIST, PURGE],
      },
    }),
});

export default store;
