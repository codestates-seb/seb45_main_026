import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json'

const globalTokens = tokens.global;

export const HeaderProfileContainer = styled.div`
    padding: ${globalTokens.Spacing4.value}px ${globalTokens.Spacing32.value}px;
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: center;
    cursor: pointer;
`
export const HeaderProfileImg = styled.img`
    margin-left: ${globalTokens.Spacing8.value}px;
    width: 35px;
`