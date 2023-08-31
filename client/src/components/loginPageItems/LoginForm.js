import React from 'react';
import { useForm } from "react-hook-form"
import { useDispatch, useSelector } from 'react-redux';
import { 
    LoginFormContainer,
    LoginFormInputContainer,
    LoginFormInput,
    ErrorTextTypo,
    LoginButton,
} from './LoginForm.style';
import { loginService } from '../../services/authServices';

export const LoginForm = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const {
        register, 
        handleSubmit,
        formState: { errors },
    } = useForm();

    const onSubmit = async (data) => {
        const response = await loginService(data);
        if(response.isLogin) {
            console.log(response)
            const authorization = response.authorization;
            const refresh = response.refresh;
        }
    };

    return (
        <LoginFormContainer onSubmit={handleSubmit(onSubmit)}>
            <LoginFormInputContainer isDark={isDark}>
                <LoginFormInput 
                    isDark={isDark} 
                    type='text' 
                    placeholder='이메일을 입력해 주세요.' 
                    {...register("email", { 
                        required: true, 
                        maxLength: 20,
                        pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/i,
                    })} />
                {
                    errors.email && errors.email.type==='required' &&
                        <ErrorTextTypo isDark={isDark}>이메일을 입력해 주세요.</ErrorTextTypo> }
                { errors.email && errors.email.type==='pattern' &&
                        <ErrorTextTypo isDark={isDark}>올바르지 않은 이메일 형식입니다.</ErrorTextTypo> }
            </LoginFormInputContainer>
            <LoginFormInputContainer>
                <LoginFormInput 
                    isDark={isDark} 
                    type='password' 
                    placeholder='비밀번호를 입력해 주세요.' 
                    {...register("password", { required: true })} />
                { errors.password && errors.password.type==='required' && 
                        <ErrorTextTypo isDark={isDark}>비밀번호를 입력해 주세요.</ErrorTextTypo> }
            </LoginFormInputContainer>
            
            <LoginButton isDark={isDark} type="submit">로그인</LoginButton>
        </LoginFormContainer>
    );
};

export default LoginForm;