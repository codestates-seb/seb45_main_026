import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import tokens from '../../styles/tokens.json';
import { IconButtonContainer, IconButtonImg } from '../../atoms/buttons/IconButtons';
import closeWihte from '../../assets/images/icons/close/closeWhite.svg'
import closeBlack from '../../assets/images/icons/close/closeBlack.svg';
import { useSelector } from 'react-redux';
import { frameInBottomToTopAnimation } from '../mainPageItems/frameAnimation';
import { Heading5Typo } from '../../atoms/typographys/Typographys';
import HelpCenterChat from './HelpCenterChat';

const globalTokens = tokens.global;

const HelpCenter = ({
    isHelpClick, 
    setIsHelpClick,
}) => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    useEffect(()=>{

    },[]);

    return (
        <HelpCenterContainer 
            className={isHelpClick?'frame-in':'frame-out'}
            isDark={isDark} 
            isHelpClick={isHelpClick}>
            <HelpCenterCloseContainer isHelpClick={isHelpClick}>
            <HelpCenterTitle isDark={isDark}>고객센터</HelpCenterTitle>
                <IconButtonContainer isHelpClick={isHelpClick} onClick={()=>{ setIsHelpClick(false) }}>
                    <IconButtonImg isHelpClick={isHelpClick} src={isDark?closeWihte:closeBlack}/>
                </IconButtonContainer>
            </HelpCenterCloseContainer>
            <HelpCenterChat/>
        </HelpCenterContainer>
    );
};

export const HelpCenterContainer = styled.section`
    position: absolute;
    bottom : 0;
    right: 0;
    opacity: ${props=>props.isHelpClick?1:0};
    visibility: ${props=>props.isHelpClick?'visible':'hidden'};
    z-index: 100;
    transition: 300ms;
    width: 300px;
    min-height: 100px;
    background-color: ${props=>props.isDark ? globalTokens.Black.value : globalTokens.White.value };
    border-radius: ${globalTokens.BigRadius.value}px;
    border: 1px solid ${props=>props.isDark? globalTokens.Gray.value : globalTokens.LightGray.value};
    display: flex;
    flex-direction: column;
    justify-content: start;
    align-items: center;
    padding: ${globalTokens.Spacing8.value}px;
    &.frame-in {
        animation: ${frameInBottomToTopAnimation} 0.5s;
    }
    &.frame-out {

    }
`
export const HelpCenterCloseContainer = styled.div`
    width: 100%;
    display: flex;
    flex-direction: row;
    justify-content: end;
    align-items: center;
    opacity: ${props=>props.isHelpClick? '1' : '0' };
    visibility: ${props=>props.isHelpClick? 'visible' : 'hidden'};
`
export const HelpCenterTitle = styled(Heading5Typo)`
    flex-grow: 1;
    margin-left: ${globalTokens.Spacing16.value}px;
`

export default HelpCenter;