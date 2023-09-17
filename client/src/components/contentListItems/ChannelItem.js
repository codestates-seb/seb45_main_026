import React, { useEffect, useState } from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import { ProfileImg, ImgContainer } from "../../pages/contents/ChannelPage";
import { Heading5Typo,BodyTextTypo } from "../../atoms/typographys/Typographys";
import profileGray from "../../assets/images/icons/profile/profileGray.svg";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import SubscribeBtn from "../DetailPage/SubscribeBtn";
import { useSelector } from "react-redux";

const globalTokens = tokens.global;

const ItemBody = styled.li`
    width: 170px;
    min-height: 320px;
    border-radius: ${globalTokens.RegularRadius.value}px;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: ${globalTokens.Spacing12.value}px;
    gap: ${globalTokens.Spacing8.value}px;
`
const ChannelImg = styled(ProfileImg)`
    height: 160px;
    width: 160px;
    object-fit: cover;
`
const ChannelImgContainer = styled(ImgContainer)`
    height: 160px;
    width: 160px;
    min-width: 160px;
    min-height: 160px;
    border-radius: 50%;
    &:hover{
        cursor: pointer;
    }
`
const ChannelName = styled(Heading5Typo)`
    &:hover{
        cursor: pointer;
    }
`
const CancelButton = styled.button`
    width: 60px;
    height: 30px;
    border: ${globalTokens.ThinHeight.value}px solid ${(props)=>props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
    border-radius: ${globalTokens.RegularRadius.value}px;
    color: ${(props)=>props.isDark? globalTokens.White.value : globalTokens.Black.value};
`

export default function ChannelItem({ getChannels, channel, accessToken, refreshToken, isSubscribed,channelGetHandler }) {
    const navigate = useNavigate();
    const isDark = useSelector((state) => state.uiSetting.isDark);
    const [isSub,setSub]=useState("")
    useEffect(()=>{
      channelGetHandler()
    },[isSub])
    const deleteHandler = (memberId) => {
    axios
      .patch(`https://api.itprometheus.net/channels/${memberId}/subscribe`,null, {
        headers: { Authorization: accessToken.authorization },
      })
      .then((res) => {
        getChannels();
      })
      .catch((err) => {
        if (err.response.data.message === "만료된 토큰입니다.") {
          refreshToken();
        } else {
          console.log(err.response.data.message);
        }
      });
  };
  return (
    <ItemBody isDark={isDark}>
      <ChannelImgContainer onClick={()=>navigate(`/channels/${channel.memberId}`)}>
        <ChannelImg src={channel.imageUrl?channel.imageUrl:profileGray} />
      </ChannelImgContainer>
      <ChannelName isDark={isDark} onClick={()=>navigate(`/channels/${channel.memberId}`)}>{channel.channelName}</ChannelName>
      <BodyTextTypo isDark={isDark}>구독자 {channel.subscribes}명</BodyTextTypo>
      {isSubscribed!==undefined ? <SubscribeBtn memberId={channel.memberId} channelInfo={channel} setSub={setSub} />:<CancelButton isDark={isDark} onClick={() => deleteHandler(channel.memberId)}>구독취소</CancelButton>}
    </ItemBody>
  );
}