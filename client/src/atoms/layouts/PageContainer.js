import React from 'react';
import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json'

const globalTokens = tokens.global;

export const PageContainer = styled.div`
    background-color: ${(props)=>props.isDark ? globalTokens.Black.value : globalTokens.Background.value};
    width: 100vw;
    display: flex;
    flex-direction: row;
    justify-content: center;
    transition: 300ms;
`
export const MainContainer = styled.main`
    width: 100%;
    max-width: 1170px;
    display: flex;
    flex-direction: column;
    justify-content: start;
    align-items: center;
    flex-wrap: wrap;
`