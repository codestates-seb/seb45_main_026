import React from 'react';
import styled from 'styled-components';
import tokens from '../../styles/tokens.json';
import { IconButtonContainer, IconButtonImg } from '../../atoms/buttons/IconButtons';
import closeWihte from '../../assets/images/icons/close/closeWhite.svg'
import closeBlack from '../../assets/images/icons/close/closeBlack.svg';
import { useSelector } from 'react-redux';
import { frameInBottomToTopAnimation, frameInTopToBottomAnimation } from '../mainPageItems/frameAnimation';

const globalTokens = tokens.global;

const HelpCenter = ({
    isHelpClick, 
    setIsHelpClick,
}) => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <HelpCenterContainer 
            className={isHelpClick?'frame-in':'frame-out'}
            isDark={isDark} 
            isHelpClick={isHelpClick}>
            <HelpCenterCloseContainer isHelpClick={isHelpClick}>
                <IconButtonContainer isHelpClick={isHelpClick} onClick={()=>{ setIsHelpClick(false) }}>
                    <IconButtonImg isHelpClick={isHelpClick} src={isDark?closeWihte:closeBlack}/>
                </IconButtonContainer>
            </HelpCenterCloseContainer>
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

export default HelpCenter;