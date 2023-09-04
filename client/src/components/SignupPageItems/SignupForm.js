import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import { useForm } from 'react-hook-form';
import { 
    SignupFormContainer,
    SignupButton,
    SignupErrorTypo,
    SignupAgreeCheckContainer,
    SignupAgreeCheckLabel,
    SignupAgreeContainer,
    SignupPositiveTypo,
} from './SignupForm.style';
import SignupInput from './SignupInput';
import { emailValidationConfirmService, emailValidationService, signupService } from '../../services/authServices';
import { useNavigate } from 'react-router-dom';
import useConfirm from '../../hooks/useConfirm';
import { Checkbox } from '../../atoms/buttons/Checkbox';

export const SignupForm = () => {
    const navigate = useNavigate();
    const emailCodeSendConfirm = useConfirm('이메일로 인증번호가 발송되었습니다. 확인 후 인증코드를 입력해 주세요.');
    const emailCodeComplete = useConfirm('이메일이 인증되었습니다.');
    const emailCodeFail = useConfirm('이메일 인증에 실패했습니다.');
    const emailCodeRequiredConfirm = useConfirm('이메일을 인증해 주세요.');
    const agreeUseRequiredConfirm = useConfirm('이용약관에 동의해 주세요.');
    const agreePrivacyRequiredConfirm = useConfirm('개인정보 처리방침에 동의해 주세요.');
    const successConfirm = useConfirm('회원가입 성공하였습니다.',
        ()=>{ navigate('/login'); }, ()=>{} );

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
                successConfirm();
                navigate('/login');
            }
        } else if(!isEmailValid) {
            emailCodeRequiredConfirm();
            return;
        } else if(!isUseAgree) {
            agreeUseRequiredConfirm();
            return;
        } else {
            agreePrivacyRequiredConfirm();
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
                emailCodeSendConfirm();
            } else {
                window.confirm(`${response.data}`)
            }
        }
    }
    //이메일 인증번호 확인 버튼 눌렀을 때 동작함
    const handleCodeConfirmButtonClick = async () => {
        console.log('인증번호 확인 버튼 누름')
        const email = watch('email','');
        const emailCode = watch('emailCode','');
        if(!errors.email && !errors.emailCode && email.length>5 && emailCode.length>4){
            //이메일 코드를 5글자 이상 입력했고 이메일 유효성 검사를 통과했으면
            const response = await emailValidationConfirmService(email,emailCode);
            if(response.status==='success') {
                emailCodeComplete();
                setIsEmailValid(true);
            } else {
                emailCodeFail();
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
        <SignupFormContainer onSubmit={handleSubmit(onSubmit)}>
            <SignupInput 
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
                handleButtonClick={handleCodeSendButtonClick}/>
            {
                errors.email && errors.email.type==='required' 
                    && <SignupErrorTypo isDark={isDark}>이메일을 입력해 주세요.</SignupErrorTypo>
            }
            {
                errors.email && errors.email.type==='pattern'
                    && <SignupErrorTypo isDark={isDark}>올바르지 않은 이메일 형식입니다.</SignupErrorTypo>
            }
            <SignupInput 
                name='emailCode'
                type='text'
                placeholder='인증코드를 입력해 주세요.'
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
                    && <SignupPositiveTypo isDark={isDark}>이메일이 인증되었습니다.</SignupPositiveTypo>
            }
            <SignupInput label='비밀번호' name='password' type='password' 
                placeholder='비밀번호를 입력해 주세요.' 
                register={register} 
                required 
                maxLength={20} 
                minLength={9}
                pattern={/^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$/}
                isButton={false}/>
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
                }}
                isButton={false}/>
            { errors.passwordConfirm && errors.passwordConfirm.type==='required'
                && <SignupErrorTypo isDark={isDark}>비밀번호 확인을 입력해 주세요.</SignupErrorTypo> }
            { errors.passwordConfirm && errors.passwordConfirm.type==='validate'
                && <SignupErrorTypo isDark={isDark}>비밀번호와 비밀번호 확인이 일치하지 않습니다.</SignupErrorTypo> }
            <SignupInput 
                label='닉네임' name='nickname' type='text' 
                placeholder='닉네임을 입력해 주세요.' 
                register={register} required
                maxLength={20}
                isButton={false}/>
            { errors.nickname && errors.nickname.type==='required'
                && <SignupErrorTypo isDark={isDark}>닉네임을 입력해 주세요.</SignupErrorTypo> }
            {
                errors.nickname && errors.nickname.type==='maxLength'
                    && <SignupErrorTypo isDark={isDark}>20자 이하로 입력해 주세요.</SignupErrorTypo>
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
    );
};

export default SignupForm;