import styled from "styled-components";
import tokens from "../../styles/tokens.json";

const globalTokens = tokens.global;

export const PageContainer = styled.div<{isDark:boolean}>`
    background-color: ${(props)=>props.isDark ? globalTokens.Black.value : globalTokens.Background.value};
    width: 100vw;
    min-height: 80vh;
    display: flex;
    flex-direction: row;
    justify-content: center;
    transition: 300ms;
`
export const MainContainer = styled.div<{isDark:boolean}>`
    width: 100%;
    max-width: 1170px;
    margin: ${globalTokens.Spacing40.value}px 0;
    padding: ${globalTokens.Spacing28.value}px;
    background-color: ${
        props=>props.isDark?'rgba(255,255,255,0.15)'
        :globalTokens.White.value
    };
    border-radius: ${globalTokens.RegularRadius.value}px;
    display: flex;
    flex-direction: column;
    align-items: center;
`
export const TableContainer = styled.table`
    width: 100%;
`