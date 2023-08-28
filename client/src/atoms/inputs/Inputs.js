import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json'

const globalTokens = tokens.global;

export const RegularInput = styled.input`
    padding: ${globalTokens.Spacing4.value}px ${globalTokens.Spacing8.value}px;
    border-radius: ${globalTokens.RegularRadius.value}px;
    border: ${globalTokens.ThinHeight.value}px solid ${(props)=>props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
    &::placeholder {
        color: ${globalTokens.Gray.value};
    }
    &:focus {
        outline: ${globalTokens.RegularHeight.value}px solid ${globalTokens.LightNavy.value};
    }
`