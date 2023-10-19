import styled from "styled-components";
import tokens from '../../styles/tokens.json';

const globalTokens = tokens.global;

export const BigButton = styled.button<{isDark:boolean}>`
    padding: ${globalTokens.Spacing8.value}px ${globalTokens.Spacing20.value}px;
    border-radius: ${globalTokens.RegularRadius.value}px;
    border: ${globalTokens.ThinHeight.value}px solid ${(props)=>props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
    background-color: ${(props)=>props.isDark? globalTokens.MainNavy.value : globalTokens.LightRed.value};
    color: ${props=>props.isDark?globalTokens.White.value:globalTokens.Black.value};
    font-size: ${globalTokens.BodyText.value}px;
    font-weight: ${globalTokens.Bold.value};
    transition: 300ms;

    &:hover {
        background-color: ${(props)=>props.isDark? globalTokens.LightNavy.value : globalTokens.MainRed.value};
        color: ${globalTokens.White.value};
    }
`
export const RegularButton = styled.button<{isDark:boolean}>`
    padding: ${globalTokens.Spacing4.value}px ${globalTokens.Spacing12.value}px;
    border-radius: ${globalTokens.RegularRadius.value}px;
    border: ${globalTokens.ThinHeight.value}px solid ${(props)=>props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
    background-color: ${(props)=>props.isDark? globalTokens.MainNavy.value : globalTokens.LightRed.value};
    color: ${props=>props.isDark?globalTokens.White.value:globalTokens.Black.value};
    font-size: ${globalTokens.BodyText.value}px;
    transition: 300ms;

    &:hover {
        background-color: ${(props)=>props.isDark? globalTokens.LightNavy.value : globalTokens.MainRed.value};
        color: ${globalTokens.White.value};
    } 
`
export const RoundButton = styled.button<{isDark:boolean}>`
    padding: ${globalTokens.Spacing4.value}px ${globalTokens.Spacing12.value}px;
    border-radius: ${globalTokens.BigRadius.value}px;
    border: ${globalTokens.ThinHeight.value}px solid ${(props)=>props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
    background-color: ${(props)=>props.isDark? globalTokens.MainNavy.value : globalTokens.LightRed.value};
    color: ${props=>props.isDark?globalTokens.White.value:globalTokens.Black.value};
    font-size: ${globalTokens.BodyText.value}px;
    transition: 300ms;

    &:hover {
        background-color: ${(props)=>props.isDark? globalTokens.LightNavy.value : globalTokens.MainRed.value};
        color: ${globalTokens.White.value};
    }
`
export const TextButton = styled.button<{isDark:boolean}>`
    padding: ${globalTokens.Spacing8.value}px ${globalTokens.Spacing12.value}px;
    background-color: rgba(0,0,0,0);
    color: ${(props)=>props.isDark ? globalTokens.White.value : globalTokens.Black.value};
    font-size: ${globalTokens.BodyText.value}px;
    transition: 300ms;
    &:hover {
        color: ${globalTokens.Gray.value};
    }
`
export const PositiveTextButton = styled.button<{isDark:boolean}>`
    padding: ${globalTokens.Spacing8.value}px ${globalTokens.Spacing12.value}px;
    background-color: rgba(0,0,0,0);
    color: ${(props)=>props.isDark ? globalTokens.LightNavy.value : globalTokens.Positive.value};
    font-size: ${globalTokens.BodyText.value}px;
    transition: 300ms;
    &:hover {
        color: ${globalTokens.Gray.value};
    }
`
export const NegativeTextButton = styled.button<{isDark:boolean}>`
    padding: ${globalTokens.Spacing8.value}px ${globalTokens.Spacing12.value}px;
    background-color: rgba(0,0,0,0);

    color: ${(props)=>props.isDark ? globalTokens.LightRed.value : globalTokens.Negative.value};
    font-size: ${globalTokens.BodyText.value}px;
    transition: 300ms;
    &:hover {
        color: ${globalTokens.Gray.value};
    }
`