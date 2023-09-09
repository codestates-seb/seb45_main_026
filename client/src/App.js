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
import CourseUploadPage from "./pages/contents/CourseUploadPage";
import SignupPage from "./pages/auth/SignupPage";
import "./App.css";
import ProblemPage from "./pages/contents/ProblemPage";
import LectureListPage from "./pages/contents/LectureListPage";
import { getUserChannelInfoService, getUserInfoService } from "./services/userInfoService";
import {
  setChannelInfo,
  setIsLogin,
  setLoginInfo,
  setMyid,
} from "./redux/createSlice/LoginInfoSlice";
import FindPasswordPage from "./pages/auth/FindPasswordPage";
import PurchasedListPage from "./pages/contents/PurchasedListPage";
import UpdatePasswordPage from "./pages/auth/UpdatePasswordPage";
import ChannelListPage from "./pages/contents/ChannelListPage";
import { AlertModal } from './atoms/modal/Modal';
import ProblemUploadPage from "./pages/contents/ProblemUploadPage";
import { useLogout } from "./hooks/useLogout";
import { useToken } from "./hooks/useToken";

function App() {
  const dispatch = useDispatch();
  const tokens = useSelector((state) => state.loginInfo.accessToken);
  const myid = useSelector((state)=>state.loginInfo.myid);
  const refreshToken = useToken();
  const logout = useLogout();
  const [ is로그인실패모달, setIs로그인실패모달 ] = useState(false);

  const handleResize = () => {
    dispatch(setBrowserWidth(window.innerWidth));
  };

  useMemo(() => {
    window.addEventListener("resize", handleResize);
  }, []);

  useEffect(() => {
    if (!(tokens.authorization === "")) {
      getUserInfoService(tokens.authorization).then((res) => {
        if (res.status === 'success') {
          //유저 정보 조회에 성공 -> 유저 정보 dispatch
          dispatch(setMyid(res.data.memberId));
          dispatch(
            setLoginInfo({
              email: res.data.email, 
              nickname: res.data.nickname,
              grade: res.data.grade,
              imgUrl: res.data.imageUrl,
              reward: res.data.reward
            }));
          dispatch(setIsLogin(true));
        } else if(res.data==='만료된 토큰입니다.'){ 
          //토큰 만료 에러인 경우 토큰 재발급 실행
            refreshToken();
         } else { //토큰 만료 에러가 아닌데 어쨋든 에러남
          logout();
        }
      });
    }
  },[tokens]);

  useEffect(()=>{
    getUserChannelInfoService(tokens.authorization, myid).then((response)=>{
      if(response.status==='success') {
          const newChannelName = response.data.channelName;
          const newDescription = response.data.description;
          dispatch(setChannelInfo({
            channelName: newChannelName!==null?newChannelName:"",
            description: newDescription!==null?newDescription:"",
          }))
      } else {

      }
  })
  },[myid])

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
        <Route path="/upload/course" element={<CourseUploadPage />} />
        <Route path="/videos/:videoId/problems/upload" element={<ProblemUploadPage />} />
        <Route path="/videos/:videoId/problems" element={<ProblemPage />} />
        <Route path="/purchased" element={<PurchasedListPage />} />
        <Route path="/channellist" element={<ChannelListPage />} />
      </Routes>
      <Footer />
    </BrowserRouter>
  );
}

export default App;
