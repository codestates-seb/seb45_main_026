import React from 'react';
import { styled } from 'styled-components';
import { PageContainer } from '../../atoms/layouts/PageContainer';
import { useSelector } from 'react-redux';

export const LecturePageContainer = styled(PageContainer)`
`

const LectureListPage = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <LecturePageContainer isDark={isDark}>
            강의 목록 페이지입니다. 
        </LecturePageContainer>
    );
};

export default LectureListPage;