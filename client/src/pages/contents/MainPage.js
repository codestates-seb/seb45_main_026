import React, { useEffect, useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  MainPageContainer,
  LightContainer,
  DarkContainer,
  FirstPageBackgroundContainer,
  FourthPageBackgroundContainer,
} from "./MainPage.style";
import { setLocation } from "../../redux/createSlice/UISettingSlice";
import MainPageFirstItem from "../../components/mainPageItems/MainPageItems";
import { useLocation, useMatch, useNavigate } from "react-router-dom";
import MainSecondPageItems from "../../components/mainPageItems/MainSecondPageItems";
import MainThirdPageItems from "../../components/mainPageItems/MainThirdPageItems";
import MainFourthPageItems from "../../components/mainPageItems/MainFourthPageItems";

const MainPage = () => {
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const isLogin = useSelector((state) => state.loginInfo.isLogin);
  const outerRef = useRef();
  const navigate = useNavigate();

  //로그인한 상태이면 lecture page로 이동
  useEffect(() => {
    if (isLogin) {
      navigate("/lecture");
    }
  });

  return (
    <MainPageContainer ref={outerRef}>
      <LightContainer isDark={isDark}>
        <FirstPageBackgroundContainer>
          <MainPageFirstItem />
        </FirstPageBackgroundContainer>
      </LightContainer>
      <DarkContainer isDark={isDark}>
        <MainSecondPageItems/>
      </DarkContainer>
      <LightContainer isDark={isDark}>
        <MainThirdPageItems/>
      </LightContainer>
      <DarkContainer isDark={isDark}>
        <FourthPageBackgroundContainer>
          <MainFourthPageItems/>
        </FourthPageBackgroundContainer>
      </DarkContainer>
    </MainPageContainer>
  );
};

export default MainPage;
