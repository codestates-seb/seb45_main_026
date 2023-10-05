import styled from "styled-components";
import tokens from '../../styles/tokens.json';

const globalTokens = tokens.global;

export const RegularInput = styled.input<{ isDark:boolean, width:string }>`
    width: ${ (props) => props.width };
    background-color: rgba(255,255,255,0.25);
    padding: ${globalTokens.Spacing8.value}px;
    border-radius: ${globalTokens.RegularRadius.value}px;
    border: ${globalTokens.ThinHeight.value}px solid ${props=>props.isDark? globalTokens.Gray.value : globalTokens.LightGray.value};
    font-size: ${globalTokens.BodyText.value}px;
    color: ${(props)=>props.isDark ? globalTokens.White.value : globalTokens.Black.value};
    &::placeholder {
        color: ${(props)=>props.isDark? globalTokens.LightGray.value : globalTokens.Gray.value};
    }
    &:focus {
        outline: ${globalTokens.RegularHeight.value}px solid ${(props)=>props.isDark ? globalTokens.White.value : globalTokens.Positive.value};
    }
    :-webkit-autofill,
    :-webkit-autofill:hover,
    :-webkit-autofill:focus,
    :-webkit-autofill:active {
        transition: background-color 5000s ease-in-out 0s;
        -webkit-transition: background-color 9999s ease-out;
        -webkit-text-fill-color: rgba(255,255,255,0.25) !important;
    }
`
export const RegularTextArea = styled.textarea<{ isDark:boolean, width:string }>`
    width: ${ (props) => props.width };
    max-width: 900px;
    background-color: rgba(255,255,255,0.25);
    padding: ${globalTokens.Spacing8.value}px;
    border-radius: ${globalTokens.RegularRadius.value}px;
    border: ${globalTokens.ThinHeight.value}px solid ${props=>props.isDark? globalTokens.Gray.value : globalTokens.LightGray.value};
    font-size: ${globalTokens.BodyText.value}px;
    color: ${(props)=>props.isDark ? globalTokens.White.value : globalTokens.Black.value};
    &::placeholder {
        color: ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
    }
`
export const RegularLabel = styled.label<{isDark:boolean, width:string}>`
    font-size: ${globalTokens.BodyText.value}px;
    color: ${props=>props.isDark?globalTokens.White.value:globalTokens.Black.value};
    transition: 200ms;
    width: ${props=>props.width};
    color: ${props=>props.isDark?globalTokens.LightGray.value : globalTokens.Gray.value};
`