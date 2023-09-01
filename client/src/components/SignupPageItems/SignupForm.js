import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import { useForm } from 'react-hook-form';
import { 
    SignupFormContainer,
    SignupButton,
    SignupErrorTypo,
    SignupFormInputContainer,
    SignupFormLabel,
    SignupWithButtonInputContainer,
    SignupFormInput,
    SignupEmailConfirmButton
} from './SignupForm.style';
import SignupInput from './SignupInput';
import { ErrorTextTypo } from '../loginPageItems/LoginForm.style';
import { emailValidationConfirmService, emailValidationService, signupService } from '../../services/authServices';
import { useNavigate } from 'react-router-dom';

export const SignupForm = () => {
    const navigate = useNavigate();
    const [ isEmailValid, setIsEmailValid ] = useState(false);
    const {
        register,
        watch, 
        handleSubmit, 
        formState: { errors }, } = useForm();
     const isDark = useSelector(state=>state.uiSetting.isDark);

     //가입하기 버튼 눌렀을 때 동작함
     const onSubmit = async (data) => {
        if(isEmailValid){
            const response = await signupService({data:data});
            if(response.status === 'success') {
                console.log('회원가입 성공!');
                navigate('/login');
            }
        }
    }
    //이메일 인증번호 발송 버튼 눌렀을 때 동작함
    const handleCodeSendButtonClick = async () => {
        const email = watch('email','')
        if(!errors.email && email.length>5) {
            //이메일 유효성 검사를 통과했으면 입력한 이메일로 인증코드를 전송함
            const response = await emailValidationService(email);
            if(response.status==='success') {
                window.confirm('이메일로 인증번호가 발송되었습니다. 확인 후 인증코드를 입력해주세요.')
            } else {
                window.confirm(`${response.data}`)
            }
        }
    }

    //이메일 인증번호 확인 버튼 눌렀을 때 동작함
    const handleCodeConfirmButtonClick = async () => {
        const email = watch('email','');
        const emailCode = watch('emailCode','');
        if(!errors.email && !errors.emailCode && email.length>5 && emailCode.length>4){
            //이메일 코드를 5글자 이상 입력했고 이메일 유효성 검사를 통과했으면
            const response = await emailValidationConfirmService(email,emailCode);
            if(response.status==='success') {
                window.confirm('이메일이 인증되었습니다.');
                setIsEmailValid(true);
            } else {
                setIsEmailValid(false);
            }
        }
    }

    return (
        <SignupFormContainer onSubmit={handleSubmit(onSubmit)}>
            <SignupFormInputContainer isDark={isDark}>
                <SignupFormLabel isDark={isDark}>이메일</SignupFormLabel>
                <SignupWithButtonInputContainer>
                    <SignupFormInput 
                        isDark={isDark} isButton={true} 
                        type='text' placeholder='이메일을 입력해 주세요.'
                        {...register('email', { 
                            required: true, 
                            maxLength: 20,
                            pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/i, })}/>
                    <SignupEmailConfirmButton 
                        isDark={isDark} 
                        onClick={handleCodeSendButtonClick}>
                            인증번호 발송
                    </SignupEmailConfirmButton>
                </SignupWithButtonInputContainer>
                {
                    errors.email && errors.email.type==='required' 
                        && <ErrorTextTypo isDark={isDark}>이메일을 입력해 주세요.</ErrorTextTypo>
                }
                {
                    errors.email && errors.email.type==='pattern'
                        && <ErrorTextTypo isDark={isDark}>올바르지 않은 이메일 형식입니다.</ErrorTextTypo>
                }
            </SignupFormInputContainer>
            <SignupFormInputContainer isDark={isDark}>
                <SignupWithButtonInputContainer>
                <SignupFormInput isDark={isDark} isButton={true}
                type='text' placeholder='인증코드를 입력해 주세요.'
                {...register('emailCode', {
                    required: true,
                })}/>
                <SignupEmailConfirmButton 
                    isDark={isDark}
                    onClick={handleCodeConfirmButtonClick}>
                    인증번호 확인
                </SignupEmailConfirmButton>
                </SignupWithButtonInputContainer>
                {
                    errors.emailCode && errors.emailCode.type==='required'
                        && <ErrorTextTypo isDark={isDark}>인증번호를 입력해 주세요.</ErrorTextTypo>
                }
            </SignupFormInputContainer>
            <SignupInput label='비밀번호' name='password' type='password' 
                placeholder='비밀번호를 입력해 주세요.' 
                register={register} 
                required 
                maxLength={20} 
                minLength={9}
                pattern={/^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$/}/>
            { errors.password && errors.password.type==='required' 
                && <SignupErrorTypo isDark={isDark}>비밀번호를 입력해 주세요.</SignupErrorTypo> }
            { errors.password && errors.password.type==='minLength'
                && <SignupErrorTypo isDark={isDark}>비밀번호는 9자 이상이어야 합니다.</SignupErrorTypo> }
            { errors.password && errors.password.type==='maxLength'
                && <SignupErrorTypo isDark={isDark}>20자 이하로 입력해 주세요.</SignupErrorTypo> }
            { errors.password && errors.password.type==='pattern'
                && <SignupErrorTypo isDark={isDark}>영문, 숫자, 특수문자를 포함해서 입력해 주세요. </SignupErrorTypo> }
            <SignupInput 
                label='비밀번호 확인' name='passwordConfirm' type='password' 
                placeholder='비밀번호 확인을 입력해 주세요.' 
                register={register} required
                validateFunc={()=>{
                    return watch('password')===watch('passwordConfirm')
                }}/>
            { errors.passwordConfirm && errors.passwordConfirm.type==='required'
                && <SignupErrorTypo isDark={isDark}>비밀번호 확인을 입력해 주세요.</SignupErrorTypo> }
            { errors.passwordConfirm && errors.passwordConfirm.type==='validate'
                && <SignupErrorTypo isDark={isDark}>비밀번호와 비밀번호 확인이 일치하지 않습니다.</SignupErrorTypo> }
            <SignupInput 
                label='닉네임' name='nickname' type='text' 
                placeholder='닉네임을 입력해 주세요.' 
                register={register} required
                maxLength={20}/>
            { errors.nickname && errors.nickname.type==='required'
                && <SignupErrorTypo isDark={isDark}>닉네임을 입력해 주세요.</SignupErrorTypo> }
            {
                errors.nickname && errors.nickname.type==='maxLength'
                    && <SignupErrorTypo isDark={isDark}>20자 이하로 입력해 주세요.</SignupErrorTypo>
            }
            <SignupButton isDark={isDark} type='submit'>가입하기</SignupButton>
        </SignupFormContainer>
    );
};

export default SignupForm;