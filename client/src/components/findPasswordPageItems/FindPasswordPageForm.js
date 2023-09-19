import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useDispatch, useSelector } from 'react-redux';
import { styled } from 'styled-components';
import { SignupButton } from '../SignupPageItems/SignupForm.style';
import { findPasswordEmailValidConfirmService, findPasswordEmailValidService } from '../../services/authServices';
import useConfirm from '../../hooks/useConfirm';
import { setFindPasswordEmail } from '../../redux/createSlice/LoginInfoSlice';
import { useNavigate } from 'react-router-dom';
import { Input, InputErrorTypo, InputPositiveTypo } from '../../atoms/inputs/Inputs';
import tokens from '../../styles/tokens.json';

const globalTokens = tokens.global;

export const FindPasswordFormContainer = styled.form`
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
`

export const FindPasswordPageForm = () => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const emailSendConfirm = useConfirm('이메일로 인증번호가 발송되었습니다. 확인 후 인증번호를 입력해 주세요.');
    const emailSendFailConfirm = useConfirm('입력하신 이메일로 인증번호를 발송할 수 없습니다. 이메일을 다시 확인해 주세요.');
    const emailCodeComplete = useConfirm('이메일이 인증되었습니다.');
    const emailCodeFail = useConfirm('이메일 인증에 실패했습니다.');
    const emailCodeRequireConfirm = useConfirm('이메일을 인증해 주세요.')
    const [ isEmailValid, setIsEmailValid ] = useState(false);
    const {
        register,
        watch, 
        trigger,
        handleSubmit, 
        formState: { errors }, } = useForm();
    
    //비밀번호 변경하기 버튼 누르면 동작함
    const onSubmit = async () => {
        if(!isEmailValid) {
            emailCodeRequireConfirm();
            return;
        } else {
            const email = watch('email');
            dispatch(setFindPasswordEmail(email));
            navigate('/findPassword/updatePassword');
        }
    }
    //이메일 인증번호 발송 버튼 누르면 실행됨
    const handleCodeSendButtonClick = async () => {
        const email = watch('email','');
        const isValid = await trigger('email');

        if(isValid) {
            const response = await findPasswordEmailValidService(email);
            if(response.status==='success') {
                emailSendConfirm();
            } else {
                emailSendFailConfirm();
            }
        }
    }
    //이메일 인증번호 확인 버튼 누르면 실행됨
    const handleCodeConfirmButtonClick = async () => {
        const email = watch('email','');
        const emailCode = watch('emailCode','');
        const isValid = await trigger('emailCode');
        if(isValid) {
            const response = await findPasswordEmailValidConfirmService(email,emailCode);
            if(response.status==='success') {
                emailCodeComplete();
                setIsEmailValid(true);
            } else {
                emailCodeFail();
                setIsEmailValid(false);
            }
        }
    }
    
    return (
        <>
        <FindPasswordFormContainer onSubmit={handleSubmit(onSubmit)}>
            <Input 
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
                handleButtonClick={handleCodeSendButtonClick}
                onChange={()=>{ setIsEmailValid(false) }}/>
            { errors.email && errors.email.type==='required'
                    && <InputErrorTypo isDark={isDark}>이메일을 입력해 주세요.</InputErrorTypo> }
            { errors.email && errors.email.type==='pattern'
                    && <InputErrorTypo isDark={isDark}>올바르지 않은 이메일 형식입니다.</InputErrorTypo> }
            <Input 
                marginTop={globalTokens.Spacing8.value}
                type='text'
                name='emailCode'
                placeholder='인증번호를 입력해 주세요.'
                register={register}
                required
                isButton={true}
                buttonTitle='인증번호 확인'
                handleButtonClick={handleCodeConfirmButtonClick}
                onChange={()=>{ setIsEmailValid(false) }}/>
            { errors.emailCode && errors.emailCode.type==='required'
                    && <InputErrorTypo isDark={isDark}>인증번호를 입력해 주세요.</InputErrorTypo> }
            { isEmailValid 
                    && <InputPositiveTypo isDark={isDark}>이메일이 인증되었습니다. </InputPositiveTypo> }
            <SignupButton isDark={isDark} type='submit'>비밀번호 변경하기</SignupButton>
        </FindPasswordFormContainer>
        </>
    );
};

export default FindPasswordPageForm;