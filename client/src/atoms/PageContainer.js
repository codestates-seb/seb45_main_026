import React from 'react';
import { styled } from 'styled-components';
import tokens from '../styles/tokens.json'

const globalTokens = tokens.global;

export const PageContainer = styled.div`
    width: 100vw;
    background-color: ${(props)=>props.isDark ? globalTokens.Black.value : globalTokens.White.value};
    display: flex;
    flex-direction: row;
`
export const MainContainer = styled.main`
    width: 100%;
    max-width: 1100px;
    display: flex;
    flex-direction: column;
    justify-content: start;
    align-items: center;
    flex-wrap: wrap;
`