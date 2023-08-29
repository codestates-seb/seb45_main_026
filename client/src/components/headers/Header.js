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
    box-shadow: 0px 2px 10px 0px rgba(0, 0, 0, 0.15);
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