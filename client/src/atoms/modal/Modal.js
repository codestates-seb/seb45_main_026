import React from 'react';
import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json';
import { useDispatch, useSelector } from 'react-redux';
import { BodyTextTypo } from '../typographys/Typographys'
import { NegativeTextButton, PositiveTextButton } from '../buttons/Buttons';

const globalTokens = tokens.global;

export const ModalBackdrop = styled.div`
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
    background-color: ${(props)=>props.isDark?'rgba(255, 255, 255, 0.25)':'rgba(0, 0, 0, 0.25)'};
    opacity: ${(props) => (props.isModalOpen ? `1` : `0`)};
    visibility: ${(props) => (props.isModalOpen ? "visible" : "hidden")};
`
export const ModalContainer = styled.div`
    width: 320px;
    height: 150px;
    padding: ${globalTokens.Spacing20.value}px 0;
    background-color: ${(props)=>props.isDark?globalTokens.Black.value:globalTokens.White.value};
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
export const ModalButtonContainer = styled.div`
    display: flex;
    flex-direction: row;
    justify-content: space-around;
    align-items: start;
`
export const ModalPositiveButton = styled(PositiveTextButton)`
    margin: ${globalTokens.Spacing4.value}px;
    padding: 0 ${globalTokens.Spacing24.value}px;
`
export const ModalNegativeButton = styled(NegativeTextButton)`
    margin: ${globalTokens.Spacing4.value}px;
    padding: 0 ${globalTokens.Spacing24.value}px;
`
//예, 아니오를 선택하는 모달
export const ConfirmModal = ({
    isModalOpen,
    setIsModalOpen,
    isBackdropClickClose,
    content,
    negativeButtonTitle,
    positiveButtonTitle,
    handleNegativeButtonClick, 
    handlePositiveButtonClick,
}) => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <ModalBackdrop 
            isModalOpen={isModalOpen}
            isDark={isDark}
            onClick={()=>{ isBackdropClickClose && setIsModalOpen(false) }}>
                <ModalContainer isDark={isDark} onClick={(e)=>{e.stopPropagation();}}>
                    <ModalContent isDark={isDark}>{content}</ModalContent>
                    <ModalButtonContainer>
                        <ModalNegativeButton isDark={isDark}
                            onClick={(e)=>{
                                handleNegativeButtonClick();
                            }}>
                                {negativeButtonTitle}
                        </ModalNegativeButton>
                        <ModalPositiveButton isDark={isDark}
                            onClick={(e)=>{
                                handlePositiveButtonClick();
                            }}>
                                {positiveButtonTitle}
                        </ModalPositiveButton>
                    </ModalButtonContainer>
                </ModalContainer>
        </ModalBackdrop>
    );
};
//확인 버튼만 있는 모달
export const AlertModal = ({
    isModalOpen,
    setIsModalOpen,
    isBackdropClickClose,
    content,
    buttonTitle,
    handleButtonClick,
}) => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <ModalBackdrop 
            isModalOpen={isModalOpen} 
            isDark={isDark}
            onClick={()=>{ isBackdropClickClose && setIsModalOpen(false) }}>
                <ModalContainer isDark={isDark} onClick={(e)=>{e.stopPropagation();}}>
                    <ModalContent isDark={isDark}>{content}</ModalContent>
                    <ModalButtonContainer>
                        <ModalPositiveButton 
                            type='button'
                            isDark={isDark}
                            onClick={handleButtonClick}>
                                {buttonTitle}
                        </ModalPositiveButton>
                    </ModalButtonContainer>
                </ModalContainer>
        </ModalBackdrop>
    );
}
