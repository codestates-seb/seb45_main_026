import React from 'react';
import { PageContainer } from '../../atoms/layouts/PageContainer';
import { useSelector } from 'react-redux';

const LoginPage = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <PageContainer isDark={isDark}>
            This is Login Page.
        </PageContainer>
    );
};

export default LoginPage;