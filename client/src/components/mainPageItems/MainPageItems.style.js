import { BigNavyButton } from '../../atoms/buttons/Buttons';
import { BodyTextTypo, Heading1Typo } from '../../atoms/typographys/Typographys';
import tokens from '../../styles/tokens.json';
import { keyframes, styled } from 'styled-components';

const globalTokens = tokens.global;

const frameInTitleAnimation = keyframes`
  0% {
    opacity: 0;
    transform: translateY(-20%);
  }
  100%{
    opacity: 1;
    transform: translateY(0%);
  }
`;

export const MainPageFirstItemContainer = styled.section`
    padding: ${globalTokens.Spacing32.value}px;
    border-radius: ${globalTokens.BigRadius.value}px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
`
export const MainPageTitleTypo = styled(Heading1Typo)`
    margin-bottom: ${globalTokens.Spacing8.value}px;
    &.frame-out {
        opacity: 0;
    }
    &.frame-in {
        animation: ${frameInTitleAnimation} 1.5s;
    }
`
export const MainPageSubTitleTypo = styled(BodyTextTypo)`
    font-size: ${globalTokens.Heading5.value}px;
    margin-bottom: ${globalTokens.Spacing8.value}px;
    &.frame-in {
        animation: ${frameInTitleAnimation} 1s;
    }    
    &.frame-out {
        opacity: 0;
    }
`
export const MainPageStartButton = styled(BigNavyButton)`
    margin: ${globalTokens.Spacing8.value}px;
    background-color: rgba(255,255,255,0.25);
    color: ${(props)=>props.isDark? globalTokens.White.value : globalTokens.Black.value};
    border: ${globalTokens.RegularHeight.value}px solid ${globalTokens.Gray.value};
    
    &:hover {
        background-color: rgba(255,255,255,0.5);
    }

    &.frame-in {
        animation: ${frameInTitleAnimation} 1s;
    }    
    &.frame-out {
        opacity: 0;
    }
`