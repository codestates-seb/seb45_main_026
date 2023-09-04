import { styled } from 'styled-components';
import { LoginButton } from '../loginPageItems/LoginForm.style';
import tokens from '../../styles/tokens.json'
import { BodyTextTypo } from '../../atoms/typographys/Typographys';

const globalTokens = tokens.global;
export const SignupFormContainer = styled.form`
`
export const SignupButton = styled(LoginButton)`
    margin-top: ${globalTokens.Spacing12.value}px;
    width: 300px;
`
export const SignupAgreeContainer = styled.section`
    width: 95%;
    margin: ${globalTokens.Spacing8.value}px;
`
export const SignupAgreeCheckContainer = styled.div`
    margin-top: ${globalTokens.Spacing8.value}px;
    display: flex;
    flex-direction: row;
    justify-content: start;
    align-items: center;
`
export const SignupAgreeCheckLabel = styled(BodyTextTypo)`
    margin-left: ${globalTokens.Spacing4.value}px;
`