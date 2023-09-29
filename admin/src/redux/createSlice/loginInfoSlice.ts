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
        }
    },
    reducers: {
        setIsLogin: (state, action) => {
            state.isLogin = action.payload;
        },
        setIsMyId: (state, action) => {
            state.myid = action.payload;
        }, 
        setLoginInfo: (state, action) => {
            state.loginInfo = action.payload;
        }
    }
})

export default loginInfoSlice;
export const { setIsLogin, setIsMyId, setLoginInfo } = loginInfoSlice.actions;