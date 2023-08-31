import React from "react";
import { styled } from "styled-components";
import { useSelector } from "react-redux";
import { PageContainer,MainContainer } from "../../atoms/layouts/PageContainer";
import tokens from "../../styles/tokens.json";
import VerticalItem from "../../components/contentListItems/VerticalItem";
import HorizonItem from "../../components/contentListItems/HorizonItem";
import CategoryFilter from "../../components/filters/CategoryFilter";

const globalTokens = tokens.global;

const ProfileContainer = styled.div`
    width: 100%;
    display: flex;
    flex-direction: row;
    gap: ${globalTokens.Spacing20.value}px;
    margin: ${globalTokens.Spacing36.value}px 0;
`
const ProfileImg = styled.img`
    max-height: 130px;
    height: auto;
    width: auto;
`
const ImgContainer = styled.div`
    width: 130px;
    height: 130px;
    min-width: 130px;
    border-radius: ${globalTokens.ProfileRadius.value}px;
    background-color: ${globalTokens.White.value};
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;
`
const InforContainer = styled.div`
    height: 130px;
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    gap: ${globalTokens.Spacing8.value}px;
`
const ChannelTitle = styled.h1`
  height: 30px;
  font-size: ${globalTokens.Heading4.value}px;
  font-weight: ${globalTokens.Bold.value};
`
const ChannelDescription = styled.div`
  height: 80px;
  flex-grow: 1;
  padding: ${globalTokens.Spacing4.value}px;
  background-color: lightgray;
  border-radius: ${globalTokens.RegularRadius.value}px;
`
const JustDiv = styled.div`
  height: 300px;
`


export default function ChannelPage() {
  const isDark = useSelector((state) => state.uiSetting.isDark);
    return (
      <PageContainer isDark={isDark}>
        <MainContainer>
          <ProfileContainer>
            <ImgContainer>
              <ProfileImg src="https://avatars.githubusercontent.com/u/50258232?v=4" />
            </ImgContainer>
            <InforContainer>
              <ChannelTitle>HyerimKimm</ChannelTitle>
              <ChannelDescription>
                안녕하세요 안녕하세요 안녕하세요 안녕하세요 안녕하세요
                안녕하세요 안녕하세요
                안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요
              </ChannelDescription>
            </InforContainer>
          </ProfileContainer>
          <CategoryFilter />
          <JustDiv></JustDiv>
        </MainContainer>
      </PageContainer>
    );
}