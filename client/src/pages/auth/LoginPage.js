import React, { useEffect } from "react";
import { PageContainer } from "../../atoms/layouts/PageContainer";
import { useDispatch, useSelector } from "react-redux";
import { styled } from "styled-components";
import Login from "../../components/loginPageItems/Login";
import tokens from "../../styles/tokens.json";
import { setLocation } from "../../redux/createSlice/UISettingSlice";
import Loading from "../../atoms/loading/Loading";

const globalTokens = tokens.global;

export const LoginPageContainer = styled(PageContainer)`
  width: 100vw;
  height: 700px;
  padding: ${globalTokens.Spacing40.value}px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
`;

const LoginPage = () => {
  const dispatch = useDispatch();
  const isDark = useSelector((state) => state.uiSetting.isDark);

//   useEffect(() => {
//     const url = new URL(window.location.href);
//     dispatch(setLocation(url.pathname));
//   }, []);

  return (
    <LoginPageContainer isDark={isDark}>
      <Loading isLoading={true}/>
      <Login />
    </LoginPageContainer>
  );
};

export default LoginPage;
