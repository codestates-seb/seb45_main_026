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
import profileGray from "../../assets/images/icons/profile/profileGray.svg"
import Setting from '../../components/contentListItems/Setting';
import { useParams } from "react-router";
import { BodyTextTypo, Heading5Typo } from "../../atoms/typographys/Typographys";
import { useToken } from "../../hooks/useToken";

const globalTokens = tokens.global;

const ChannelMainContainer = styled(MainContainer)`
  min-width: 600px;
  min-height: 700px;
  margin-bottom: ${globalTokens.Spacing40.value}px;
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
    width: 130px;
    height: 130px;
    object-fit: cover;
    
`
export const ImgContainer = styled.div`
    width: 130px;
    height: 130px;
    min-width: 130px;
    border-radius: ${globalTokens.ProfileRadius.value}px;
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;
    border: 1px solid ${globalTokens.LightGray.value};
`
const InforContainer = styled.div`
    min-height: 130px;
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    gap: ${globalTokens.Spacing8.value}px;
`
const ChannelTitle = styled(Heading5Typo)`
  /* height: 30px;
  font-size: ${globalTokens.Heading4.value}px;
  font-weight: ${globalTokens.Bold.value}; */
`;
const ChannelSubscribers = styled(BodyTextTypo)`
  /* height: 20px;
  font-size: ${globalTokens.BodyText.value}px;
  font-weight: ${globalTokens.Bold.value}; */
`
const ChannelDescription = styled.div`
  flex-grow: 1;
  padding: ${globalTokens.Spacing8.value}px;
  background-color: ${props=>props.isDark?'rgba(255,255,255,0.15)':globalTokens.White.value};
  color: ${props=>props.isDark?globalTokens.White.value:globalTokens.Black.value};
  border-radius: ${globalTokens.RegularRadius.value}px;
`

export default function ChannelPage() {
  const refreshToken = useToken();
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const accessToken = useSelector(state=>state.loginInfo.accessToken);
  const [navigate, setNavigate] = useState(0)
  const [channelInfor, setChannelInfor] = useState({})
  const { userId } = useParams();

  useEffect(() => {
    axios
      .get(`https://api.itprometheus.net/channels/${userId}`, {
        headers: { Authorization: accessToken.authorization },
      })
      .then((res) => setChannelInfor(res.data.data))
      .catch((err) => {
        if(err.response.data.message==='만료된 토큰입니다.') {
          refreshToken();
        } else {
          console.log(err.response.data.message);
        }
      });
  },[accessToken,userId]);

    return (
      <PageContainer isDark={isDark}>
        <ChannelMainContainer>
          <ProfileContainer>
            <ImgContainer>
              <ProfileImg
                src={
                  channelInfor.imageUrl
                    ? `${channelInfor.imageUrl}?${new Date().getTime()}`
                    : profileGray
                }
              />
            </ImgContainer>
            <InforContainer>
              <ChannelTitle isDark={isDark}>
                {channelInfor.channelName}
              </ChannelTitle>
              <ChannelSubscribers isDark={isDark}>
                구독자 {channelInfor.subscribers}명
              </ChannelSubscribers>
              <ChannelDescription isDark={isDark}>
                {channelInfor.description
                  ? channelInfor.description
                  : "아직 채널 소개가 없습니다"}
              </ChannelDescription>
            </InforContainer>
          </ProfileContainer>
          <ChannelNav navigate={navigate} setNavigate={setNavigate} />
          {navigate === 0 ? (
            <ChannelHome
              channelInfor={channelInfor}
              accessToken={accessToken}
              userId={userId}
            />
          ) : navigate === 1 ? (
            <ChannelList
              channelInfor={channelInfor}
              accessToken={accessToken}
              userId={userId}
            />
          ) : navigate === 2 ? (
            <ChannelNotice channelInfor={channelInfor} userId={userId} />
          ) : (
            <Setting />
          )}
        </ChannelMainContainer>
      </PageContainer>
    );
}