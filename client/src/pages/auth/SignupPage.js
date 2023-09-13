import React from 'react';
import { PageContainer } from '../../atoms/layouts/PageContainer';
import { useSelector } from 'react-redux';
import { styled } from 'styled-components';
import { LoginContainer, LoginTitle } from '../../components/loginPageItems/Login';
import { LoginFormLogoContainer, LoginLogoImg, LoginLogoTitle } from '../../components/loginPageItems/LoginLogo';
import lightLogo from '../../assets/images/logos/lightLogo.png';
import logo from '../../assets/images/logos/logo.png';
import tokens from '../../styles/tokens.json';
import SignupForm from '../../components/SignupPageItems/SignupForm';

const globalTokens = tokens.global;

export const SignupPageContainer = styled(PageContainer)`
    min-height: 900px;
    padding: ${globalTokens.Spacing40.value}px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
`
export const SignupContainer = styled(LoginContainer)`
    height: 750px;
`
export const SignupFormLogoContainer = styled(LoginFormLogoContainer)`
`
export const SignupLogoTitle = styled(LoginLogoTitle)`
    left: 85px;
`
export const SignupLogoImg = styled(LoginLogoImg)`
    right: 80px;
`
export const SignupTitle = styled(LoginTitle)`
`

export const SignupFormLogo = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <SignupFormLogoContainer isDark={isDark}>
            <SignupLogoTitle isDark={isDark}>IT Prometheus</SignupLogoTitle>
            <SignupLogoImg src={isDark?lightLogo:logo}/>
        </SignupFormLogoContainer>
    )
}

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