import React from 'react';
import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json'

const globalTokens = tokens.global;

export const CheckboxContainer = styled.div`
    width: 25px;
    height: 25px;
    border-radius: ${globalTokens.RegularRadius.value}px;
    border: ${globalTokens.ThinHeight.value}px solid ${globalTokens.Gray.value};
    background-color: ${(props)=>
        props.isChecked&&props.isDark? globalTokens.LightNavy.value
        :props.isChecked&&!props.isDark? globalTokens.MainRed.value
        : `rgba(255,255,255,0.15)` };
    cursor: pointer;
`