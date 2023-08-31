import React from 'react';
import { PageContainer } from '../../atoms/layouts/PageContainer';
import { useSelector } from 'react-redux';

export const SignupPage = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    
    return (
        <PageContainer isDark={isDark}>
            This is Sign up Page.
        </PageContainer>
    );
};

export default SignupPage;