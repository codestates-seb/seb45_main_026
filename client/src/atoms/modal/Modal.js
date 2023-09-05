import React from 'react';
import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json';
import { useDispatch, useSelector } from 'react-redux';
import { BodyTextTypo } from '../typographys/Typographys'
import { NegativeTextButton, PositiveTextButton } from '../buttons/Buttons';
import { setModal } from '../../redux/createSlice/UISettingSlice';
import { useModalClose } from '../../hooks/useModal';

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
    background-color: white;
    padding: 0 ${globalTokens.Spacing24.value}px;
`
export const ModalNegativeButton = styled(NegativeTextButton)`
    margin: ${globalTokens.Spacing4.value}px;
    background-color: white;
    padding: 0 ${globalTokens.Spacing24.value}px;
`

export const Modal = ({
    isModalOpen, 
    isBackdropClose,
    content,
    negativeButtonTitle,
    positiveButtonTitle,
}) => {
    const dispatch = useDispatch();
    const isDark = useSelector(state=>state.uiSetting.isDark);
    
    //모달을 닫는 메소드
    const closeModal = () => {
        dispatch(setModal({
            isModalOpen: false,
            isBackdropClose: false,
            content: '',
            negativeButtonTitle: '',
            positiveButtonTitle: '',
        }));
    }
    
    //backdrop을 클릭했을 때 실행되는 메소드
    const handleBackdropClick = () => {
        if(isBackdropClose) {
            closeModal();
        }
    }

    return (
        <ModalBackdrop 
            isModalOpen={isModalOpen} 
            onClick={ handleBackdropClick }
            isDark={isDark}>
                <ModalContainer isDark={isDark}>
                    <ModalContent>{content}</ModalContent>
                    <ModalButtonContainer>
                        <ModalNegativeButton 
                            onClick={()=>{}}>
                                {negativeButtonTitle}
                        </ModalNegativeButton>
                        <ModalPositiveButton 
                            onClick={()=>{}}>
                                {positiveButtonTitle}
                        </ModalPositiveButton>
                    </ModalButtonContainer>
                </ModalContainer>
        </ModalBackdrop>
    );
};

export default Modal;