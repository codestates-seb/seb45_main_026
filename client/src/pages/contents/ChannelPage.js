import React from "react";
import { styled } from "styled-components";
import { useSelector } from "react-redux";
import { PageContainer,MainContainer } from "../../atoms/layouts/PageContainer";
import tokens from "../../styles/tokens.json";

const globalTokens = tokens.global;

const ProfileContainer = styled.div`
    display: flex;
    flex-direction: row;
    gap: ${globalTokens.Spacing20.value}px;
`
const ProfileImg = styled.img`
    max-height: 130px;
    max-width: 130px;
    height: auto;
    width: auto;
`
const ImgContainer = styled.div`
    width: 130px;
    height: 130px;
    border-radius: ${globalTokens.PrifileRadius.value}px;
    background-color: white;
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;
`
const InforContainer = styled.div`
    height: 130px;
    flex-grow: 1;
    border: 1px solid black;
`

export default function ChannelPage() {
    const isDark = useSelector((state) => state.uiSetting.isDark);
    return (
      <PageContainer isDark={isDark}>
        <MainContainer>
          <ProfileContainer>
            <ImgContainer>
                <ProfileImg src="https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg" />
            </ImgContainer>
            <InforContainer></InforContainer>
          </ProfileContainer>
        </MainContainer>
      </PageContainer>
    );
}