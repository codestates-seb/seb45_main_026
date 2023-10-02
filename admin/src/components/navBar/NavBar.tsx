import React from 'react';
import styled from 'styled-components';
import NavButton from './NavButton';
import tokens from '../../styles/tokens.json';
import { useSelector } from 'react-redux';
import { RootState } from '../../redux/Store';

const globalTokens = tokens.global;

const NavBar = () => {
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);

    return (
        <NavBarContainer isDark={isDark}>
            <NavButton text='test1' isSelected={true}/>
            <NavButton text='test2' isSelected={false}/>
        </NavBarContainer>
    );
};

const NavBarContainer = styled.nav<{isDark : boolean}>`
    width: 95%;
    box-shadow: 0 -2px 0 ${props=>props.isDark?globalTokens.Gray.value:globalTokens.LightGray.value} inset; 
    display: flex;
    flex-direction: row;
`

export default NavBar;