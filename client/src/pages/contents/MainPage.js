import React from 'react';
import { PageContainer } from '../../atoms/layouts/PageContainer';
import { useSelector } from 'react-redux';

const MainPage = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <PageContainer isDark={isDark}>
            This is Main Page.
        </PageContainer>
    );
};

export default MainPage;