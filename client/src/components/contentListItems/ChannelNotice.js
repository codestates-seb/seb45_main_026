import React,{useEffect,useState} from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import NoticeItem from "./NoticeItem";
import NoticeSubmit from "./NoticeSubmit";
import axios from "axios";
import { useSelector } from "react-redux";
import { HomeTitle } from './ChannelHome';

const globalTokens = tokens.global;

const NoticeBody = styled.div`
    width: 100%;
    padding: ${globalTokens.Spacing20.value}px;
    max-width: 1170px;
    min-height: 600px;
    padding-top: ${globalTokens.Spacing24.value}px;
    display: flex;
    flex-direction: column;
    align-items: center;
    background-color: ${props=>props.isDark?'rgba(255,255,255,0.15)':globalTokens.White.value};
    border-radius: 0 0 ${globalTokens.RegularRadius.value}px ${globalTokens.RegularRadius.value}px;

`
const NoticeTitle = styled(HomeTitle)`
    margin-left: ${globalTokens.Spacing40.value}px;
    margin-bottom: 0;
`
const ItemContainer = styled.div`
    width: 100%;
    padding: ${globalTokens.Spacing20.value}px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: ${globalTokens.Spacing28.value}px;
`
const Nothing = styled.div`
    width: 100%;
    height: 500px;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: ${globalTokens.Heading4.value}px;
`


export default function ChannelNotice({channelInfor,userId}) {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const accessToken = useSelector(state=>state.loginInfo.accessToken);
    const myId = useSelector((state) => state.loginInfo.myid);
    const [notices, setNotices] = useState([])
    
    useEffect(() => {
        axios.get(`https://api.itprometheus.net/channels/${userId}/announcements?page=1&size=10`)
            .then(res => setNotices(res.data.data))
            .catch(err=>console.log(err))
    },[])
    return (
      <NoticeBody isDark={isDark}>
        <NoticeTitle isDark={isDark}>커뮤니티</NoticeTitle>
        {myId === Number(userId) ? (
          <NoticeSubmit
            userId={userId}
            accessToken={accessToken}
            setNotices={setNotices}
            todo="post"
          />
        ) : (
          <></>
        )}
        <ItemContainer>
          {notices.map((el) => (
            <NoticeItem
              key={el.announcementId}
              channelInfor={channelInfor}
              accessToken={accessToken}
              notice={el}
              setNotices={setNotices}
              userId={userId}
            />
          ))}
          {notices.length === 0 ? (
            <Nothing>등록된 공지사항이 없습니다.</Nothing>
          ) : (
            <></>
          )}
        </ItemContainer>
      </NoticeBody>
    );
}