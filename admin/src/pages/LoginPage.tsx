import React from 'react';
import { MainContainer, PageContainer } from '../atoms/layouts/PageContainer';
import { useSelector } from 'react-redux';
import { RootState } from '../redux/Store';
import LoginForm from '../components/loginPage/LoginForm';
import styled from 'styled-components';

const LoginPageContainer = styled(PageContainer)`
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
`

const LoginPage = () => {
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);

    return (
        <LoginPageContainer isDark={isDark}>
            <LoginForm/>
        </LoginPageContainer>
    );
};

export default LoginPage;