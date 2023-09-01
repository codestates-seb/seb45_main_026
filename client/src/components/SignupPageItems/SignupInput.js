import React from 'react';
import { useSelector } from 'react-redux';
import { SignupEmailConfirmButton, SignupFormInput, SignupFormInputContainer, SignupFormLabel, SignupWithButtonInputContainer } from './SignupForm.style';
import { RegularInput } from '../../atoms/inputs/Inputs';
import { BodyTextTypo } from '../../atoms/typographys/Typographys';

export const SignupInput = ({
    label, name, type, placeholder, 
    register, required, maxLength, minLength, pattern, validateFunc,
    isButton, buttonTitle, handleButtonClick }) => {
        const isDark = useSelector(state=>state.uiSetting.isDark);

        return (
            <SignupFormInputContainer isDark={isDark}>
                { label && <BodyTextTypo isDark={isDark}>{label}</BodyTextTypo> }
                { isButton? 
                    <SignupWithButtonInputContainer isDark={isDark}>
                        <RegularInput
                            isDark={isDark} 
                            width={ isButton ? '200px' : '300px' }
                            type={type}
                            placeholder={placeholder}
                            {...register(name, { 
                                required: required,
                                maxLength: maxLength,
                                minLength: minLength,                                    pattern: pattern,
                                validate: validateFunc })}
                            />
                        { isButton &&
                            <SignupEmailConfirmButton
                            isDark={isDark}
                            onClick={handleButtonClick}>
                                {buttonTitle}
                            </SignupEmailConfirmButton> }
                    </SignupWithButtonInputContainer>
                : 
                    <RegularInput
                        isDark={isDark}
                        width={ isButton ? '200px' : '300px' }
                        type={type}
                        placeholder={placeholder}
                        {...register(name, { 
                            required: required,
                            maxLength: maxLength,
                            minLength: minLength,
                            pattern: pattern,
                            validate: validateFunc })}/>
                }
            </SignupFormInputContainer> )}

export default SignupInput;
