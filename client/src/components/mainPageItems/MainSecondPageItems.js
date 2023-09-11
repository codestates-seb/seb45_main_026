import React, { useEffect, useRef, useState } from 'react';
import { keyframes, styled } from 'styled-components';
import tokens from '../../styles/tokens.json';
import { MainPageSubTitleTypo, MainPageTitleTypo } from './MainPageItems.style';
import { useSelector } from 'react-redux';
import Carousel from './Carousel';

const globalTokens = tokens.global;

const frameInLeftToRightAnimation = keyframes`
  0% {
    opacity: 0;
    transform: translateX(-20%);
  }
  100%{
    opacity: 1;
    transform: translateX(0%);
  }
`;

const MainPageSecontItemContainer = styled.section`
    padding: ${globalTokens.Spacing32.value}px;
    border-radius: ${globalTokens.BigRadius.value}px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
`


export const MainSecondPageItems = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <MainPageSecontItemContainer>
            <MainPageTitleTypo isDark={isDark}>
                Growth & Rewards
            </MainPageTitleTypo>
            <MainPageSubTitleTypo isDark={isDark}>
                성장은 기본, 영상으로 수익 창출까지!
            </MainPageSubTitleTypo>
            <Carousel/>
        </MainPageSecontItemContainer>
    );
};

export default MainSecondPageItems;