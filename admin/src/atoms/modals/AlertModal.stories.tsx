import { Meta, Story } from "@storybook/react";
import { AlertModalButton, ModalBackdrop, ModalContainer, ModalContent } from "./Modal.style";
import { useState } from "react";
import { RegularButton } from "../buttons/Buttons";

type alertModalExPropsType = {
    isDark: boolean;
    content: string;
    isModalBackdropClickClose: boolean;
}

const AlertModalEx = ({
    isDark,
    content,
    isModalBackdropClickClose,
} : alertModalExPropsType) => {
    const [ isModalOpen, setIsModalOpen ] = useState(false); 

    return (
        <>
            <RegularButton isDark={isDark} onClick={()=>{ setIsModalOpen(true) }}>모달 열기</RegularButton>
            <ModalBackdrop 
                isDark={isDark}
                isModalOpen={isModalOpen}
                onClick={()=>{ isModalBackdropClickClose && setIsModalOpen(false) }}>
                    <ModalContainer 
                        isDark={isDark}
                        onClick={(e)=>{ e.stopPropagation() }}>
                        <ModalContent isDark={isDark}>{content}</ModalContent>
                        <AlertModalButton 
                            isDark={isDark}
                            onClick={()=>{ setIsModalOpen(false) }}>확인</AlertModalButton>
                    </ModalContainer>
            </ModalBackdrop>
        </>
    )
}

export default {
    title: 'Components/Modal',
    component: AlertModalEx,
    argTypes: {
        isDark: { controls: 'boolean' },
        content: { controls: 'text' },
        isModalBackdropClickClose: { controls: 'boolean' }
    }
}

export const AlertModalTemplate = (args:alertModalExPropsType) => <AlertModalEx {...args}/>
AlertModalTemplate.args = {
    isDark: false,
    content: '나는 모달입니다.',
    isModalBackdropClickClose: true,
}