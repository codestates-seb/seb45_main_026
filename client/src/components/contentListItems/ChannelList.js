import React from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import CategoryFilter from "../filters/CategoryFilter";
import HorizonItem from "./HorizonItem";

const globalTokens = tokens.global;

const ListBody = styled.div`
    width: 100%;
    max-width: 1170px;
    min-height: 600px;
    padding-top: ${globalTokens.Spacing24.value}px;
    display: flex;
    flex-direction: column;
    background-color: ${globalTokens.White.value};
    gap: ${globalTokens.Spacing24.value}px;
`; 
const ListContainer = styled.div`
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: ${globalTokens.Spacing16.value}px;
    margin-bottom: ${globalTokens.Spacing24.value}px;
`

export default function ChannelList() {
    return (
        <ListBody>
            <CategoryFilter />
            <ListContainer>
                <HorizonItem/>
                <HorizonItem/>
                <HorizonItem/>
                <HorizonItem/>
                <HorizonItem/>
                <HorizonItem/>
            </ListContainer>
        </ListBody>
    )
}
