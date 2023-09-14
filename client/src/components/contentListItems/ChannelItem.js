import React, { useEffect, useState } from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import { ProfileImg, ImgContainer } from "../../pages/contents/ChannelPage";
import { Heading5Typo,BodyTextTypo } from "../../atoms/typographys/Typographys";
import profileGray from "../../assets/images/icons/profile/profileGray.svg";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const globalTokens = tokens.global;

const ItemBody = styled.li`
    width: 210px;
    min-height: 320px;
    border-radius: ${globalTokens.RegularRadius.value}px;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: ${globalTokens.Spacing12.value}px;
    gap: ${globalTokens.Spacing8.value}px;
`
const ChannelImg = styled(ProfileImg)`
    height: 180px;
    width: 180px;
`
const ChannelImgContainer = styled(ImgContainer)`
    height: 180px;
    width: 180px;
    min-width: 180px;
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
    border: 1px black solid;
    border-radius: ${globalTokens.RegularRadius.value}px;
`

export default function ChannelItem({ getChannels, channel, accessToken, refreshToken }) {
    const navigate = useNavigate();
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
    <ItemBody>
      <ChannelImgContainer onClick={()=>navigate(`/channels/${channel.memberId}`)}>
        <ChannelImg src={channel.imageUrl} />
      </ChannelImgContainer>
      <ChannelName onClick={()=>navigate(`/channels/${channel.memberId}`)}>{channel.channelName}</ChannelName>
      <BodyTextTypo>구독자 {channel.subscribes}명</BodyTextTypo>
      <CancelButton onClick={() => deleteHandler(channel.memberId)}>구독취소</CancelButton>
    </ItemBody>
  );
}