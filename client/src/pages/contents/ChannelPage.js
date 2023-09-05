import React,{useState} from "react";
import { styled } from "styled-components";
import { useSelector } from "react-redux";
import { PageContainer,MainContainer } from "../../atoms/layouts/PageContainer";
import tokens from "../../styles/tokens.json";
import ChannelNav from "../../components/contentListItems/ChannelNav";
import ChannelHome from "../../components/contentListItems/ChannelHome";
import ChannelList from "../../components/contentListItems/ChannelList";
import ChannelNotice from "../../components/contentListItems/ChannelNotice";
import axios from "axios";
import Setting from "../../components/contentListItems/Setting";

const globalTokens = tokens.global;

const ChannelMainContainer = styled(MainContainer)`
  min-width: 600px;
  border: none;
`
const ProfileContainer = styled.div`
  width: 100%;
  display: flex;
  flex-direction: row;
  flex-wrap: nowrap;
  /* gap: ${globalTokens.Spacing20.value}px; */
  margin: ${globalTokens.Spacing36.value}px 0;
`;
const ProfileImg = styled.img`
    width: 130px;
`
const ImgContainer = styled.div`
    width: 130px;
    height: 130px;
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
`;
const ChannelDescription = styled.div`
  height: 80px;
  flex-grow: 1;
  padding: ${globalTokens.Spacing4.value}px;
  background-color: lightgray;
  border-radius: ${globalTokens.RegularRadius.value}px;
`

export default function ChannelPage() {
  const isDark = useSelector((state) => state.uiSetting.isDark);

  const [navigate, setNavigate] = useState(0)
  
    return (
      <PageContainer isDark={isDark}>
        <ChannelMainContainer>
          <ProfileContainer>
            <ImgContainer>
              <ProfileImg src="https://avatars.githubusercontent.com/u/50258232?v=4"/>
            </ImgContainer>
            <InforContainer>
              <ChannelTitle>HyerimKimm</ChannelTitle>
              <ChannelDescription>안녕하세요</ChannelDescription>
            </InforContainer>
          </ProfileContainer>
          <ChannelNav navigate={navigate} setNavigate={setNavigate} />
          { navigate===0? <ChannelHome/>
            : navigate===1? <ChannelList/>
            : navigate===2?  <ChannelNotice/>
            : <Setting/> }
        </ChannelMainContainer>
      </PageContainer>
    );
}