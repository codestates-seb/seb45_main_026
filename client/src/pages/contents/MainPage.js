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
import { Swiper, SwiperSlide } from 'swiper/react';
import 'swiper/css';
import 'swiper/css/pagination';
import { Pagination, A11y, Mousewheel } from 'swiper/modules';

const MainPage = () => {
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const isLogin = useSelector((state) => state.loginInfo.isLogin);
  const navigate = useNavigate();

  //로그인한 상태이면 lecture page로 이동
  useEffect(() => {
    if (isLogin) {
      navigate("/lecture");
    }
  });

  return (
    <MainPageContainer>
      <Swiper 
        modules={[Pagination, A11y, Mousewheel ]}
        spaceBetween={0} 
        mousewheel={true}
        direction="vertical"
        speed={1000}>
        <SwiperSlide>
          <LightContainer isDark={isDark}>
            <FirstPageBackgroundContainer>
              <MainPageFirstItem />
            </FirstPageBackgroundContainer>
          </LightContainer>
        </SwiperSlide>
        <SwiperSlide>
          <DarkContainer isDark={isDark}>
            <MainSecondPageItems/>
          </DarkContainer>
        </SwiperSlide>
        <SwiperSlide>
          <LightContainer isDark={isDark}>
            <MainThirdPageItems/>
          </LightContainer>
        </SwiperSlide>
        <SwiperSlide>
          <DarkContainer isDark={isDark}>
            <FourthPageBackgroundContainer>
              <MainFourthPageItems/>
            </FourthPageBackgroundContainer>
          </DarkContainer>
        </SwiperSlide>
      </Swiper>
    </MainPageContainer>
  );
};

export default MainPage;
