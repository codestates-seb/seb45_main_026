import React from 'react';
import { styled } from 'styled-components';
import { PageContainer } from '../../atoms/layouts/PageContainer';
import tokens from '../../styles/tokens.json';
import { LoginContainer, LoginTitle } from '../../components/loginPageItems/Login';
import LoginFormLogo from '../../components/loginPageItems/LoginLogo';
import { useSelector } from 'react-redux';
import { FindPasswordSubTitle } from './FindPasswordPage';
import UpdatePasswordForm from '../../components/findPasswordPageItems/UpdatePasswordForm';

const globalTokens = tokens.global;

export const UpdatePasswordPageContainer = styled(PageContainer)`
    height: 600px;
    padding: ${globalTokens.Spacing40.value}px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
`
export const UpdatePasswordContainer = styled(LoginContainer)`
    height: 350px;
    min-width: 300px;
`
export const UpdatePasswordLogo = styled(LoginFormLogo)`
`
export const UpdatePasswordTitle = styled(LoginTitle)`
    margin: ${globalTokens.Spacing28.value}px ${globalTokens.Spacing4.value}px ${globalTokens.Spacing8.value}px ${globalTokens.Spacing4.value}px;
`
export const UpdatePasswordSubTitle = styled(FindPasswordSubTitle)`
`
export const UpdatePasswordPage = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <UpdatePasswordPageContainer isDark={isDark}>
            <UpdatePasswordContainer isDark={isDark}>
                <UpdatePasswordLogo isDark={isDark}/>
                <UpdatePasswordTitle  isDark={isDark}>비밀번호 변경하기</UpdatePasswordTitle>
                <UpdatePasswordSubTitle>새로운 비밀번호로 변경해 주세요.</UpdatePasswordSubTitle>
                <UpdatePasswordForm/>
            </UpdatePasswordContainer>
        </UpdatePasswordPageContainer>
    );
};

export default UpdatePasswordPage;