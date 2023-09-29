import React from 'react';
import { MainContainer, PageContainer } from '../atoms/layouts/PageContainer';
import { useSelector } from 'react-redux';
import { RootState } from '../redux/Store';

const MemberPage = () => {
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);

    return (
        <PageContainer isDark={isDark}>
            <MainContainer isDark={isDark}>
                
            </MainContainer>
        </PageContainer>
    );
};

export default MemberPage;