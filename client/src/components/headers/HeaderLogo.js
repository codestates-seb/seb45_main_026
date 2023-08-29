import React, { useState } from 'react';
import { styled } from 'styled-components';
import { useLocation, useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from 'react-redux';
import { setIsDark } from '../../redux/createSlice/UISettingSlice'
import { BodyTextTypo } from '../../atoms/typographys/Typographys';
import logo from '../../assets/images/logos/logo.png'
import tokens from '../../styles/tokens.json'
import { useLongPress } from '../../hooks/useLongPress';

const globalTokens = tokens.global;

export const HeaderLogoContainer = styled.section`
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    padding: ${globalTokens.Spacing4.value}px ${globalTokens.Spacing12.value}px
`
export const HeaderLogoTitle = styled(BodyTextTypo)`
    height: 50px;
    text-align: end;
    display: flex;
    flex-direction: column;
    justify-content: end;
`
export const HeaderLogoImg = styled.img`
    width: 50px;
`

const HeaderLogo = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const isDark = useSelector(state=>state.uiSetting.isDark);

    const onLongPress = () => {
        console.log('longpress is triggered');
        dispatch(setIsDark(!isDark));
    };

    const onClick = () => {
        console.log('click is triggered');
        navigate('/');
    }

    const defaultOptions = {
        shouldPreventDefault: true,
        delay: 1000,
    };

    const longPressEvent = useLongPress(onLongPress, onClick, defaultOptions);
    
    return (
        <HeaderLogoContainer {...longPressEvent}>
            <HeaderLogoTitle isDark={isDark}>IT Prometheus</HeaderLogoTitle>
            <HeaderLogoImg src={logo}/>
        </HeaderLogoContainer>
    );
};

export default HeaderLogo;