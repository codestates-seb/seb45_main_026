import { createSlice } from "@reduxjs/toolkit";

const LoginInfoSlice = createSlice({
  name: "LoginInfo", // 단순히 이름 지정
  initialState: {
    loginInfo: { email: "", nickname: "" },
    oauth: { provider: "" },
    accessToken: {
      authorization: '',
      refresh: '',
    },
    myid: "",
  }, // 초기값 설정, 데이터 값의 형태를 설정 해놓으면 좋음.
  reducers: {
    // reducer들을 method 형태로 보관.
    setEmail: (state, action) => {
      // email이라는 method는 dispatch 할 때, Action Creator의 역할(자동으로 Action Creator로 설정)
      state.loginInfo.email = action.payload;
      // dispatch(setEmail('kimcoding@google.com')) 요청 하게 되면 => action 값에는 { payload : 'kimcoding' } 값이 들어옴
    },
    setNickname: (state, action) => {
      state.loginInfo.nickname = action.payload;
    },
    setAuthorizationToken: (state, action) => {
      // login 했을 때 accessToken 을 저장하는 method
      state.accessToken.authorization = action.payload;
    },
    setRefreshToken: (state, action) => {
      state.accessToken.refresh = action.payload;
    },
    setProvider: (state, action) => {
      // OAuth에 사용될 provider 값 저장
      state.oauth.provider = action.payload;
    },
    setMyid: (state, action) => {
      // 나의 memberId 를 저장
      state.myid = action.payload;
    },
    initLogin: (state) => {
      // 저장되어 있는 모든 값을 초기화
      state.loginInfo.email = "";
      state.loginInfo.nickname='';
      state.token = "";
      state.myid = "";
    },
  },
});

export default LoginInfoSlice;
export const { 
  setEmail, 
  setNickname, 
  setAuthorizationToken, 
  setRefreshToken, 
  setProvider,
  setMyid,
  initLogin
} = LoginInfoSlice.actions;