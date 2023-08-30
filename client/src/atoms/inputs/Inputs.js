import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json'

const globalTokens = tokens.global;

export const RegularInput = styled.input`
    background-color: rgba(255,255,255,0.25);
    padding: ${globalTokens.Spacing8.value}px;
    border-radius: ${globalTokens.RegularRadius.value}px;
    border: ${globalTokens.ThinHeight.value}px solid ${globalTokens.LightGray.value};
    font-size: ${globalTokens.BodyText.value}px;
    color: ${(props)=>props.isDark ? globalTokens.White.value : globalTokens.Black.value};
    &::placeholder {
        color: ${(props)=>props.isDark? globalTokens.LightGray.value : globalTokens.Gray.value};
    }
    &:focus {
        outline: ${globalTokens.RegularHeight.value}px solid ${(props)=>props.isDark ? globalTokens.White.value : globalTokens.Positive.value};
    }
`