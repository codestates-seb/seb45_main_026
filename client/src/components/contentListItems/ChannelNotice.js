import React,{useEffect} from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import NoticeItem from "./NoticeItem";
import axios from "axios";
import { useSelector } from "react-redux";
import { HomeTitle } from './ChannelHome';

const globalTokens = tokens.global;

const NoticeBody = styled.div`
    width: 100%;
    max-width: 1170px;
    min-height: 600px;
    padding-top: ${globalTokens.Spacing24.value}px;
    display: flex;
    flex-direction: column;
    background-color: ${props=>props.isDark?'rgba(255,255,255,0.15)':globalTokens.White.value};
`
const NoticeTitle = styled(HomeTitle)`
    /* height: 20px;
    font-size: ${globalTokens.Heading5.value}px;
    font-weight: ${globalTokens.Bold.value};
    margin-left: ${globalTokens.Spacing28.value}px;
    margin-bottom: ${globalTokens.Spacing20.value}px; */
`
const ItemContainer = styled.div`
    width: 100%;
    padding: ${globalTokens.Spacing20.value}px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: ${globalTokens.Spacing28.value}px;
`

export default function ChannelNotice() {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const accessToken = useSelector(state=>state.loginInfo.accessToken);
    useEffect(() => {
        axios.get("https://api.itprometheus.net/channels/4/announcements?page=1&size=10")
            .then(res => console.log(res.data))
            .catch(err=>console.log(err))
    })
    return (
        <NoticeBody isDark={isDark}>
            <NoticeTitle isDark={isDark}>커뮤니티</NoticeTitle>
            <ItemContainer>
                <NoticeItem/>
                <NoticeItem/>
                <NoticeItem/>
            </ItemContainer>
        </NoticeBody>
    )
}