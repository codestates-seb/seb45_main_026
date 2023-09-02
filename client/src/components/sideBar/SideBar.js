import React from 'react';
import { styled } from 'styled-components';
import { useSelector } from 'react-redux';
import tokens from '../../styles/tokens.json'

const globalTokens = tokens.global;

export const SideBarContainer = styled.aside`
    visibility: ${(props)=>props.isSideBar?'visible':'hidden'};
    position: fixed;
    top: 60px;
    bottom: 0;
    right: 0;
    z-index: 999;
    min-width: 200px;
    width: 15vw;
    background-color: ${(props)=>props.isDark?globalTokens.Black.value:globalTokens.Header.value};
    box-shadow: 
        ${(props)=>props.isDark?-1:-4}px 
        ${(props)=>props.isDark?1:10}px 
        ${(props)=>props.isDark ? globalTokens.RegularWhiteShadow.value.blur : globalTokens.RegularShadow.value.blur}px
        ${(props)=>props.isDark ? globalTokens.RegularWhiteShadow.value.spread : globalTokens.RegularShadow.value.spread}px
        ${(props)=>props.isDark ? globalTokens.RegularWhiteShadow.value.color : globalTokens.RegularShadow.value.color };
    display: flex;
    flex-direction: column;
    justify-content: start;
    align-items: center;
`

const SideBar = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const isSideBar = useSelector(state=>state.uiSetting.isSideBar);

    return (
        <SideBarContainer isDark={isDark} isSideBar={isSideBar}>
            This is SideBar.
        </SideBarContainer>
    );
};

export default SideBar;