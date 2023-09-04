import React from 'react';
import { PageContainer } from '../../atoms/layouts/PageContainer';
import { useSelector } from 'react-redux';
import { styled } from 'styled-components';
import { LoginContainer, LoginTitle } from '../../components/loginPageItems/Login';
import LoginFormLogo from '../../components/loginPageItems/LoginLogo';
import tokens from '../../styles/tokens.json'
import SignupForm from '../../components/SignupPageItems/SignupForm';

const globalTokens = tokens.global;

export const SignupPageContainer = styled(PageContainer)`
    height: 950px;
    padding: ${globalTokens.Spacing40.value}px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
`
export const SignupContainer = styled(LoginContainer)`
    height: 750px;
`
export const SignupFormLogo = styled(LoginFormLogo)`
`
export const SignupTitle = styled(LoginTitle)`
`

export const SignupPage = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <SignupPageContainer isDark={isDark}>
            <SignupContainer isDark={isDark}>
                <SignupFormLogo isDark={isDark}/>
                <SignupTitle isDark={isDark}>회원가입</SignupTitle>
                <SignupForm/>
            </SignupContainer>
        </SignupPageContainer>
    );
};

export default SignupPage;