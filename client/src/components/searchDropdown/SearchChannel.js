import React, { useState } from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import profileGray from "../../assets/images/icons/profile/profileGray.svg";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { BodyTextTypo } from "../../atoms/typographys/Typographys";

const globalTokens = tokens.global;

const ChannelBody = styled.li`
    width: 100px;
    min-width: 100px;
    min-height: 150px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: ${globalTokens.Spacing4.value}px;
    &:hover{
        cursor: pointer;
    }
`
const ImgContainer = styled.div`
    width: 100px;
    height: 100px;
    min-width: 100px;
    border-radius: 50%;
    overflow: hidden;
`
const ChannelImg = styled.img`
    width: 100%;
    height: 100%;
    object-fit: cover;
`
const ChannelName = styled(BodyTextTypo)`
    text-align: center;
    font-weight: ${globalTokens.Bold.value};
`

export default function SearchChannel({channel}) {
    const isDark = useSelector((state) => state.uiSetting.isDark);
    const navigate=useNavigate()
    return (
      <ChannelBody onMouseDown={()=>navigate(`/channels/${channel.memberId}`)}>
        <ImgContainer>
          <ChannelImg src={channel.imageUrl ? channel.imageUrl : profileGray} />
        </ImgContainer>
        <ChannelName isDark={isDark}>{channel.channelName}</ChannelName>
      </ChannelBody>
    );
}