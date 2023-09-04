import { createSlice } from "@reduxjs/toolkit";

const LoginInfoSlice = createSlice({
  name: "LoginInfo", // 단순히 이름 지정
  initialState: {
    isLogin: false,
    loginInfo: { email: "", nickname: "" },
    oAuthProvider: "", //google, github, kakao
    accessToken: {
      authorization: "",
      refresh: "",
    },
    myid: "",
    findPasswordEmail: '',
  }, // 초기값 설정, 데이터 값의 형태를 설정 해놓으면 좋음.
  reducers: {
    // reducer들을 method 형태로 보관.
    setIsLogin: (state, action) => {
      state.isLogin = action.payload;
    },
    setLoginInfo: (state, action) => {
      state.loginInfo = action.payload;
    },
    setToken: (state, action) => {
      state.accessToken = action.payload;
    },
    setProvider: (state, action) => {
      // OAuth에 사용될 provider 값 저장
      state.oAuthProvider = action.payload;
    },
    setMyid: (state, action) => {
      // 나의 memberId 를 저장
      state.myid = action.payload;
    },
    setFindPasswordEmail: (state, action) => {
      //비밀번호 변경 시 인증한 이메일을 저장
      state.findPasswordEmail = action.payload;
    }
  },
});

export default LoginInfoSlice;
export const { 
  setIsLogin,
  setLoginInfo,
  setToken,
  setProvider,
  setMyid,
  setFindPasswordEmail
} = LoginInfoSlice.actions;