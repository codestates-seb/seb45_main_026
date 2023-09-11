import React from 'react';
import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json';
import { MainPageSubTitleTypo, MainPageTitleTypo } from './MainPageItems.style';
import { useSelector } from 'react-redux';

const globalTokens = tokens.global;

const MainPageThirdItemContainer = styled.section`
    padding: ${globalTokens.Spacing32.value}px;
    border-radius: ${globalTokens.BigRadius.value}px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
`

export const MainThirdPageItems = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <MainPageThirdItemContainer>
            <MainPageTitleTypo isDark={isDark}>
                Tech Stack
            </MainPageTitleTypo>
            <MainPageSubTitleTypo isDark={isDark}>
                기술 스텍
            </MainPageSubTitleTypo>
        </MainPageThirdItemContainer>
    );
};

export default MainThirdPageItems;