import "./App.css";
import { useEffect, useMemo, useState } from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import {
  setBrowserWidth,
} from "./redux/createSlice/UISettingSlice";
import MainPage from "./pages/contents/MainPage";
import LoginPage from "./pages/auth/LoginPage";
import Header from "./components/headers/Header";
import Footer from "./components/footers/Footer";
import ChannelPage from "./pages/contents/ChannelPage";
import DetailPage from "./pages/contents/DetailPage/DetailPage";
import CartPage from "./pages/contents/CartPage/CartPage";
import UploadPage from "./pages/contents/UploadPage";
import SignupPage from "./pages/auth/SignupPage";
import "./App.css";
import ProblemPage from "./pages/contents/ProblemPage";
import LectureListPage from "./pages/contents/LectureListPage";
import { getUserInfoService } from "./services/userInfoService";
import {
  setIsLogin,
  setLoginInfo,
  setMyid,
  setProvider,
  setToken,
} from "./redux/createSlice/LoginInfoSlice";
import FindPasswordPage from "./pages/auth/FindPasswordPage";
import PurchasedListPage from "./pages/contents/PurchasedListPage";
import UpdatePasswordPage from "./pages/auth/UpdatePasswordPage";
import ChannelListPage from "./pages/contents/ChannelListPage";
import { AlertModal } from './atoms/modal/Modal';
import { getNewAuthorizationService } from "./services/authServices";
import Loading from "./atoms/loading/Loading";

function App() {
  const dispatch = useDispatch();
  const tokens = useSelector((state) => state.loginInfo.accessToken);
  const [ is로그인실패모달, setIs로그인실패모달 ] = useState(false);

  const handleResize = () => {
    dispatch(setBrowserWidth(window.innerWidth));
  };

  useMemo(() => {
    window.addEventListener("resize", handleResize);
  }, []);

  //웹을 실행했을 때 저장된 토큰이 있으면 토큰을 가지고 프로필 조회를 한다.
  //authorization이 만료되었으면 refresh 토큰을 통해서 authorization 토큰을 갱신한다.
  //authorization 갱신에 성공하면 다시 프로필 조회를 한다.
  //authorization 갱신에도 실패하면 강제 로그아웃 한다.
  useEffect(() => {
    if (!(tokens.authorization === "")) {
      getUserInfoService(tokens.authorization).then((res) => {
        //토큰이 유효하면 회원 정보를 dispatch 후, isLogin을 true로 설정한다.
        if (res.status === 'success') {
          dispatch(setMyid(res.data.memberId));
          dispatch(
            setLoginInfo({
              email: res.data.email, 
              nickname: res.data.nickname,
              grade: res.data.grade,
              imgUrl: res.data.imgUrl,
              reward: res.data.reward
            }));
          dispatch(setIsLogin(true));
        } else if(res.data==='만료된 토큰입니다.'){ //토큰 만료 에러인 경우
        //프로필정보 조회 API, 토큰 재발급 API를 모두 실패하면 로그아웃 처리한다. 
            //토큰 refresh 요청
            getNewAuthorizationService(tokens.refresh).then((res)=>{
              if(res.status==='success') { //토큰 재발급 성공
                dispatch(setToken({
                  ...tokens,
                  authorization: res.data
                }));
              } else { //토큰 재발급 실패
                //로그아웃 처리 로직
                dispatch(setMyid(''));
                dispatch(setLoginInfo({
                email: "",
                nickname: "",
                grade: "",
                imgUrl: "",
                reward: "" 
              }));
                dispatch(setProvider(''));
                dispatch(setIsLogin(false));
              }
            })
         } else { //토큰 만료 에러가 아닌 경우
          dispatch(setMyid(''));
          dispatch(setLoginInfo({
            email: "",
            nickname: "",
            grade: "",
            imgUrl: "",
            reward: "" 
          }));
          dispatch(setProvider(''));
          dispatch(setIsLogin(false));
        }
      });
    }
  },[tokens]);

  return (
    <BrowserRouter>
      <AlertModal
        isModalOpen={is로그인실패모달}
        setIsModalOpen={setIs로그인실패모달}
        isBackdropClickClose={true}
        content='로그인 정보가 만료되었습니다. 다시 로그인하세요.'
        buttonTitle='확인'
        handleButtonClick={()=>{ setIs로그인실패모달(false) }}/>
      <Header />
      <Routes>
        <Route path="/" element={<MainPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/findPassword" element={<FindPasswordPage />} />
        <Route path="/findPassword/updatePassword" element={<UpdatePasswordPage/>}/>
        <Route path="/lecture" element={<LectureListPage />} />
        <Route path="/videos/:videoId" element={<DetailPage />} />
        <Route path="/channels/:userId" element={<ChannelPage/>} />
        <Route path="/carts" element={<CartPage />} />
        <Route path="/upload" element={<UploadPage />} />
        <Route path="/videos/1/problems" element={<ProblemPage />} />
        <Route path="/purchased" element={<PurchasedListPage />} />
        <Route path="/channellist" element={<ChannelListPage />} />
      </Routes>
      <Footer />
    </BrowserRouter>
  );
}

export default App;
