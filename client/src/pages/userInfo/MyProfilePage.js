import React from 'react';
import { PageContainer } from '../../atoms/layouts/PageContainer';
import { useSelector } from 'react-redux';

const MyProfilePage = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    
    return (
        <PageContainer isDark={isDark}>
            This is My Profile Page.
        </PageContainer>
    );
};

export default MyProfilePage;