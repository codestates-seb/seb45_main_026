import React from 'react';
import { useForm } from "react-hook-form"
import { useSelector } from 'react-redux';
import { 
    LoginFormContainer,
    LoginFormInputContainer,
    LoginFormInput,
    ErrorTextTypo,
    LoginButton,
    SNSLoginButtonForGoogle,
    SNSLoginButtonIcon,
    SNSLoginButtonText,
    SNSLoginButtonForGitHub
} from './LoginForm.style';

export const LoginForm = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const { 
        register, 
        handleSubmit, 
        watch, 
        formState: { errors },
    } = useForm();

    const onSubmit = (data) => console.log(data);

    return (
        <LoginFormContainer onSubmit={handleSubmit(onSubmit)}>
            {/* register your input into the hook by invoking the "register" function */}
            <LoginFormInputContainer isDark={isDark}>
                <LoginFormInput isDark={isDark} type='text' placeholder='아이디를 입력해 주세요.' {...register("id", { required: true })} />
                { errors.id && 
                        <ErrorTextTypo isDark={isDark}>아이디를 입력해 주세요.</ErrorTextTypo> }
            </LoginFormInputContainer>
            <LoginFormInputContainer>
                {/* include validation with required or other standard HTML validation rules */}
                <LoginFormInput isDark={isDark} type='password' placeholder='비밀번호를 입력해 주세요.' {...register("password", { required: true })} />
                {/* errors will return when field validation fails  */}
                { errors.password && 
                        <ErrorTextTypo isDark={isDark}>비밀번호를 입력해 주세요.</ErrorTextTypo> }
            </LoginFormInputContainer>
            <LoginButton isDark={isDark} type="submit">로그인</LoginButton>
        </LoginFormContainer>
    );
};

export default LoginForm;