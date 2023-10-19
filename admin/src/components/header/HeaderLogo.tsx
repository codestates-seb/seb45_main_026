import React, { useState } from 'react';
import tokens from '../../styles/tokens.json';
import styled from 'styled-components';
import { BodyTextTypo } from '../../atoms/typographys/Typographys';
import { useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../../redux/Store';
import logo from '../../assets/images/logos/logo.png';
import lightLogo from '../../assets/images/logos/lightLogo.png';
import { Toggle } from '../../atoms/toggle/Toggle';
import { setIsDark } from '../../redux/createSlice/uiSettingSlice';

const globalTokens = tokens.global;

export const HeaderWrapper = styled.div`
    display: flex;
    flex-direction: row;
    align-items: center;
    width: fit-content;
`
export const HeaderLogoContainer = styled.section`
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    padding: ${globalTokens.Spacing4.value}px ${globalTokens.Spacing32.value}px;
    transition: 1000ms;
    &:active {
        opacity: 0.3;
    }
`
export const HeaderLogoTitle = styled(BodyTextTypo)`
    height: 50px;
    text-align: end;
    display: flex;
    flex-direction: column;
    justify-content: end;
    font-family: 'Saira Semi Condensed', sans-serif;
`
export const HeaderLogoImg = styled.img`
    width: 50px;
    transition: 300ms;
`

const HeaderLogo = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);

    const handleOnClick = () => {
        navigate('/');
    }

    return (
        <HeaderWrapper>
            <HeaderLogoContainer onClick={handleOnClick}>
                <HeaderLogoTitle isDark={isDark}>IT Prometheus</HeaderLogoTitle>
                <HeaderLogoImg src={isDark?lightLogo:logo}/>
            </HeaderLogoContainer>
            <Toggle isOn={isDark} setIsOn={()=>{ dispatch(setIsDark(!isDark)) }}/>
        </HeaderWrapper>
    );
};

export default HeaderLogo;