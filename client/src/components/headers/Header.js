import React from 'react';
import { styled } from 'styled-components';
import { useSelector } from 'react-redux';
import tokens from '../../styles/tokens.json'
import HeaderLogo from './HeaderLogo';

const globalTokens = tokens.global;

export const HeaderContainer = styled.header`
    height: 60px;
    background-color: ${(props)=>props.isDark ? globalTokens.BackgroundDark.value : globalTokens.Header.value};
    position: sticky;
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

const Header = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <HeaderContainer isDark={isDark}>
            <HeaderLogo/>
        </HeaderContainer>
    );
};

export default Header;