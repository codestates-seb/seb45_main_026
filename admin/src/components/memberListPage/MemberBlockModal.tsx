import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '../../redux/Store';
import { 
    CloseButton, 
    CloseButtonContainer, 
    CloseImg, 
    ModalBackdrop, 
    ModalContainer, 
} from '../../atoms/modals/Modal.style';
import { RegularInput, RegularLabel, RegularTextArea } from '../../atoms/inputs/Input.style';
import styled from 'styled-components';
import { BodyTextTypo } from '../../atoms/typographys/Typographys';
import { RegularButton } from '../../atoms/buttons/Buttons';
import tokens from '../../styles/tokens.json';
import { useMemberStatusUpdate } from '../../hooks/useMemberStateUpdate';
import { SubmitHandler, useForm } from 'react-hook-form';
import { InputErrorTypo } from '../loginPage/\bLoginForm.style';

const globalTokens = tokens.global;

type memberBlockModalProps = {
    isModalOpen : boolean;
    setIsModalOpen : React.Dispatch<React.SetStateAction<boolean>>;
    content : string;
    memberId : number;
}

type formValue = {
    days : number;
    blockReason : string;
}

const MemberBlockModal = ({
    isModalOpen,
    setIsModalOpen, 
    content,
    memberId,
} : memberBlockModalProps ) => {
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);
    const accessToken = useSelector((state:RootState)=>state.loginInfo.accessToken);
    
    const {
        mutate, isLoading, isError, error, isSuccess 
    } = useMemberStatusUpdate(()=>{
        setIsModalOpen(false);
    });

    const {
        register, 
        handleSubmit, 
        reset,
        formState: { 
            errors,
        }
    } = useForm<formValue>({ mode : 'all' });

    const onSubmit : SubmitHandler<formValue> = async (data) => {
        mutate({
            authorization : accessToken.authorization,
            memberId : memberId,
            days : data.days,
            blockReason : data.blockReason,
        });
        reset({ days: 0, blockReason: '', });
    }

    return (
        <ModalBackdrop 
            isDark={isDark}
            isModalOpen={isModalOpen}>
            <ModalContainer 
                isDark={isDark}
                height='300px'
                onClick={(e)=>{ e.stopPropagation(); }}>
                <CloseButtonContainer>
                    <CloseButton onClick={()=>{ setIsModalOpen(false) }}>
                        <CloseImg isDark={isDark}/>
                    </CloseButton>
                </CloseButtonContainer>
                <MemberBlockModalContent isDark={isDark}>{ content }</MemberBlockModalContent>
                <MemberBlockFormContainer onSubmit={handleSubmit(onSubmit)}>
                    <RegularLabel width='85%' isDark={isDark}>차단 기간</RegularLabel>
                    <RegularInput 
                        isDark={isDark} 
                        width='85%'
                        placeholder='차단 기간을 입력하세요.'
                        {...register('days',{
                            required: true,
                            pattern: /^[0-9]/i,
                        })}/>
                    { errors.days && errors.days.type==='required' && 
                        <InputErrorTypo 
                            isDark={isDark}
                            width='85%'>차단 기간을 입력해주세요.</InputErrorTypo> }
                    { errors.days && errors.days.type==='pattern' && 
                        <InputErrorTypo 
                            isDark={isDark}
                            width='85%'>숫자로 입력해주세요.</InputErrorTypo> }
                    <RegularLabel 
                        htmlFor='blockReason'
                        width='85%'
                        isDark={isDark}>차단 사유</RegularLabel>
                    <MemberBlockTextArea 
                        id='blockReason'
                        isDark={isDark}
                        width='85%'
                        placeholder='차단 사유를 입력해 주세요.'
                        {...register('blockReason',{
                            required: true,
                        })}/>
                    { errors.blockReason && errors.blockReason.type==='required' && 
                        <InputErrorTypo 
                            isDark={isDark}
                            width='85%'>차단 사유를 입력해주세요.</InputErrorTypo> }
                    <ButtonContainer>
                        <RegularButton 
                            type='submit'
                            isDark={isDark}>차단하기</RegularButton>
                    </ButtonContainer>
                </MemberBlockFormContainer>
            </ModalContainer>
        </ModalBackdrop>
    );
};
const MemberBlockModalContent = styled(BodyTextTypo)`
    width: 100%;
    height: 2rem;
    text-align: center;
`
const MemberBlockTextArea = styled(RegularTextArea)`
    flex-grow: 1;
`
const ButtonContainer = styled.div`
    width: 100%;
    margin: ${globalTokens.Spacing12.value}px 0 0 0;
    display: flex;
    flex-direction: row;
    justify-content: center;
`
const MemberBlockFormContainer = styled.form`
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
`
export default MemberBlockModal;