import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useSelector } from 'react-redux';
import { styled } from 'styled-components';
import FindPasswordPageInput from './FindPasswordPageInput';
import { SignupErrorTypo, SignupPositiveTypo } from '../SignupPageItems/SignupForm.style';

export const FindPasswordFormContainer = styled.form`
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
`

export const FindPasswordPageForm = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const [ isEmailValid, setIsEmailValid ] = useState(false);
    const {
        register,
        watch, 
        trigger,
        handleSubmit, 
        formState: { errors }, } = useForm();
    
    const handleCodeSendButtonClick = async () => {
        const isValid = await trigger('email');
        if(isValid) {

        }
    }
    const handleCodeConfirmButtonClick = async () => {
        const isValid = await trigger('emailCode');
        if(isValid) {

        }
    }
    
    return (
        <FindPasswordFormContainer>
            <FindPasswordPageInput 
                type='text'
                name='email'
                placeholder='이메일을 입력해 주세요.'
                register={register}
                required
                maxLength={20}
                minLength={5}
                pattern={/^[^\s@]+@[^\s@]+\.[^\s@]+$/i}
                isButton={true}
                buttonTitle='인증번호 발송'
                handleButtonClick={handleCodeSendButtonClick}/>
            {
                errors.email && errors.email.type==='required'
                    && <SignupErrorTypo isDark={isDark}>이메일을 입력해 주세요.</SignupErrorTypo>
            }
            {
                errors.email && errors.email.type==='pattern'
                    && <SignupErrorTypo isDark={isDark}>올바르지 않은 이메일 형식입니다.</SignupErrorTypo>
            }
            <FindPasswordPageInput 
                type='text'
                name='emailCode'
                placeholder='인증번호를 입력해 주세요.'
                register={register}
                required
                isButton={true}
                buttonTitle='인증번호 확인'
                handleButtonClick={handleCodeConfirmButtonClick}/>
            {
                errors.emailCode && errors.emailCode.type==='required'
                    && <SignupErrorTypo isDark={isDark}>인증번호를 입력해 주세요.</SignupErrorTypo>
            }
            {
                isEmailValid 
                    && <SignupPositiveTypo isDark={isDark}>이메일이 인증되었습니다. </SignupPositiveTypo>
            }
        </FindPasswordFormContainer>
    );
};

export default FindPasswordPageForm;