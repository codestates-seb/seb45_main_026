import React from 'react';
import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json'
import LoginFormLogo from './LoginLogo';
import LoginForm from './LoginForm';
import { useSelector } from 'react-redux';
import { Heading5Typo } from '../../atoms/typographys/Typographys';
import { SNSLogin } from './SNSLogin';
import { ExtraButton } from './ExtraButton';

const globalTokens = tokens.global;

export const LoginContainer = styled.div`
    height: 520px;
    padding: ${globalTokens.Spacing24.value}px;
    background-color: ${(props)=>props.isDark ? globalTokens.DarkGray.value : globalTokens.White.value};
    border-radius: ${globalTokens.BigRadius.value}px;
    position: relative;
    display: flex;
    flex-direction: column;
    justify-content: center;
    box-shadow: 
        ${(props)=>props.isDark?globalTokens.RegularWhiteShadow.value.x : globalTokens.RegularShadow.value.x}px 
        ${(props)=>props.isDark?globalTokens.RegularWhiteShadow.value.y : globalTokens.RegularShadow.value.y}px 
        ${(props)=>props.isDark?globalTokens.RegularWhiteShadow.value.blur : globalTokens.RegularShadow.value.blur}px 
        ${(props)=>props.isDark?globalTokens.RegularWhiteShadow.value.spread : globalTokens.RegularShadow.value.spread}px 
        ${(props)=>props.isDark?globalTokens.RegularWhiteShadow.value.color : globalTokens.RegularShadow.value.color};
`
export const LoginTitle = styled(Heading5Typo)`
    margin: ${globalTokens.Spacing40.value}px ${globalTokens.Spacing8.value}px ${globalTokens.Spacing8.value}px ${globalTokens.Spacing8.value}px ;
`
export const Login = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <LoginContainer isDark={isDark}>
            <LoginFormLogo/>
            <LoginTitle isDark={isDark}>로그인</LoginTitle>
            <LoginForm/>
            <SNSLogin/>
            <ExtraButton/>
        </LoginContainer>
    );
};

export default Login;
