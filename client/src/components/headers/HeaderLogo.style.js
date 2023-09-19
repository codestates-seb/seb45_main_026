import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json'
import { BodyTextTypo } from '../../atoms/typographys/Typographys';

const globalTokens = tokens.global;

export const HeaderLogoToggleContainer = styled.div`
    display: flex;
    flex-direction: row;
    align-items: center;
`

export const HeaderLogoContainer = styled.section`
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    padding: ${globalTokens.Spacing4.value}px ${globalTokens.Spacing32.value}px;
    transition: 1000ms;
    &:active {
        opacity: 0.3;
    }
`
export const HeaderLogoTitle = styled(BodyTextTypo)`
    height: 50px;
    text-align: end;
    display: flex;
    flex-direction: column;
    justify-content: end;
    font-family: 'Saira Semi Condensed', sans-serif;
`
export const HeaderLogoImg = styled.img`
    width: 50px;
    transition: 300ms;
`
