import styled from "styled-components";
import tokens from '../../styles/tokens.json';
import { BodyTextTypo } from "../typographys/Typographys";
import { BigButton, PositiveTextButton, TextButton } from "../buttons/Buttons";
import closeBlack from '../../assets/images/icons/close/closeBlack.svg';
import closeWhite from '../../assets/images/icons/close/closeWhite.svg';

const globalTokens = tokens.global;

export const ModalBackdrop = styled.div<{
    isDark : boolean,
    isModalOpen : boolean }>`
        width: 100vw;
        height: 100vh;
        position: fixed;
        z-index: 1001;
        top: 0;
        left: 0;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        background-color: ${(props) =>
            props.isDark ? "rgba(255, 255, 255, 0.25)" : "rgba(0, 0, 0, 0.25)"};
        opacity: ${(props) => (props.isModalOpen ? `1` : `0`)};
        visibility: ${(props) => (props.isModalOpen ? "visible" : "hidden")};
`
export const ModalContainer = styled.div<{ isDark: boolean, height? : string }>`
  width: 320px;
  height: ${ props=>props.height? props.height : '150px' };
  padding: ${globalTokens.Spacing20.value}px ${globalTokens.Spacing8.value}px;
  background-color: ${(props) =>
    props.isDark ? globalTokens.Black.value : globalTokens.White.value};
  border-radius: ${globalTokens.BigRadius.value}px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
`
export const ModalContent = styled(BodyTextTypo)`
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
`
export const AlertModalButton = styled(PositiveTextButton)`
  width: 100%;
`
export const CloseButtonContainer = styled.div`
  width: 85%;
  display: flex;
  flex-direction: row;
  justify-content: end;
  align-items: center;
`
export const CloseButton = styled.button`
  width: 1.5rem;
  height: 1.5rem;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
`
export const CloseImg = styled.img<{isDark : boolean}>`
  width: 1.2rem;
  height: 1.2rem;
  content: url(${props=>props.isDark?closeWhite:closeBlack});
  transition: 300ms;
`