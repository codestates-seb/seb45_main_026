import React from 'react';
import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json';
import { useSelector } from 'react-redux';
import { MainPageSubTitleTypo, MainPageTitleTypo } from './MainPageItems.style';

const globalTokens = tokens.global;

const MainPageFourthItemContainer = styled.section`
    padding: ${globalTokens.Spacing32.value}px;
    border-radius: ${globalTokens.BigRadius.value}px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
`

export const MainFourthPageItems = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <MainPageFourthItemContainer>
            <MainPageTitleTypo isDark={isDark}>
                Solve a Problem
            </MainPageTitleTypo>
            <MainPageSubTitleTypo isDark={isDark}>
                문제를 풀면서, 포인트도 얻어봐요!
            </MainPageSubTitleTypo>
        </MainPageFourthItemContainer>
    );
};

export default MainFourthPageItems;