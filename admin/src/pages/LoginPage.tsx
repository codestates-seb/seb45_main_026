import React from 'react';
import { PageContainer } from './PageContainer';
import { useSelector } from 'react-redux';
import { RootState } from '../redux/Store';

const LoginPage = () => {
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);

    return (
        <PageContainer isDark={isDark}>
        </PageContainer>
    );
};

export default LoginPage;