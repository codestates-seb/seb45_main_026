import React from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import NoticeItem from "./NoticeItem";

const globalTokens = tokens.global;

const NoticeBody = styled.div`
    width: 100%;
    max-width: 1170px;
    min-height: 600px;
    padding-top: ${globalTokens.Spacing24.value}px;
    display: flex;
    flex-direction: column;
    background-color: ${globalTokens.White.value};
`
const NoticeTitle = styled.h2`
    height: 20px;
    font-size: ${globalTokens.Heading5.value}px;
    font-weight: ${globalTokens.Bold.value};
    margin-left: ${globalTokens.Spacing28.value}px;
    margin-bottom: ${globalTokens.Spacing20.value}px;
`
const ItemContainer = styled.div`
    width: 100%;
    padding: ${globalTokens.Spacing24.value}px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: ${globalTokens.Spacing28.value}px;
`

export default function ChannelNotice() {
    return (
        <NoticeBody>
            <NoticeTitle>커뮤니티</NoticeTitle>
            <ItemContainer>
                <NoticeItem/>
                <NoticeItem/>
                <NoticeItem/>
            </ItemContainer>
        </NoticeBody>
    )
}