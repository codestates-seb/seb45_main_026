import "./App.css";
import { useEffect, useMemo } from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { setBrowserWidth } from "./redux/createSlice/UISettingSlice";
import MainPage from "./pages/contents/MainPage";
import LoginPage from "./pages/auth/LoginPage";
import MyProfilePage from "./pages/userInfo/MyProfilePage";
import Header, { MainPageHeader } from "./components/headers/Header";
import Footer from "./components/footers/Footer";
import ChannelPage from "./pages/contents/ChannelPage";
import DetailPage from "./pages/contents/DetailPage/DetailPage";
import CartPage from "./pages/contents/CartPage/CartPage";
import UploadPage from "./pages/contents/UploadPage";
import SignupPage from "./pages/auth/SignupPage";
import "./App.css";
import LectureListPage from "./pages/contents/LectureListPage";
import { getUserInfoService } from "./services/userInfoService";

function App() {
  const url = new URL(window.location.href);
  const dispatch = useDispatch();
  const tokens = useSelector(state=>state.loginInfo.accessToken);
  
  const handleResize = () => {
      dispatch(setBrowserWidth(window.innerWidth));
  };
  
  useMemo(() => {
    window.addEventListener("resize", handleResize);
  }, []);

  //웹을 실행했을 때 저장된 토큰이 있으면 토큰을 가지고 프로필 조회를 한다. 
  useEffect(()=>{
    if(tokens.authorization) {
      getUserInfoService(tokens.authorization).then((res)=>{
        //토큰이 유효하지 않으면 저장된 토큰을 삭제한다. 
      })
    }
  },[])

  return (
    <BrowserRouter>
      <Header />
      <Routes>
        <Route path="/" element={<MainPage/>}/>
        <Route path="/login" element={<LoginPage/>}/>
        <Route path="/signup" element={<SignupPage/>}/>
        <Route path="/MyProfile" element={<MyProfilePage/>}/>
        <Route path="/lecture" element={<LectureListPage/>}/> 
        <Route path="/videos/1" element={<DetailPage/>}/>
        <Route path="/channels/1" element={<ChannelPage/>}/>
        <Route path="/carts" element={<CartPage />} />
        <Route path="/upload" element={<UploadPage />} />
      </Routes>
      <Footer />
    </BrowserRouter>
  );
}

export default App;