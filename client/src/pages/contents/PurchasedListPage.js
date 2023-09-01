import React,{useState} from "react";
import { styled } from "styled-components";
import { useSelector } from "react-redux";
import { PageContainer,MainContainer } from "../../atoms/layouts/PageContainer";
import tokens from "../../styles/tokens.json";

const globalTokens = tokens.global;

const PurchasedListContainer = styled(MainContainer)`
    min-width: 600px;
`

export default function PurchasedListPage() {

    const isDark = useSelector((state) => state.uiSetting.isDark);

    return (
        <PageContainer isDark={isDark}>
            <PurchasedListContainer></PurchasedListContainer>
        </PageContainer>
    )
}