import { useState } from "react";
import { AlertModal } from "./Modal";
import { RegularButton } from "../buttons/Buttons";

const AlertModalPage = ({content, buttonTitle}) => {
    const [isModalOpen, setIsModalOpen] = useState(false);

    const handleButtonClick = () => {
        console.log('확인 버튼을 눌렀습니다.');
        setIsModalOpen(false);
    }

    return (
        <div>
            <AlertModal
                isModalOpen={isModalOpen}
                setIsModalOpen={setIsModalOpen}
                isBackdropClickClose={true}
                content={content}
                buttonTitle={buttonTitle}
                handleButtonClick={handleButtonClick}/>
            <RegularButton onClick={()=>{setIsModalOpen(!isModalOpen)}}>
                모달 테스트 버튼
            </RegularButton>
        </div>
    );
}

export default {
    title: 'Components/Modal',
    component: AlertModalPage,
    argTypes: {
        content: { control: 'text' },
        buttonTitle: { control: 'text' },
    }
}

export const AlertModalTemplate = (args) => <AlertModalPage {...args}/>
AlertModalTemplate.args = {
    content: '모달 테스트입니다!',
    buttonTitle: '확인',
}