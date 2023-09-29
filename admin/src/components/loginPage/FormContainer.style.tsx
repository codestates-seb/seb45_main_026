import styled from "styled-components";
import tokens from '../../styles/tokens.json';

const globalTokens = tokens.global;

export const LoginContainer = styled.section<{isDark : boolean}>`
    height: 520px;
    padding: ${globalTokens.Spacing24.value}px;
    background-color: ${(props)=>props.isDark ? globalTokens.DarkGray.value : globalTokens.White.value};
    border-radius: ${globalTokens.BigRadius.value}px;
    position: relative;
    display: flex;
    flex-direction: column;
    justify-content: center;
    box-shadow: 
        ${(props)=>props.isDark?globalTokens.RegularWhiteShadow.value.x : globalTokens.RegularShadow.value.x}px 
        ${(props)=>props.isDark?globalTokens.RegularWhiteShadow.value.y : globalTokens.RegularShadow.value.y}px 
        ${(props)=>props.isDark?globalTokens.RegularWhiteShadow.value.blur : globalTokens.RegularShadow.value.blur}px 
        ${(props)=>props.isDark?globalTokens.RegularWhiteShadow.value.spread : globalTokens.RegularShadow.value.spread}px 
        ${(props)=>props.isDark?globalTokens.RegularWhiteShadow.value.color : globalTokens.RegularShadow.value.color};
`
export const FormContainer = styled.form`
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
`