import styled from "styled-components";
import tokens from '../../styles/tokens.json';
import { Heading5Typo, NegativeTextTypo } from "../../atoms/typographys/Typographys";
import { BigButton } from "../../atoms/buttons/Buttons";

const globalTokens = tokens.global;

export const LoginContainer = styled.section<{isDark : boolean}>`
    height: 350px;
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
export const FormContainer = styled.form`
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 10px;
`
export const LoginTitleTypo = styled(Heading5Typo)`
    width: 100%;
    margin: ${globalTokens.Spacing12.value}px 0; 
`
export const InputContainer = styled.div`
    display: flex;
    flex-direction: column;
`
export const InputErrorTypo = styled(NegativeTextTypo)<{width?:string}>`
    width: ${props=>props.width?props.width:'100%'};
    text-align: end;
`
export const LoginButton = styled(BigButton)`
    width: 100%;
`