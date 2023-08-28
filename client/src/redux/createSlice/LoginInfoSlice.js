import { createSlice } from "@reduxjs/toolkit";

const LoginInfoSlice = createSlice({
  name: "LoginInfo", // 단순히 이름 지정
  initialState: {
    loginInfo: { email: "", password: "" },
    oauth: { provider: "" },
    accessToken: "",
    myid: "",
  }, // 초기값 설정, 데이터 값의 형태를 설정 해놓으면 좋음.
  reducers: {
    // reducer들을 method 형태로 보관.
    email: (state, action) => {
      // email이라는 method는 dispatch 할 때, Action Creator의 역할(자동으로 Action Creator로 설정)
      state.loginInfo.email = action.payload;
      // dispatch(email('kimcoding@google.com')) 요청 하게 되면 => action 값에는 { payload : 'kimcoding' } 값이 들어옴
    },
    password: (state, action) => {
      // login 에서의 password 를 저장
      state.loginInfo.password = action.payload;
      // action.payload를 initialState 값에 해당되는 key의 value에 껴놓는다고 생각하면 됨.
    },
    logintoken: (state, action) => {
      // login 했을 때 accessToken 을 저장하는 method
      state.accessToken = action.payload;
    },
    provider: (state, action) => {
      // OAuth에 사용될 provider 값 저장
      state.oauth.provider = action.payload;
    },
    myid: (state, action) => {
      // 나의 memberId 를 저장
      state.myid = action.payload;
    },
    initLogin: (state) => {
      // 저장되어 있는 모든 값을 초기화
      state.loginInfo.email = "";
      state.loginInfo.password = "";
      state.token = "";
      state.myid = "";
    },
  },
});

export default LoginInfoSlice;
export const { email, password, logintoken, provider, myid, initLogin } =
  LoginInfoSlice.actions;
