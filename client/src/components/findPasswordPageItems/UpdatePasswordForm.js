import React from 'react';
import tokens from '../../styles/tokens.json';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import useConfirm from '../../hooks/useConfirm';
import { useForm } from 'react-hook-form';
import { updatePasswordService } from '../../services/authServices';
import { setFindPasswordEmail } from '../../redux/createSlice/LoginInfoSlice';
import { styled } from 'styled-components';
import { Input, InputErrorTypo } from '../../atoms/inputs/Inputs';
import { LoginButton } from '../loginPageItems/LoginForm.style';

const globalTokens = tokens.global;

export const UpdatePasswordFormContainer = styled.form`
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
`

const UpdatePasswordForm = () => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const email = useSelector(state=>state.loginInfo.findPasswordEmail);
    const updatePasswordCompleteConfirm = useConfirm(
        '비밀번호가 성공적으로 변경되었습니다!',
        ()=>{ navigate('/login') },
        ()=>{}
    );
    const updatePasswordFailConfirm = useConfirm('비밀번호 변경에 실패했습니다.');
    const { 
        register,
        handleSubmit,
        watch,
        formState: { errors }, 
    } = useForm();

    const onSubmit = async () => {
        const newPassword = watch('newPassword');

        const response = await updatePasswordService(email, newPassword);
        if(response.status==='success') {
            dispatch(setFindPasswordEmail(''));
            updatePasswordCompleteConfirm();
        } else {
            updatePasswordFailConfirm();
        }
    }

    return (
        <UpdatePasswordFormContainer onSubmit={handleSubmit(onSubmit)}>
            <Input
                label='새로운 비밀번호'
                isDark={isDark}
                width='250px'
                type='password'
                name='newPassword'
                placeholder='비밀번호를 입력해 주세요.'
                register={register}
                required
                maxLength={20}
                minLength={9}
                pattern={/^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$/}/>
            { errors.newPassword && errors.newPassword.type==='required' && 
                <InputErrorTypo isDark={isDark}>비밀번호를 입력해 주세요.</InputErrorTypo> }
            { errors.newPassword && errors.newPassword.type==='maxLength' && 
                <InputErrorTypo isDark={isDark}>비밀번호는 20자 이하입니다.</InputErrorTypo> }
            { errors.newPassword && errors.newPassword.type==='minLength' && 
                <InputErrorTypo isDark={isDark}>비밀번호는 9자 이상입니다.</InputErrorTypo> }
            { errors.newPassword && errors.newPassword.type==='pattern' && 
                <InputErrorTypo isDark={isDark}>비밀번호는 영문자, 숫자, 특수기호를 포함합니다.</InputErrorTypo> }
            <Input
                marginTop={globalTokens.Spacing8.value}
                label='새로운 비밀번호 확인'
                isDark={isDark}
                width='250px'
                type='password'
                name='newPasswordConfirm'
                placeholder='비밀번호 확인을 입력해 주세요.'
                register={register}
                required={true}
                validateFunc={()=>{
                    return watch('newPassword')===watch('newPasswordConfirm');
                }}/>
            { errors.newPasswordConfirm && errors.newPasswordConfirm.type==='required' && 
                <InputErrorTypo isDark={isDark}>비밀번호 확인을 입력해 주세요.</InputErrorTypo>} 
            <LoginButton isDark={isDark} type='submit'>비밀번호 변경하기</LoginButton>
        </UpdatePasswordFormContainer>
    );
};

export default UpdatePasswordForm;