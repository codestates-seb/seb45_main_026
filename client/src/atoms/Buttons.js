import React from 'react';
import { styled } from 'styled-components';
import tokens from '../styles/tokens.json'

const globalTokens = tokens.global;

export const RegularRedButton = styled.button`
    padding: ${globalTokens.Spacing8.value}px ${globalTokens.Spacing12.value}px;
    border-radius: ${globalTokens.RegularRadius.value}px;
    border: ${globalTokens.ThinHeight.value}px solid ${(props)=>props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
    background-color: ${globalTokens.LightRed.value};
    color: ${globalTokens.White.value};
    transition: 300ms;

    &:hover {
        background-color: ${globalTokens.MainRed.value};
    }
`
export const RegularNavyButton = styled.button`
    padding: ${globalTokens.Spacing8.value}px ${globalTokens.Spacing12.value}px;
    border-radius: ${globalTokens.RegularRadius.value}px;
    border: ${globalTokens.ThinHeight.value}px solid ${(props)=>props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
    background-color: ${globalTokens.LightBlue.value};
    color: ${globalTokens.White.value};
    transition: 300ms;

    &:hover {
        background-color: ${globalTokens.MainNavy.value};
    }
`
