import React from "react";
import { styled } from "styled-components";
import { useSelector } from "react-redux";
import tokens from "../../styles/tokens.json";
import { PageContainer, MainContainer } from "../../atoms/layouts/PageContainer";

const globalTokens = tokens.global;

const ChannelListContainer = styled(MainContainer)`
    min-width: 600px;
    min-height: 600px;
    border: none;
    background-color: ${globalTokens.White.value};
`
const ListTitle = styled.h2`
  height: 30px;
  width: 100%;
  font-size: ${globalTokens.Heading5.value}px;
  font-weight: ${globalTokens.Bold.value};
  padding-left: ${globalTokens.Spacing28.value}px;
  margin-top: ${globalTokens.Spacing20.value}px;
`;

export default function ChannelListPage() {
    const isDark = useSelector((state) => state.uiSetting.isDark);
    return (
        <PageContainer isDark={isDark}>
            <ChannelListContainer>
                <ListTitle>채널 목록</ListTitle>
            </ChannelListContainer>
        </PageContainer>
    )
}