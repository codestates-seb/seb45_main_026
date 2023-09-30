import { createSlice } from '@reduxjs/toolkit';

const loginInfoSlice = createSlice({
    name: 'loginInfo',
    initialState: {
        isLogin: false,
        myid: '',
        loginInfo: {
            email: "",
            nickname: "", 
            grade: "", 
            imgUrl: "", 
            reward: "",
            authority: "",
        },
        accessToken: {
            authorization: '',
            refresh: '',
        }

    },
    reducers: {
        setIsLogin: (state, action) => {
            state.isLogin = action.payload;
        },
        setMyId: (state, action) => {
            state.myid = action.payload;
        }, 
        setLoginInfo: (state, action) => {
            state.loginInfo = action.payload;
        },
        setAccessToken: (state, action) => {
            state.accessToken = action.payload;
        }
    }
})

export default loginInfoSlice;
export const { setIsLogin, setMyId, setLoginInfo, setAccessToken } = loginInfoSlice.actions;