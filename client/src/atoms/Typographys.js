import React from 'react';
import { styled } from 'styled-components';
import tokens from '../styles/tokens.json'

const globalTokens = tokens.global;

export const Heading1Typo = styled.h1`
    color: ${(props)=>props.isDark? globalTokens.White.value : globalTokens.Black.value};
    font-size: ${globalTokens.Heading1.value}px;
    transition: 300ms;
`
export const Heading2Typo = styled.h2`
    color: ${(props)=>props.isDark? globalTokens.White.value : globalTokens.Black.value};
    font-size: ${globalTokens.Heading2.value}px;
    transition: 300ms;
`
export const Heading3Typo = styled.h3`
    color: ${(props)=>props.isDark? globalTokens.White.value : globalTokens.Black.value};
    font-size: ${globalTokens.Heading3.value}px;
    transition: 300ms;
`
export const Heading4Typo = styled.h4`
    color: ${(props)=>props.isDark? globalTokens.White.value : globalTokens.Black.value};
    font-size: ${globalTokens.Heading4.value}px;
    transition: 300ms;
`
export const Heading5Typo = styled.h5`
    color: ${(props)=>props.isDark? globalTokens.White.value : globalTokens.Black.value};
    font-size: ${globalTokens.Heading5.value}px;
    transition: 300ms;
`
export const BodyTextTypo = styled.div`
    color: ${(props)=>props.isDark? globalTokens.White.value : globalTokens.Black.value};
    font-size: ${globalTokens.BodyText.value}px;
    transition: 300ms;
`
export const SmallTextTypo = styled.div`
    color: ${(props)=>props.isDark? globalTokens.White.value : globalTokens.Black.value};
    font-size: ${globalTokens.SmallText.value}px;
    transition: 300ms;
`