import React, { useEffect, useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  MainPageContainer,
  LightContainer,
  DarkContainer,
  FirstPageBackgroundContainer,
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
  const dispatch = useDispatch();
  const navigate = useNavigate();

  //로그인한 상태이면 lecture page로 이동
  useEffect(() => {
    if (isLogin) {
      navigate("/lecture");
    }
  });

  useEffect(() => {
    const wheelHandler = (e) => {
      e.preventDefault();
      const { deltaY } = e;
      const { scrollTop } = outerRef.current;
      const pageHeight = window.innerHeight;

      if (deltaY > 0) {
        //스크롤 내릴 때
        if (scrollTop >= 0 && scrollTop < pageHeight) {
          //현재 1페이지
          outerRef.current.scrollTo({
            top: pageHeight,
            left: 0,
            behavior: "smooth",
          });
        } else if (scrollTop >= pageHeight && scrollTop < pageHeight * 2) {
          //현재 2페이지
          outerRef.current.scrollTo({
            top: pageHeight * 2,
            left: 0,
            behavior: "smooth",
          });
        } else if (scrollTop >= pageHeight * 2 && scrollTop < pageHeight * 3) {
          //현재 3페이지
          outerRef.current.scrollTo({
            top: pageHeight * 3,
            left: 0,
            behavior: "smooth",
          });
        } else {
          //현재 4페이지
          outerRef.current.scrollTo({
            top: pageHeight * 3,
            left: 0,
            behavior: "smooth",
          });
        }
      } else {
        //스크롤 올릴 때
        if (scrollTop >= 0 && scrollTop < pageHeight) {
          outerRef.current.scrollTo({
            //현재 1페이지
            top: 0,
            left: 0,
            behavior: "smooth",
          });
        } else if (scrollTop >= pageHeight && scrollTop < pageHeight * 2) {
          outerRef.current.scrollTo({
            //현재 2페이지
            top: 0,
            left: 0,
            behavior: "smooth",
          });
        } else if (scrollTop >= pageHeight * 2 && scrollTop < pageHeight * 3) {
          outerRef.current.scrollTo({
            //현재 3페이지
            top: pageHeight,
            left: 0,
            behavior: "smooth",
          });
        } else {
          outerRef.current.scrollTo({
            //현재 4페이지
            top: pageHeight * 2,
            left: 0,
            behavior: "smooth",
          });
        }
      }
    };
    const outerRefCurrent = outerRef.current;
    outerRefCurrent.addEventListener("wheel", wheelHandler);
    return () => {
      outerRefCurrent.removeEventListener("wheel", wheelHandler);
    };
  }, []);

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
        <MainFourthPageItems/>
      </DarkContainer>
    </MainPageContainer>
  );
};

export default MainPage;
