import React from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import VerticalItem from "./VerticalItem";

const globalTokens = tokens.global;

const HomeBody = styled.div`
    width: 100%;
    max-width: 1170px;
    min-height: 600px;
    padding-top: ${globalTokens.Spacing24.value}px;
    display: flex;
    flex-direction: column;
    background-color: ${globalTokens.White.value};
`
const HomeTitle = styled.h2`
    height: 20px;
    font-size: ${globalTokens.Heading5.value}px;
    font-weight: ${globalTokens.Bold.value};
    margin-left: ${globalTokens.Spacing28.value}px;
    margin-bottom: ${globalTokens.Spacing20.value}px;
`
const ItemContainer = styled.ul`
    width: 100%;
    min-height: 400px;
    display: flex;
    flex-direction: row;
    justify-content: start;
    flex-wrap: wrap;
    gap: ${globalTokens.Spacing28.value}px;
`

export default function ChannelHome() {
    return (
        <HomeBody>
            <HomeTitle>무료강의</HomeTitle>
            <ItemContainer>
                <VerticalItem/>
                <VerticalItem/>
                <VerticalItem/>
                <VerticalItem/>
            </ItemContainer>
            <HomeTitle>채널 내 인기 강의</HomeTitle>
            <ItemContainer>
                <VerticalItem/>
                <VerticalItem/>
                <VerticalItem/>
                <VerticalItem/>
            </ItemContainer>
        </HomeBody>
    )
}