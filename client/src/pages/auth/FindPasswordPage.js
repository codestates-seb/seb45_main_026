import React from 'react';
import { PageContainer } from '../../atoms/layouts/PageContainer';
import { styled } from 'styled-components';
import { useSelector } from 'react-redux';
import { LoginContainer, LoginTitle } from '../../components/loginPageItems/Login';
import LoginFormLogo from '../../components/loginPageItems/LoginLogo';
import tokens from '../../styles/tokens.json';
import FindPasswordPageForm from '../../components/findPasswordPageItems/FindPasswordPageForm';

const globalTokens = tokens.global;

export const FindPasswordPageContainer = styled(PageContainer)`
    height: 600px;
    padding: ${globalTokens.Spacing40.value}px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
`
export const FindPasswordContainer = styled(LoginContainer)`
    height: 400px;
    min-width: 300px;
`
export const FindPasswordLogo = styled(LoginFormLogo)`
`
export const FindPasswordTitle = styled(LoginTitle)`
`

const FindPasswordPage = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <FindPasswordPageContainer isDark={isDark}>
            <FindPasswordContainer isDark={isDark}>
                <FindPasswordLogo isDark={isDark}/>
                <FindPasswordTitle isDark={isDark}>비밀번호 찾기</FindPasswordTitle>
                <FindPasswordPageForm/>
            </FindPasswordContainer>
        </FindPasswordPageContainer>
    );
};

export default FindPasswordPage;