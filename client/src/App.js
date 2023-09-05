import "./App.css";
import { useEffect, useMemo } from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import {
  setBrowserWidth,
} from "./redux/createSlice/UISettingSlice";
import MainPage from "./pages/contents/MainPage";
import LoginPage from "./pages/auth/LoginPage";
import MyProfilePage from "./pages/userInfo/MyProfilePage";
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
  setToken,
} from "./redux/createSlice/LoginInfoSlice";
import useConfirm from "./hooks/useConfirm";
import FindPasswordPage from "./pages/auth/FindPasswordPage";
import PurchasedListPage from "./pages/contents/PurchasedListPage";
import UpdatePasswordPage from "./pages/auth/UpdatePasswordPage";
import ChannelListPage from "./pages/contents/ChannelListPage";

function App() {
  const dispatch = useDispatch();
  const tokens = useSelector((state) => state.loginInfo.accessToken);
  const tokenFinishConfirm = useConfirm(
    "토큰이 만료되었거나, 서버 오류로 로그아웃 되었습니다."
  );

  const handleResize = () => {
    dispatch(setBrowserWidth(window.innerWidth));
  };

  useMemo(() => {
    window.addEventListener("resize", handleResize);
  }, []);

  //웹을 실행했을 때 저장된 토큰이 있으면 토큰을 가지고 프로필 조회를 한다.
  useEffect(() => {
    if (!(tokens.authorization === "")) {
      getUserInfoService(tokens.authorization).then((res) => {
        if (res.status === 'success') {
          console.log('success')
          //토큰이 유효하면 회원 정보를 dispatch 후, isLogin을 true로 설정한다.
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
        } else {
          //토큰이 유효하지 않으면 저장된 토큰, 로그인 정보를 삭제하고 isLogin을 false로 설정한다.
          tokenFinishConfirm();
          dispatch(setToken({ authorization: "", refresh: "" }));
          dispatch(setMyid(0))
          dispatch(setLoginInfo({ 
            email: "", 
            nickname: "",
            grade: "",
            imgUrl: "",
            reward: 0
          }));
          dispatch(setIsLogin(false));
        }
      });
    }
  });

  return (
    <BrowserRouter>
      <Header />
      <Routes>
        <Route path="/" element={<MainPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/findPassword" element={<FindPasswordPage />} />
        <Route path="/findPassword/updatePassword" element={<UpdatePasswordPage/>}/>
        <Route path="/lecture" element={<LectureListPage />} />
        <Route path="/videos/1" element={<DetailPage />} />
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
