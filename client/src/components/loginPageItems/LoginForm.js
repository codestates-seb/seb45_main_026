import React, { useEffect, useState } from 'react';
import { FormProvider, useForm, useWatch } from "react-hook-form"
import { useDispatch, useSelector } from 'react-redux';
import { 
    LoginFormContainer,
    LoginButton,
} from './LoginForm.style';
import { loginService } from '../../services/authServices';
import { setToken, setIsLogin } from '../../redux/createSlice/LoginInfoSlice';
import { useNavigate } from 'react-router-dom';
import useConfirm from '../../hooks/useConfirm';
import { Input, InputErrorTypo } from '../../atoms/inputs/Inputs';
import tokens from '../../styles/tokens.json';
import { AlertModal } from '../../atoms/modal/Modal';

const globalTokens = tokens.global;

export const LoginForm = () => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const [ isLoginFailModalOpen, setIsLoginFailModalOpen ] = useState(false);

    const isDark = useSelector(state=>state.uiSetting.isDark);

    const method = useForm({
        mode: 'all'
    });
    const {
        register, 
        handleSubmit, 
        formState: { errors } } = method;

    // const [email,password] = useWatch({
    //     control, name: [ "email", "password" ]
    // });

    const onSubmit = async (data) => {
        const response = await loginService(data);
        if(response.status==='success') {
            //로그인에 성공하면 token을 state에 저장하고, 강의 목록 페이지로 이동한다.
            const authorization = response.authorization;
            const refresh = response.refresh;
            
            dispatch(setToken({
                authorization: authorization,
                refresh: refresh
            }));
            dispatch(setIsLogin(true));
            navigate('/lecture');
        } else {
            //로그인에 실패하면 로그인 실패 알림창을 띄운다.
            setIsLoginFailModalOpen(true);
        }
    };

    return (
        <>
            <AlertModal
                isModalOpen={isLoginFailModalOpen}
                setIsModalOpen={setIsLoginFailModalOpen}
                isBackdropClickClose={true}
                content='이메일, 비밀번호를 확인해 주세요!'
                buttonTitle='확인'
                handleButtonClick={()=>{ setIsLoginFailModalOpen(false) }}/>
            <FormProvider {...method}>
                <LoginFormContainer onSubmit={handleSubmit(onSubmit)}>
                    <Input
                        width='250px'
                        type='text'
                        name='email'
                        placeholder='이메일을 입력해 주세요.'
                        register={register}
                        required={true}
                        pattern={/^[^\s@]+@[^\s@]+\.[^\s@]+$/i}/>
                    { errors.email && errors.email.type==='required' &&
                            <InputErrorTypo isDark={isDark}>이메일을 입력해 주세요.</InputErrorTypo> }
                    { errors.email && errors.email.type==='pattern' &&
                            <InputErrorTypo isDark={isDark}>올바르지 않은 이메일 형식입니다.</InputErrorTypo> }
                    
                    <Input
                        marginTop={globalTokens.Spacing8.value}
                        width='250px'
                        type='password'
                        name='password'
                        placeholder='비밀번호를 입력해 주세요.'
                        register={register}
                        required={true}
                        maxLength={20}
                        minLength={9}
                        pattern={/^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$/} />
                    { errors.password && errors.password.type==='required' && 
                            <InputErrorTypo isDark={isDark}>비밀번호를 입력해 주세요.</InputErrorTypo> }
                    { errors.password && errors.password.type==='maxLength' && 
                            <InputErrorTypo isDark={isDark}>비밀번호는 20자 이하입니다.</InputErrorTypo> }
                    { errors.password && errors.password.type==='minLength' && 
                            <InputErrorTypo isDark={isDark}>비밀번호는 9자 이상입니다.</InputErrorTypo> }
                    { errors.password && errors.password.type==='pattern' && 
                            <InputErrorTypo isDark={isDark}>비밀번호는 영문자, 숫자, 특수기호를 포함합니다.</InputErrorTypo> }
                    <LoginButton isDark={isDark} type="submit">로그인</LoginButton>
                </LoginFormContainer>
            </FormProvider>
        </>
    );
};

export default LoginForm;