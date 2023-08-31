import React from 'react';
import { useSelector } from 'react-redux';
import { SignupFormInput, SignupFormInputContainer, SignupFormLabel } from './SignupForm.style';

export const SignupInput = ({
    label, name, type, placeholder, 
    register, required, maxLength, minLength, validateFunc }) => {
        const isDark = useSelector(state=>state.uiSetting.isDark);

        return ( 
            <SignupFormInputContainer isDark={isDark}>
                { label && <SignupFormLabel isDark={isDark}>{label}</SignupFormLabel> }
                <SignupFormInput
                        isDark={isDark}
                        isButton={false} 
                        type={type}
                        placeholder={placeholder}
                        {...register(name, { 
                            required: required,
                            maxLength: maxLength,
                            minLength: minLength,
                            validate: validateFunc })}/>
            </SignupFormInputContainer> )}

export default SignupInput;
