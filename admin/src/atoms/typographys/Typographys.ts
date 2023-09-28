import styled from "styled-components";
import tokens from '../../styles/tokens.json';

const globalTokens = tokens.global;

export const Heading1Typo = styled.div<{isDark:boolean}>`
    font-size: ${globalTokens.Heading1.value}px;
    color: ${props=>props.isDark?globalTokens.White.value:globalTokens.Black.value};
    transition: 200ms;
`
export const Heading2Typo = styled.div<{isDark:boolean}>`
    font-size: ${globalTokens.Heading2.value}px;
    color: ${props=>props.isDark?globalTokens.White.value:globalTokens.Black.value};
    transition: 200ms;
`
export const Heading3Typo = styled.div<{isDark:boolean}>`
    font-size: ${globalTokens.Heading3.value}px;
    color: ${props=>props.isDark?globalTokens.White.value:globalTokens.Black.value};
    transition: 200ms;
`
export const Heading4Typo = styled.div<{isDark:boolean}>`
    font-size: ${globalTokens.Heading4.value}px;
    color: ${props=>props.isDark?globalTokens.White.value:globalTokens.Black.value};
    transition: 200ms;
`
export const Heading5Typo = styled.div<{isDark:boolean}>`
    font-size: ${globalTokens.Heading5.value}px;
    color: ${props=>props.isDark?globalTokens.White.value:globalTokens.Black.value};
    transition: 200ms;
`
export const BodyTextTypo = styled.div<{isDark:boolean}>`
    font-size: ${globalTokens.BodyText.value}px;
    color: ${props=>props.isDark?globalTokens.White.value:globalTokens.Black.value};
    transition: 200ms;
`
export const SmallTextTypo = styled.div<{isDark:boolean}>`
    font-size: ${globalTokens.SmallText.value}px;
    color: ${props=>props.isDark?globalTokens.White.value:globalTokens.Black.value};
    transition: 200ms;
`