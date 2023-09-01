import { styled } from 'styled-components';
import { ErrorTextTypo, LoginButton, LoginFormContainer } from '../loginPageItems/LoginForm.style';
import tokens from '../../styles/tokens.json'
import { RegularButton } from '../../atoms/buttons/Buttons';

const globalTokens = tokens.global;

export const SignupFormContainer = styled(LoginFormContainer)`
`
export const SignupFormInputContainer = styled.div`
    margin-top: ${globalTokens.Spacing8.value}px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: start;
`
export const SignupButton = styled(LoginButton)`
    margin-top: ${globalTokens.Spacing12.value}px;
    width: 300px;
`
export const SignupErrorTypo = styled(ErrorTextTypo)`
    width: 95%;
    text-align: end;
`
export const SignupWithButtonInputContainer = styled.div`
    display: flex;
    flex-direction: row;
`
export const SignupEmailConfirmButton = styled(RegularButton)`
    margin-left: ${globalTokens.Spacing4.value}px;
    padding: ${globalTokens.Spacing4.value}px;
`