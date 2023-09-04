import React,{useState} from "react";
import { styled } from "styled-components";
import { useSelector } from "react-redux";
import { PageContainer,MainContainer } from "../../atoms/layouts/PageContainer";
import tokens from "../../styles/tokens.json";
import CategoryFilter from "../../components/filters/CategoryFilter";

const globalTokens = tokens.global;

const PurchasedListContainer = styled(MainContainer)`
    min-width: 600px;
    min-height: 600px;
    background-color: ${globalTokens.White.value};
    border: none;
    gap: ${globalTokens.Spacing28.value}px;
    align-items: start;
`
const ListTitle = styled.h2`
    height: 30px;
    width: 100%;
    font-size: ${globalTokens.Heading5.value}px;
    font-weight: ${globalTokens.Bold.value};
    padding-left: ${globalTokens.Spacing28.value}px;
    margin-top: ${globalTokens.Spacing20.value}px;
`

export default function PurchasedListPage() {

    const isDark = useSelector((state) => state.uiSetting.isDark);

    return (
        <PageContainer isDark={isDark}>
            <PurchasedListContainer>
                <ListTitle>구매한 강의 목록</ListTitle>
                <CategoryFilter/>
            </PurchasedListContainer>
        </PageContainer>
    )
}