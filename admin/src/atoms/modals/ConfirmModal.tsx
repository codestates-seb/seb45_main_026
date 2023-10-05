import React from 'react';
import { ModalBackdrop, ModalContainer } from './Modal.style';
import { useSelector } from 'react-redux';
import { RootState } from '../../redux/Store';

type confirmModalPropsType = {
    isModalOpen : boolean;
    setIsModalOpen : React.Dispatch<React.SetStateAction<boolean>>;
    isBackdropClickClose : boolean;
    content : string;
    positiveButtonTitle : string;
    negativeButtonTitle : string;
    handlePositiveButtonClick : Function;
    handleNegativeButtonClick : Function;
}

//예, 아니오를 선택하는 모달
const ConfirmModal = ({
    isModalOpen,
    setIsModalOpen,
    isBackdropClickClose,
    content,
    positiveButtonTitle,
    negativeButtonTitle,
    handlePositiveButtonClick,
    handleNegativeButtonClick,
} : confirmModalPropsType) => {
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);

    return (
        <ModalBackdrop 
            isDark={isDark}
            isModalOpen={isModalOpen}
            onClick={ ()=>{ isBackdropClickClose && setIsModalOpen(false) } }>
            <ModalContainer
                isDark={isDark}
                onClick={(e)=>{
                    e.stopPropagation();
                }}>

            </ModalContainer>
        </ModalBackdrop>
    );
};

export default ConfirmModal;