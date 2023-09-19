import { useState } from "react"
import { ConfirmModal } from "./Modal";
import { RegularButton } from "../buttons/Buttons";

const ConfirmModalPage = ({
        content, 
        positiveButtonTitle, 
        negativeButtonTitle,
        isBackdropClickClose,
    }) => {
    const [ isModalOpen, setIsModalOpen ] = useState(false);

    const handlePositiveButtonClick = () => {
        console.log('Positive Button is Clicked!');
        setIsModalOpen(false);
    }

    const handleNegativeButtonClick = () => {
        console.log('Negative Button is Clicked!');
        setIsModalOpen(false);
    }

    return (
        <div>
            <ConfirmModal
                isModalOpen={isModalOpen}
                setIsModalOpen={setIsModalOpen}
                isBackdropClickClose={isBackdropClickClose}
                content={content}
                negativeButtonTitle={negativeButtonTitle}
                positiveButtonTitle={positiveButtonTitle}
                handleNegativeButtonClick={handleNegativeButtonClick}
                handlePositiveButtonClick={handlePositiveButtonClick}/>
            <RegularButton onClick={()=>{setIsModalOpen(!isModalOpen)}}>
                모달 테스트 버튼
            </RegularButton>
        </div>
    )
}

export default {
    title: 'Components/Modal',
    component: ConfirmModalPage,
    argTypes: {
        content:'text', 
        positiveButtonTitle:'text', 
        negativeButtonTitle:'text',
        isBackdropClickClose:'boolean',
    }
}

export const ConfirmModalTemplate = (args) => <ConfirmModalPage {...args}/>
ConfirmModalTemplate.args = {
    content: '모달입니까?',
    positiveButtonTitle: '예',
    negativeButtonTitle: '아니오',
    isBackdropClickClose: true,
} 