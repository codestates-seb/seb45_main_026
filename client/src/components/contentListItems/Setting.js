import React from 'react';
import tokens from '../../styles/tokens.json';
import { Input, InputErrorTypo } from '../../atoms/inputs/Inputs';
import { useForm } from 'react-hook-form';
import { useDispatch, useSelector } from 'react-redux';
import { updateNicknameService, updatePasswordService } from '../../services/userInfoService';
import useConfirm from '../../hooks/useConfirm'
import { setLoginInfo } from '../../redux/createSlice/LoginInfoSlice';
import { NegativeTextButton, PositiveTextButton, } from '../../atoms/buttons/Buttons';
import { SettingContainer, SettingTitle, UserInfoContainer, ExtraButtonContainer  } from './Setting.style';

const globalTokens = tokens.global;

const Setting = () => {
    const dispatch = useDispatch();
    const loginUserInfo = useSelector(state=>state.loginInfo.loginInfo);
    const accessToken = useSelector(state=>state.loginInfo.accessToken);
    const isDark= useSelector(state=>state.uiSetting.isDark);
    const nicknameUpdateSuccessConfirm = useConfirm('닉네임이 변경되었습니다.');
    const nicknameUpdateFailConfirm = useConfirm('닉네임 변경 실패했습니다.');
    const passwordUpdateSuccessConfirm = useConfirm('비밀번가 변경되었습니다!');
    const passwordUpdateFailConfirm = useConfirm('비밀번호 변경 실패했습니다. 비밀번호를 다시 확인해주세요.');
    //내 이메일
    //닉네임, 비밀번호 변경
    //회원 탈퇴
    const {
        register,
        watch, 
        trigger,
        setValue,
        formState: { errors }, } = useForm({
            defaultValues: {
                email: loginUserInfo.email,
                nickname: loginUserInfo.nickname,
              }
        });

    //닉네임 변경 버튼 누르면 동작함
    const handleNicknameUpdateClick = async () => {
        const isValid = await trigger('nickname');
        const newNickname = watch('nickname');

        if( isValid ) {
            const response = await updateNicknameService(
                accessToken.authorization, newNickname);
            if(response.status==='success') {
                dispatch(setLoginInfo({
                    ...loginUserInfo,
                    nickname: newNickname,
                }))
                nicknameUpdateSuccessConfirm();
            } else {
                nicknameUpdateFailConfirm();
                return;
            }
        }
    }
    //비밀번호 변경 버튼 누르면 동작함
    const handlePasswordUpdateClick = async () => {
        const prePassword = watch('password');
        const newPassword = watch('newPassword');
        const isPasswordValid = await trigger('password');
        const isNewPasswordValid = await trigger('newPassword');

        if(isPasswordValid && isNewPasswordValid) {
            const response = await updatePasswordService(
                accessToken.authorization, 
                prePassword,
                newPassword );
            if(response.status==='success') {
                setValue('password','');
                setValue('newPassword','');
                passwordUpdateSuccessConfirm();
            } else {
                console.log(response.data);
                passwordUpdateFailConfirm();
            }
        }
    }
    //회원 탈퇴 버튼 누르면 동작함
    const handleDeleteUserClick = async () => {
        
    }
    
    return (
        <SettingContainer isDark={isDark}>
            <UserInfoContainer>
                <SettingTitle isDark={isDark}>내 정보</SettingTitle>
                <Input 
                    marginTop={globalTokens.Spacing8.value}
                    label='이메일'
                    width='50vw'
                    name='email'
                    type='text' 
                    disabled
                    register={register}/>
                <Input
                    marginTop={globalTokens.Spacing8.value}
                    label='닉네임'
                    width='50vw'
                    name='nickname'
                    type='text'
                    placeholder='닉네임을 입력해 주세요.'
                    register={register}
                    maxLength={20}
                    pattern={/^[A-Za-z0-9가-힣]+$/}
                    validateFunc={()=>{
                        return watch('nickname')!==loginUserInfo.nickname
                    }}
                    isButton={true}
                    required
                    buttonTitle='변경하기'
                    handleButtonClick={handleNicknameUpdateClick}/>
                { errors.nickname && errors.nickname.type==='required'
                && <InputErrorTypo isDark={isDark} width='50vw'>닉네임을 입력해 주세요.</InputErrorTypo> }
                { errors.nickname && errors.nickname.type==='maxLength'
                    && <InputErrorTypo isDark={isDark} width='50vw'>20자 이하로 입력해 주세요.</InputErrorTypo> }
                { errors.nickname && errors.nickname.type==='pattern'
                    && <InputErrorTypo isDark={isDark} width='50vw'>한글, 영문자, 숫자만 입력 가능합니다.</InputErrorTypo> }
                { errors.nickname && errors.nickname.type==='validate'
                    && <InputErrorTypo isDark={isDark} width='50vw'>기존에 사용하던 닉네임입니다.</InputErrorTypo> }
                <SettingTitle isDark={isDark}>비밀번호 변경하기</SettingTitle>
                <Input
                    marginTop={globalTokens.Spacing8.value}
                    label='기존 비밀번호'
                    type='password'
                    width='50vw'
                    name='password'
                    placeholder='기존 비밀번호를 입력해 주세요.'
                    register={register}
                    required={true}
                    maxLength={20}
                    minLength={9}
                    pattern={/^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$/}
                    isButton={false}/>
                { errors.password && errors.password.type==='required'
                    && <InputErrorTypo isDark={isDark} width='50vw'>기존 비밀번호를 입력해 주세요.</InputErrorTypo> }
                { errors.password && errors.password.type==='maxLength'
                    && <InputErrorTypo isDark={isDark} width='50vw'>20자 이내로 입력해 주세요.</InputErrorTypo> }
                { errors.password && errors.password.type==='minLength'
                    && <InputErrorTypo isDark={isDark} width='50vw'>9자 이상 입력해 주세요.</InputErrorTypo> }
                { errors.password && errors.password.type==='pattern'
                    && <InputErrorTypo isDark={isDark} width='50vw'>비밀번호는 영문자, 숫자, 특수문자를 모두 포함합니다.</InputErrorTypo> }
                <Input 
                    marginTop={globalTokens.Spacing8.value}
                    label='새로운 비밀번호'
                    type='password'
                    width='50vw'
                    name='newPassword'
                    placeholder='새로운 비밀번호를 입력해 주세요.'
                    register={register}
                    required={true}
                    maxLength={20}
                    minLength={9}
                    pattern={/^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$/}
                    validateFunc={()=> watch('password')!==watch('newPassword')}
                    isButton={true}
                    buttonTitle='변경하기'
                    handleButtonClick={handlePasswordUpdateClick}/>
                { errors.newPassword && errors.newPassword.type==='required'
                    && <InputErrorTypo isDark={isDark} width='50vw'>새로운 비밀번호를 입력해 주세요.</InputErrorTypo> }
                { errors.newPassword && errors.newPassword.type==='maxLength'
                    && <InputErrorTypo isDark={isDark} width='50vw'>20자 이내로 입력해 주세요.</InputErrorTypo> }
                { errors.newPassword && errors.newPassword.type==='minLength'
                    && <InputErrorTypo isDark={isDark} width='50vw'>9자 이상 입력해 주세요.</InputErrorTypo> }
                { errors.newPassword && errors.newPassword.type==='pattern'
                    && <InputErrorTypo isDark={isDark} width='50vw'>비밀번호는 영문자, 숫자, 특수문자를 모두 포함합니다.</InputErrorTypo> }
                { errors.newPassword && errors.newPassword.type==='validate'
                    && <InputErrorTypo isDark={isDark} width='50vw'>새로운 비밀번호와 기존 비밀번호가 동일합니다.</InputErrorTypo>}
            <ExtraButtonContainer>
                <PositiveTextButton isDark={isDark} type='button'>이용약관 보기</PositiveTextButton>
                <PositiveTextButton isDark={isDark} type='button'>개인정보 처리방침 보기</PositiveTextButton>
                <NegativeTextButton isDark={isDark} type='button' onClick={handleDeleteUserClick}>회원 탈퇴하기</NegativeTextButton>
            </ExtraButtonContainer>
            </UserInfoContainer>
        </SettingContainer>
    );
};

export default Setting;