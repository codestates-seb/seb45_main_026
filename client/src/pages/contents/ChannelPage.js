import React,{useEffect, useState} from "react";
import { styled } from "styled-components";
import { useSelector } from "react-redux";
import { PageContainer,MainContainer } from "../../atoms/layouts/PageContainer";
import tokens from "../../styles/tokens.json";
import ChannelNav from "../../components/contentListItems/ChannelNav";
import ChannelHome from "../../components/contentListItems/ChannelHome";
import ChannelList from "../../components/contentListItems/ChannelList";
import ChannelNotice from "../../components/contentListItems/ChannelNotice";
import axios from "axios";
import frofileGray from "../../assets/images/icons/profile/profileGray.svg"

const globalTokens = tokens.global;

const ChannelMainContainer = styled(MainContainer)`
  min-width: 600px;
  border: none;
`
const ProfileContainer = styled.div`
  width: 100%;
  display: flex;
  flex-direction: row;
  gap: ${globalTokens.Spacing20.value}px;
  margin: ${globalTokens.Spacing36.value}px 0;
`
export const ProfileImg = styled.img`
    max-height: 130px;
    height: auto;
    width: auto;
`
export const ImgContainer = styled.div`
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
    min-height: 130px;
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
const ChannelSubscribers = styled.div`
  height: 20px;
  font-size: ${globalTokens.BodyText.value}px;
  font-weight: ${globalTokens.Bold.value};
`
const ChannelDescription = styled.div`
  flex-grow: 1;
  padding: ${globalTokens.Spacing8.value}px;
  background-color: lightgray;
  border-radius: ${globalTokens.RegularRadius.value}px;
`



export default function ChannelPage() {
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const [navigate, setNavigate] = useState(0)
  const [channelInfor,setChannelInfor]=useState({})
  useEffect(() => {
    axios.get("https://api.itprometheus.net/channels/4")
      .then(res => setChannelInfor(res.data.data))
      .catch(err=>console.log(err))
  },[])
    return (
      <PageContainer isDark={isDark}>
        <ChannelMainContainer>
          <ProfileContainer>
            <ImgContainer>
              <ProfileImg src={channelInfor.imageUrl?channelInfor.imageUrl:frofileGray} />
            </ImgContainer>
            <InforContainer>
              <ChannelTitle>{channelInfor.channelName}</ChannelTitle>
              <ChannelSubscribers>구독자 {channelInfor.subscribers}명</ChannelSubscribers>
              <ChannelDescription>{channelInfor.description?channelInfor.description:"아직 채널 소개가 없습니다"}</ChannelDescription>
            </InforContainer>
          </ProfileContainer>
          <ChannelNav navigate={navigate} setNavigate={setNavigate} />
          {navigate === 0 ? <ChannelHome channelInfor={channelInfor} />:navigate===1?<ChannelList channelInfor={channelInfor}/>:<ChannelNotice/>}
        </ChannelMainContainer>
      </PageContainer>
    );
}