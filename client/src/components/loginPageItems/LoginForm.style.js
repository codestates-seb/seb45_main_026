import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json'
import { BigButton } from '../../atoms/buttons/Buttons';

const globalTokens = tokens.global;

export const LoginFormContainer = styled.form`
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 100%;
`
export const LoginFormInputContainer = styled.div`
    margin: ${globalTokens.Spacing4.value}px;
`
export const LoginButton = styled(BigButton)`
    margin: ${globalTokens.Spacing8.value}px;
    width: 250px;
`