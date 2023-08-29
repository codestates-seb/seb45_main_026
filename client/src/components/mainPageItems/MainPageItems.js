import React from 'react';
import { styled } from 'styled-components';
import { BodyTextTypo, Heading1Typo } from '../../atoms/typographys/Typographys';
import { useSelector } from 'react-redux';
import tokens from '../../styles/tokens.json'

const globalTokens = tokens.global;

export const MainPageFirstItemContainer = styled.section`
    padding: ${globalTokens.Spacing32.value}px;
    border-radius: ${globalTokens.BigRadius.value}px;
`
export const MainPageTitleTypo = styled(Heading1Typo)`
    margin-bottom: ${globalTokens.Spacing8.value}px;
`
export const MainPageSubTitleTypo = styled(BodyTextTypo)`
    font-size: ${globalTokens.Heading5.value}px;
`

export const MainPageFirstItem = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <MainPageFirstItemContainer>
            <MainPageTitleTypo isDark={isDark}>Teaching Someone,<br/>Grow Your Skills</MainPageTitleTypo>
            <MainPageSubTitleTypo isDark={isDark}>
                직접 코딩 강의 영상을 만들어 올려보세요!<br/>
                누군가를 가르칠 때 많이 성장합니다.
            </MainPageSubTitleTypo>
        </MainPageFirstItemContainer>
    );
};

export default MainPageFirstItem;