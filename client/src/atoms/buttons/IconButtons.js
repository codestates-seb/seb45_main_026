import React from 'react';
import { styled } from 'styled-components';
import closeBlack from '../../assets/images/icons/close/closeBlack.svg';
import closeWihte from '../../assets/images/icons/close/closeWhite.svg'
import tokens from '../../styles/tokens.json'

const globalTokens = tokens.global;

export const IconButtonContainer = styled.button`
    padding: ${globalTokens.Spacing8.value}px ${globalTokens.Spacing12.value}px;
    background-color: rgba(0,0,0,0);
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    transition: 300ms;
    
    &:hover {
        opacity: 0.5;
    }
`
export const IconButtonImg = styled.img`
    width: 25px;
    height: 25px;
`

export const CloseIconButton = ({isDark}) => {
    return (
        <IconButtonContainer>
            <IconButtonImg src={isDark ? closeWihte : closeBlack }/>
        </IconButtonContainer>
    );
};