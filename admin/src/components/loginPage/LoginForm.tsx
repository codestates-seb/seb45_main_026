import React from 'react';
import { FormContainer, LoginContainer } from './FormContainer.style';
import { useSelector } from 'react-redux';
import { RootState } from '../../redux/Store';
import { RegularInput } from '../../atoms/inputs/Input.style';
import { FormProvider, SubmitHandler, useForm } from 'react-hook-form';

export type inputType = {

}

const LoginForm = () => {
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);
    
    const method = useForm({
        mode : 'all'
    });

    const {
        register,
        handleSubmit, 
        formState: {errors}
    } = method;

    const onSubmit: SubmitHandler<inputType> = (data) => {

    }

    return (
        <LoginContainer isDark={isDark}>
            <FormProvider {...method}>
                <FormContainer onSubmit={onSubmit}>
                    <RegularInput 
                        isDark={isDark}
                        width='250px'
                        type='text'
                        required={true}
                        placeholder='이메일을 입력해 주세요.'
                        {...register('email',{
                                required: true,
                                maxLength: 50,
                                minLength: 6,
                                pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/i})}/>
                </FormContainer>
            </FormProvider>
        </LoginContainer>
    );
};

export default LoginForm;