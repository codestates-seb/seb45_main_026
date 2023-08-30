import React from 'react';
import { useSelector } from 'react-redux';
import { 
    MainPageFirstItemContainer, 
    MainPageTitleTypo, 
    MainPageSubTitleTypo,
    MainPageStartButton 
} from './MainPageItems.style';

export const MainPageFirstItem = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <MainPageFirstItemContainer>
            <MainPageTitleTypo isDark={isDark}>Teaching Someone,<br/>Grow Your Skills</MainPageTitleTypo>
            <MainPageSubTitleTypo isDark={isDark}>
                직접 코딩 강의 영상을 만들어 올려보세요!<br/>
                누군가를 가르칠 때 많이 성장합니다.
            </MainPageSubTitleTypo>
            <MainPageStartButton isDark={isDark}>시작하기</MainPageStartButton>
        </MainPageFirstItemContainer>
    );
};

export default MainPageFirstItem;