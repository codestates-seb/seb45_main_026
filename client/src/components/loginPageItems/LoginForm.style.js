import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json'
import { RegularInput } from '../../atoms/inputs/Inputs';
import { SmallTextTypo } from '../../atoms/typographys/Typographys';
import { BigRedButton } from '../../atoms/buttons/Buttons';

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
export const LoginFormInput = styled(RegularInput)`
    width: 250px;
`
export const ErrorTextTypo = styled(SmallTextTypo)`
    color: ${(props)=>props.isDark ? globalTokens.LightRed.value : globalTokens.Negative.value};
    text-align: end;
`
export const LoginButton = styled(BigRedButton)`
    margin: ${globalTokens.Spacing8.value}px;
    width: 250px;
`