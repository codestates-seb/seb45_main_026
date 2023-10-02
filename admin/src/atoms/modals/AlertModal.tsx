import React, { EventHandler } from 'react';
import { AlertModalButton, ModalBackdrop, ModalContainer, ModalContent } from './Modal.style';
import { useSelector } from 'react-redux';
import { RootState } from '../../redux/Store';

type alertModalPropsType = {
    isModalOpen : boolean;
    setIsModalOpen : React.Dispatch<React.SetStateAction<boolean>>;
    isBackdropClickClose : boolean;
    content: string;
}

const AlertModal = ({
    isModalOpen,
    setIsModalOpen,
    isBackdropClickClose,
    content,
}:alertModalPropsType) => {
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);

    return (
        <ModalBackdrop 
            isDark={isDark} isModalOpen={isModalOpen}
            onClick={ ()=>{ isBackdropClickClose && setIsModalOpen(false) } }>
            <ModalContainer isDark={isDark}>
                <ModalContent isDark={isDark}>{content}</ModalContent>
                <AlertModalButton 
                    isDark={isDark}
                    onClick={ ()=> { setIsModalOpen(false) }}>확인</AlertModalButton>
            </ModalContainer>
        </ModalBackdrop>
    );
};

export default AlertModal;