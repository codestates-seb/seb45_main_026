import styled from "styled-components";
import tokens from '../styles/tokens.json';

const globalTokens = tokens.global;

export const Heading1Typo = styled.div`
    font-size: ${globalTokens.Heading1.value}px;
`
export const Heading2Typo = styled.div`
    font-size: ${globalTokens.Heading2.value}px;
`
export const Heading3Typo = styled.div`
    font-size: ${globalTokens.Heading3.value}px;
`
export const Heading4Typo = styled.div`
    font-size: ${globalTokens.Heading4.value}px;
`
export const Heading5Typo = styled.div`
    font-size: ${globalTokens.Heading5.value}px;
`
export const BodyTextTypo = styled.div`
    font-size: ${globalTokens.BodyText.value}px;
`
export const SmallTextTypo = styled.div`
    font-size: ${globalTokens.SmallText.value}px;
`