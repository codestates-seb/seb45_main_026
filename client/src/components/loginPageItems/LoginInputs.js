import React from 'react';
import { useSelector } from 'react-redux';
import { RegularInput } from '../../atoms/inputs/Inputs';

const LoginInputs = ({
    width,
    type, name, placeholder, 
    register,required, maxLength, minLength, pattern,
}) => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <RegularInput
            isDark={isDark}
            width={width}
            type={type}
            placeholder={placeholder}
            { ...register(name,{
                required,
                maxLength,
                minLength,
                pattern,
            }) } />
    );
};

export default LoginInputs;