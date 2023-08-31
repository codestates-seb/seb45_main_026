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
    display: flex;
    flex-direction: row;
    gap: ${globalTokens.Spacing20.value}px;
    width: 100%;
    padding: ${globalTokens.Spacing32.value}px;
`
const ProfileImg = styled.img`
    max-height: 130px;
    height: auto;
    width: auto;
`
const ImgContainer = styled.div`
    width: 130px;
    height: 130px;
    border-radius: ${globalTokens.ProfileRadius.value}px;
    background-color: white;
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;
    border: 1px solid black;
`
const InforContainer = styled.div`
    height: 130px;
    flex-grow: 1;
    padding: 5px;
    border: 1px solid black;
`
const ChannelTitle = styled.h1`
  height: 30px;
  font-size: ${globalTokens.Heading4.value}px;
  font-weight: ${globalTokens.Bold.value};
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
            </InforContainer>
          </ProfileContainer>
          <VerticalItem />
          <HorizonItem />
          <CategoryFilter />
          <JustDiv></JustDiv>
        </MainContainer>
      </PageContainer>
    );
}