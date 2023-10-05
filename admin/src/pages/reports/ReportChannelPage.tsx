import React from "react";
import {
  MainContainer,
  PageContainer,
} from "../../atoms/layouts/PageContainer";
import { useSelector } from "react-redux";
import { RootState } from "../../redux/Store";
import { PageTitle } from "../../styles/PageTitle";
import NavBar from "../../components/navBar/NavBar";

const ReportChannelPage = () => {
  const isDark = useSelector((state: RootState) => state.uiSetting.isDark);
  return (
    <PageContainer isDark={isDark}>
      <MainContainer isDark={isDark}>
        <PageTitle isDark={isDark}>신고 내역 관리</PageTitle>
        <NavBar NavType="채널" />
      </MainContainer>
    </PageContainer>
  );
};

export default ReportChannelPage;
