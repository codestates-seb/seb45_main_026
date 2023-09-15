import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import { useForm } from 'react-hook-form';
import { emailValidationConfirmService, emailValidationService, signupService } from '../../services/authServices';
import { useNavigate } from 'react-router-dom';
import { Checkbox } from '../../atoms/buttons/Checkbox';
import { Input, InputErrorTypo, InputPositiveTypo } from '../../atoms/inputs/Inputs';
import tokens from '../../styles/tokens.json'
import { SignupFormContainer, SignupAgreeContainer, SignupAgreeCheckContainer, SignupAgreeCheckLabel, SignupButton } from './SignupForm.style';
import { AlertModal, ConfirmModal } from '../../atoms/modal/Modal';

const globalTokens = tokens.global;

export const SignupForm = () => {
    const navigate = useNavigate();
    const [ is이메일인증번호발송팝업, setIs이메일인증번호발송팝업 ] = useState(false);
    const [ is이메일인증완료팝업, setIs이메일인증완료팝업 ] = useState(false);
    const [ is이메일인증실패팝업, setIs이메일인증실패팝업 ] = useState(false);
    const [ is이메일인증요청팝업, setIs이메일인증요청팝업 ] = useState(false);
    const [ is이용약관동의요청팝업, setIs이용약관동의요청팝업 ] = useState(false);
    const [ is개인정보동의요청팝업, setIs개인정보동의요청팝업 ] = useState(false);
    const [ is회원가입성공팝업, setIs회원가입성공팝업 ] = useState(false);
    const [ isEmailValid, setIsEmailValid ] = useState(false);
    const [ isUseAgree, setIsUseAgree ] = useState(false);
    const [ isPrivacyAgree, setIsPrivacyAgree ] = useState(false);
    const {
        register,
        watch, 
        trigger,
        handleSubmit, 
        formState: { errors }, } = useForm();
     const isDark = useSelector(state=>state.uiSetting.isDark);

     //가입하기 버튼 눌렀을 때 동작함
     const onSubmit = async (data) => {
        if(isEmailValid&&isUseAgree&&isPrivacyAgree){
            const response = await signupService({data:data});
            if(response.status === 'success') {
                setIs회원가입성공팝업(true);
            }
        } else if(!isEmailValid) {
            setIs이메일인증요청팝업(true);
            return;
        } else if(!isUseAgree) {
            setIs이용약관동의요청팝업(true);
            return;
        } else {
            setIs개인정보동의요청팝업(true);
            return;
        }
    }
    //이메일 인증번호 발송 버튼 눌렀을 때 동작함
    const handleCodeSendButtonClick = async () => {
        const email = watch('email','');
        const isValid = await trigger('email');
        if( isValid ) {
            //이메일 유효성 검사를 통과했으면 입력한 이메일로 인증코드를 전송함
            const response = await emailValidationService(email);
            if(response.status==='success') {
                setIs이메일인증번호발송팝업(true);
            } else {
                window.confirm(`${response.data}`)
            }
        }
    }
    //이메일 인증번호 확인 버튼 눌렀을 때 동작함
    const handleCodeConfirmButtonClick = async () => {
        const email = watch('email','');
        const emailCode = watch('emailCode','');
        const isValid = await trigger('emailCode');
        if( isValid ){
            //이메일 코드 유효성 검사를 통과했으면
            const response = await emailValidationConfirmService(email,emailCode);
            if(response.status==='success') {
                setIs이메일인증완료팝업(true);
                setIsEmailValid(true);
            } else {
                setIs이메일인증실패팝업(true);
                setIsEmailValid(false);
            }
        } 
    }
    //전체동의 체크박스 눌렀을 때 동작함
    const handleWholeAgreeCheckClick = () => {
        if(isUseAgree&&isPrivacyAgree) {
            setIsUseAgree(false);
            setIsPrivacyAgree(false);
        } else {
            setIsUseAgree(true);
            setIsPrivacyAgree(true);
        }
    }
    //이용약관 동의 체크박스 눌렀을 때 동작함
    const handleUseAgreeCheckClick = () => {
        setIsUseAgree(!isUseAgree);
    }
    //개인정보 이용 동의 체크박스 눌렀을 때 동작함
    const handlePrivacyAgreeCkeckClick = () => {
        setIsPrivacyAgree(!isPrivacyAgree);
    }
    
    return (
        <>
        {/* 이메일 인증번호 발송 팝업 */}
        <AlertModal
            isModalOpen={is이메일인증번호발송팝업}
            setIsModalOpen={setIs이메일인증번호발송팝업}
            isBackdropClickClose={true}
            content='이메일로 인증번호가 발송되었습니다. 확인 후 인증번호를 입력해 주세요.'
            buttonTitle='확인'
            handleButtonClick={()=>{ setIs이메일인증번호발송팝업(false) }}/>
        <AlertModal
            isModalOpen={is이메일인증완료팝업}
            setIsModalOpen={setIs이메일인증완료팝업}
            isBackdropClickClose={true}
            content='이메일이 인증되었습니다.'
            buttonTitle='확인'
            handleButtonClick={()=>{ setIs이메일인증완료팝업(false); }}/>
        <AlertModal
            isModalOpen={is이메일인증실패팝업}
            setIsModalOpen={setIs이메일인증실패팝업}
            isBackdropClickClose={true}
            content='이메일 인증 실패했습니다.'
            buttonTitle='확인'
            handleButtonClick={()=>{ setIs이메일인증실패팝업(false) }}/>
        <AlertModal
            isModalOpen={is이메일인증요청팝업}
            setIsModalOpen={setIs이메일인증요청팝업}
            isBackdropClickClose={true}
            content='이메일을 인증해 주세요.'
            buttonTitle='확인'
            handleButtonClick={()=>{ setIs이메일인증요청팝업(false) }}/>
        <AlertModal
            isModalOpen={is이용약관동의요청팝업}
            setIsModalOpen={setIs이용약관동의요청팝업}
            isBackdropClickClose={true}
            content='이용약관에 동의해 주세요.'
            buttonTitle='확인'
            handleButtonClick={()=>{ setIs이용약관동의요청팝업(false) }}/>
        <AlertModal
            isModalOpen={is개인정보동의요청팝업}
            setIsModalOpen={setIs개인정보동의요청팝업}
            isBackdropClickClose={true}
            content='개인정보 처리방침에 동의해 주세요.'
            buttonTitle='확인'
            handleButtonClick={()=>{ setIs개인정보동의요청팝업(false) }}/>
        <ConfirmModal
            isModalOpen={is회원가입성공팝업}
            setIsModalOpen={setIs회원가입성공팝업}
            isBackdropClickClose={false}
            content='회원가입 성공했습니다.'
            negativeButtonTitle='홈으로 가기'
            positiveButtonTitle='로그인'
            handleNegativeButtonClick={()=>{navigate('/')}}
            handlePositiveButtonClick={()=>{navigate('/login')}}/>
        <SignupFormContainer onSubmit={handleSubmit(onSubmit)}>
            <Input 
                marginTop={globalTokens.Spacing8.value}
                label='이메일' 
                name='email'
                type='text'
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
            {
                errors.email && errors.email.type==='required' 
                    && <InputErrorTypo isDark={isDark}>이메일을 입력해 주세요.</InputErrorTypo>
            }
            {
                errors.email && errors.email.type==='pattern'
                    && <InputErrorTypo isDark={isDark}>올바르지 않은 이메일 형식입니다.</InputErrorTypo>
            }
            <Input 
                marginTop={globalTokens.Spacing8.value}
                name='emailCode'
                type='text'
                placeholder='인증코드를 입력해 주세요.'
                register={register}
                required={true}
                isButton={true}
                buttonTitle='인증번호 확인'
                handleButtonClick={handleCodeConfirmButtonClick}/>
            {
                errors.emailCode && errors.emailCode.type==='required'
                    && <InputErrorTypo isDark={isDark}>인증번호를 입력해 주세요.</InputErrorTypo>
            }
            {
                isEmailValid 
                    && <InputPositiveTypo isDark={isDark}>이메일이 인증되었습니다.</InputPositiveTypo>
            }
            <Input 
                marginTop={globalTokens.Spacing8.value}
                label='비밀번호' name='password' type='password' 
                placeholder='비밀번호를 입력해 주세요.' 
                register={register} 
                required 
                maxLength={20} 
                minLength={9}
                pattern={/^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$/}
                isButton={false}/>
            { errors.password && errors.password.type==='required' 
                && <InputErrorTypo isDark={isDark}>비밀번호를 입력해 주세요.</InputErrorTypo> }
            { errors.password && errors.password.type==='minLength'
                && <InputErrorTypo isDark={isDark}>비밀번호는 9자 이상이어야 합니다.</InputErrorTypo> }
            { errors.password && errors.password.type==='maxLength'
                && <InputErrorTypo isDark={isDark}>20자 이하로 입력해 주세요.</InputErrorTypo> }
            { errors.password && errors.password.type==='pattern'
                && <InputErrorTypo isDark={isDark}>영문, 숫자, 특수문자를 포함해서 입력해 주세요. </InputErrorTypo> }
            <Input 
                marginTop={globalTokens.Spacing8.value}
                label='비밀번호 확인' name='passwordConfirm' type='password' 
                placeholder='비밀번호 확인을 입력해 주세요.' 
                register={register} required
                validateFunc={()=>{
                    return watch('password')===watch('passwordConfirm')
                }}
                isButton={false}/>
            { errors.passwordConfirm && errors.passwordConfirm.type==='required'
                && <InputErrorTypo isDark={isDark}>비밀번호 확인을 입력해 주세요.</InputErrorTypo> }
            { errors.passwordConfirm && errors.passwordConfirm.type==='validate'
                && <InputErrorTypo isDark={isDark}>비밀번호와 비밀번호 확인이 일치하지 않습니다.</InputErrorTypo> }
            <Input 
                marginTop={globalTokens.Spacing8.value}
                label='닉네임' name='nickname' type='text' 
                placeholder='닉네임을 입력해 주세요.' 
                register={register} required
                maxLength={20}
                isButton={false}/>
            { errors.nickname && errors.nickname.type==='required'
                && <InputErrorTypo isDark={isDark}>닉네임을 입력해 주세요.</InputErrorTypo> }
            {
                errors.nickname && errors.nickname.type==='maxLength'
                    && <InputErrorTypo isDark={isDark}>20자 이하로 입력해 주세요.</InputErrorTypo>
            }
            <SignupAgreeContainer>
                <SignupAgreeCheckContainer>
                    <Checkbox 
                        isDark={isDark} 
                        isChecked={isUseAgree&&isPrivacyAgree}
                        setIsChecked={handleWholeAgreeCheckClick}/>
                    <SignupAgreeCheckLabel isDark={isDark}>전체 동의</SignupAgreeCheckLabel>
                </SignupAgreeCheckContainer>
                <SignupAgreeCheckContainer>
                    <Checkbox
                        isDark={isDark} 
                        isChecked={isUseAgree}
                        setIsChecked={handleUseAgreeCheckClick}/>
                    <SignupAgreeCheckLabel isDark={isDark}>이용약관 동의</SignupAgreeCheckLabel>
                </SignupAgreeCheckContainer>
                <SignupAgreeCheckContainer>
                    <Checkbox
                        isDark={isDark} 
                        isChecked={isPrivacyAgree}
                        setIsChecked={handlePrivacyAgreeCkeckClick}/>
                    <SignupAgreeCheckLabel isDark={isDark}>개인정보 처리방침 동의</SignupAgreeCheckLabel>
                </SignupAgreeCheckContainer>
            </SignupAgreeContainer>
            <SignupButton isDark={isDark} type='submit'>가입하기</SignupButton>
        </SignupFormContainer>
        </>
    );
};

export default SignupForm;