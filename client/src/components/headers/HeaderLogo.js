import React, { useState } from 'react';
import { useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from 'react-redux';
import { setIsDark } from '../../redux/createSlice/UISettingSlice'
import logo from '../../assets/images/logos/logo.png'
import lightLogo from  '../../assets/images/logos/lightLogo.png'
import { useLongPress } from '../../hooks/useLongPress';
import { HeaderLogoContainer, HeaderLogoImg, HeaderLogoTitle, HeaderLogoToggleContainer } from './HeaderLogo.style';
import Toggle from '../../atoms/buttons/Toggle';

const HeaderLogo = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const isDark = useSelector(state=>state.uiSetting.isDark);

    const onLongPress = () => {
        dispatch(setIsDark(!isDark));
    };

    const onClick = () => {
        navigate('/');
    }

    const defaultOptions = {
        shouldPreventDefault: true,
        delay: 1000,
    };

    const longPressEvent = useLongPress(onLongPress, onClick, defaultOptions);
    
    return (
        <HeaderLogoToggleContainer>
            <HeaderLogoContainer {...longPressEvent}>
                <HeaderLogoTitle isDark={isDark}>IT Prometheus</HeaderLogoTitle>
                <HeaderLogoImg src={isDark?lightLogo:logo}/>
            </HeaderLogoContainer>
            <Toggle/>
        </HeaderLogoToggleContainer>
    );
};

export default HeaderLogo;