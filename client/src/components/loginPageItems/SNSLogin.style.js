import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json'
import { LoginButton } from './LoginForm.style';
import { BodyTextTypo } from '../../atoms/typographys/Typographys';

const globalTokens = tokens.global;

export const SNSLoginButton = styled(LoginButton)`
    margin-top: 0;
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    border: ${globalTokens.ThinHeight.value}px solid ${(props)=>props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
`
export const SNSLoginButtonForGoogle = styled(SNSLoginButton)`
    background-color: ${globalTokens.White.value};
    &:hover {
        background-color: ${globalTokens.White.value};
    }
`
export const SNSLoginButtonForGitHub = styled(SNSLoginButton)`
    background-color: ${globalTokens.Black.value};
    &:hover {
        background-color: ${globalTokens.Black.value};
    }
`
export const SNSLoginButtonForKakao = styled(SNSLoginButton)`
    background-color: ${globalTokens.KakaoTalk.value};
    &:hover {
        background-color: ${globalTokens.KakaoTalk.value};
    }
`
export const SNSLoginButtonIcon = styled.img`
    width: 26px;
    height: 26px;
`
export const SNSLoginButtonText = styled(BodyTextTypo)`
    flex-grow: 1;
`
export const SNSLoginButtonTextWhite = styled(BodyTextTypo)`
    flex-grow: 3;
    color: ${globalTokens.White.value};
`