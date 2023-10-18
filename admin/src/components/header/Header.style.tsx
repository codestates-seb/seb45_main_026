import styled from "styled-components";
import tokens from '../../styles/tokens.json';

const globalTokens = tokens.global;

export const HeaderContainer = styled.header<{isDark:boolean}>`
    height: 60px;
    background-color: ${(props)=>props.isDark ? globalTokens.Black.value : globalTokens.Header.value};
    position: sticky;
    top: 0;
    z-index: 999;
    display: flex;
    flex-direction: row;
    align-items: center;
    box-shadow: 
        ${(props)=>props.isDark ? globalTokens.RegularWhiteShadow.value.x : globalTokens.RegularShadow.value.x}px 
        ${(props)=>props.isDark ? globalTokens.RegularWhiteShadow.value.y : globalTokens.RegularShadow.value.y}px 
        ${(props)=>props.isDark ? globalTokens.RegularWhiteShadow.value.blur : globalTokens.RegularShadow.value.blur}px 
        ${(props)=>props.isDark ? globalTokens.RegularWhiteShadow.value.spread : globalTokens.RegularShadow.value.spread}px 
        ${(props)=>props.isDark ? globalTokens.RegularWhiteShadow.value.color : globalTokens.RegularShadow.value.color };
    transition: 300ms;
`