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