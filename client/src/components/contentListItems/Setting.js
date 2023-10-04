import React, { useEffect, useState } from 'react';
import tokens from '../../styles/tokens.json';
import { Input, InputErrorTypo } from '../../atoms/inputs/Inputs';
import { useForm } from 'react-hook-form';
import { useDispatch, useSelector } from 'react-redux';
import { deleteUserInfoService, getAccountInfoService, getUserChannelInfoService, updateAccountInfoService, updateChannelInfoService, updateNicknameService, updatePasswordService } from '../../services/userInfoService';
import { setAccountInfo, setChannelInfo, setIsLogin, setLoginInfo, setMyid, setProvider, setToken } from '../../redux/createSlice/LoginInfoSlice';
import { NegativeTextButton, PositiveTextButton, } from '../../atoms/buttons/Buttons';
import { SettingContainer, SettingTitle, UserInfoContainer, ExtraButtonContainer  } from './Setting.style';
import { AlertModal, ConfirmModal } from '../../atoms/modal/Modal';
import { useNavigate } from 'react-router-dom';
import ImageInput from '../../atoms/inputs/ImageInput';
import { Textarea } from '../../atoms/inputs/TextAreas';
import { useLogout } from '../../hooks/useLogout';
import { useToken } from '../../hooks/useToken';

const globalTokens = tokens.global;

const Setting = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const refreshToken = useToken();
    const logout = useLogout();
    const loginUserInfo = useSelector(state=>state.loginInfo.loginInfo);
    const myid = useSelector(state=>state.loginInfo.myid);
    const channelInfo = useSelector(state=>state.loginInfo.channelInfo);
    const accountInfo = useSelector(state=>state.loginInfo.accountInfo);
    const accessToken = useSelector(state=>state.loginInfo.accessToken);
    const isDark= useSelector(state=>state.uiSetting.isDark);
    const [ isDeleteUserConfirmModalOpen, setIsDeleteUserConfirmModalOpen ] = useState(false);
    const [ isDeleteUserAlertModalOpen, setIsDeleteUserAlertModalOpen ] = useState(false);
    const [ is닉네임변경성공팝업, setIs닉네임변경성공팝업 ] = useState(false);
    const [ is닉네임변경실패팝업, setIs닉네임변경실패팝업 ] = useState(false);
    const [ is채널명변경성공팝업, setIs채널명변경성공팝업 ] = useState(false);
    const [ is채널명변경실패팝업, setIs채널명변경실패팝업 ] = useState(false);
    const [ is채널설명변경성공팝업, setIs채널설명변경성공팝업 ] = useState(false);
    const [ is채널설명변경실패팝업, setIs채널설명변경실패팝업 ] = useState(false);
    const [ is비밀번호변경성공팝업, setIs비밀번호변경성공팝업 ] = useState(false);
    const [ is비밀번호변경실패팝업, setIs비밀번호변경실패팝업 ] = useState(false);
    const [ is계좌정보변경성공팝업, setIs계좌정보변경성공팝업 ] = useState(false);
    const [ is계좌정보변경실패팝업, setIs계좌정보변경실패팝업 ] = useState(false);
    
    const {
        register,
        watch, 
        trigger,
        setValue,
        formState: { errors }, } = useForm({
            defaultValues: {
                email: loginUserInfo.email,
                nickname: loginUserInfo.nickname,
                channelName: channelInfo.channelName,
                channelDescription: channelInfo.description,
                accountHolder: accountInfo.accountHolder,
                bank: accountInfo.bank,
                accountNumber: accountInfo.account,
              }
    });

    useEffect(()=>{
        getUserChannelInfoService(accessToken.authorization, myid).then((response)=>{
            if(response.status==='success') {
                const newChannelName = response.data.channelName;
                const newDescription = response.data.description;
                dispatch(setChannelInfo({
                    channelName: newChannelName!==null?newChannelName:"",
                    description: newDescription!==null?newDescription:"",
                }));
                getAccountInfoService(accessToken.authorization).then((response)=>{
                    if(response.status==='success') {
                        dispatch(setAccountInfo({
                          accountHolder: response.data.data.name,
                          bank: response.data.data.bank,
                          account: response.data.data.account
                        }))
                    } else {
                        console.log(response.data);
                    }
                });
            } else if (response.data==='만료된 토큰입니다.') {
                console.log(response.data);
                //토큰 만료 에러인 경우 토큰 재발급 실행
                refreshToken();
            } else {
                logout();
                navigate('/');
            }
        })
    },[accessToken]);

  //닉네임 변경 버튼 누르면 동작함
  const handleNicknameUpdateClick = async () => {
    const isValid = await trigger("nickname");
    const newNickname = watch("nickname");

    if (isValid) {
      const response = await updateNicknameService(
        accessToken.authorization,
        newNickname
      );
      if (response.status === "success") {
        dispatch(
          setLoginInfo({
            ...loginUserInfo,
            nickname: newNickname,
          })
        );
        setIs닉네임변경성공팝업(true);
      } else {
        setIs닉네임변경실패팝업(true);
        return;
      }
    }
  };
  //채널명 변경 버튼을 누르면 동작함
  const handleChannelNameUpdateClick = async () => {
    const isValid = await trigger("channelName");
    const newChannelName = watch("channelName");

    if (isValid) {
      const response = await updateChannelInfoService(
        accessToken.authorization,
        myid,
        newChannelName,
        channelInfo.description
      );
      if (response.status === "success") {
        dispatch(
          setChannelInfo({
            ...channelInfo,
            channelName: newChannelName,
          })
        );
        setIs채널명변경성공팝업(true);
      } else {
        setIs채널명변경실패팝업(true);
      }
    }
  }
  //채널설명 변경 버튼을 누르면 동작함
  const handleChannelDescriptionUpdateClick = async () => {
    const isValid = await trigger("channelDescription");
    const newChannelDescription = watch("channelDescription");

    if (isValid) {
      const response = await updateChannelInfoService(
        accessToken.authorization,
        myid,
        channelInfo.channelName,
        newChannelDescription
      );
      if (response.status === "success") {
        dispatch(
          setChannelInfo({
            ...channelInfo,
            description: newChannelDescription,
          })
        );
        setIs채널설명변경성공팝업(true);
      } else {
        setIs채널설명변경실패팝업(true);
      }
    }
  };
  //계좌정보 변경 버튼 누르면 동작함
  const handleAccountNumberClick = async () => {
    const accountHolder = watch('accountHolder');
    const bank = watch('bank');
    const accountNumber = watch('accountNumber');
    const isAccountHolder = await trigger('accountHolder');
    const isBank = await trigger('bank');
    const isAccountNumber = await trigger('accountNumber');
    
    if ( isAccountHolder && isBank && isAccountNumber ) {
      const response = await updateAccountInfoService( 
          accessToken.authorization, 
          accountHolder, 
          bank, 
          accountNumber );
      if (response.status==='success') {
        setIs계좌정보변경성공팝업(true);
      } else {
        setIs계좌정보변경실패팝업(true);
      }
    }
  }
  //비밀번호 변경 버튼 누르면 동작함
  const handlePasswordUpdateClick = async () => {
    const prePassword = watch("password");
    const newPassword = watch("newPassword");
    const isPasswordValid = await trigger("password");
    const isNewPasswordValid = await trigger("newPassword");

    if (isPasswordValid && isNewPasswordValid) {
      const response = await updatePasswordService(
        accessToken.authorization,
        prePassword,
        newPassword
      );
      if (response.status === "success") {
        setValue("password", "");
        setValue("newPassword", "");
        setIs비밀번호변경성공팝업(true);
      } else {
        setIs비밀번호변경실패팝업(true);
      }
    }
  };
  //회원 탈퇴 버튼 누르면 동작함
  const handleDeleteUserClick = () => {
    //Confirm 모달을 연다.
    setIsDeleteUserConfirmModalOpen(true);
  };
  //Confirm 모달의 '예' 눌렀을 때 실행하는 메소드
  const handlePositiveButtonClick = async () => {
    //회원탈퇴 API를 실행한다.
    const response = await deleteUserInfoService(accessToken.authorization);
    if (response.status === "success") {
      dispatch(
        setLoginInfo({
          email: "",
          nickname: "",
          grade: "",
          imgUrl: "",
          reward: "",
        })
      );
      dispatch(
        setToken({
          authorization: "",
          refresh: "",
        })
      );
      dispatch(setProvider(""));
      dispatch(setMyid(""));
      dispatch(setIsLogin(false));
      setIsDeleteUserConfirmModalOpen(true);
    }
    setIsDeleteUserConfirmModalOpen(false);
    navigate("/");
  };
  //Confirm 모달의 '아니오' 눌렀을 때 실행되는 메소드
  const handleNegativeButtonClick = () => {
    //모달을 닫는다.
    setIsDeleteUserConfirmModalOpen(false);
  };
  //Alert 모달의 '홈으로 가기'를 눌렀을 때 실행되는 메소드
  const handleAlertButtonClick = () => {
    setIsDeleteUserAlertModalOpen(false);
    navigate("/");
  };

    return (
        <>
            {/* 탈퇴 여부 재확인 팝업 */}
            <ConfirmModal 
                isModalOpen={isDeleteUserConfirmModalOpen}
                setIsModalOpen={setIsDeleteUserConfirmModalOpen}
                isBackdropClickClose={true}
                content='정말로 탈퇴 하시겠습니까?'
                negativeButtonTitle='아니오'
                positiveButtonTitle='예'
                handleNegativeButtonClick={handleNegativeButtonClick}
                handlePositiveButtonClick={handlePositiveButtonClick}/>
            {/* 탈퇴 성공 팝업 */}
            <AlertModal 
                isModalOpen={isDeleteUserAlertModalOpen}
                setIsModalOpen={setIsDeleteUserAlertModalOpen}
                isBackdropClickClose={false}
                content='탈퇴되었습니다!'
                buttonTitle='홈으로 가기'
                handleButtonClick={handleAlertButtonClick}/>
            {/* 닉네임 변경 성공 팝업 */}
            <AlertModal 
                isModalOpen={is닉네임변경성공팝업}
                setIsModalOpen={setIs닉네임변경성공팝업}
                isBackdropClickClose={true}
                content='닉네임이 변경되었습니다!'
                buttonTitle='확인'
                handleButtonClick={()=>{ setIs닉네임변경성공팝업(false) }}/>
            {/* 닉네임 변경 실패 팝업 */}
            <AlertModal 
                isModalOpen={is닉네임변경실패팝업}
                setIsModalOpen={setIs닉네임변경실패팝업}
                isBackdropClickClose={true}
                content='기존에 사용하던 닉네임이거나, 사용할 수 없는 닉네임입니다.'
                buttonTitle='확인'
                handleButtonClick={()=>{ setIs닉네임변경실패팝업(false) }}/>
            {/* 채널명 변경 성공 팝업 */}
            <AlertModal
                isModalOpen={is채널명변경성공팝업}
                setIsModalOpen={setIs채널명변경성공팝업}
                isBackdropClickClose={true}
                content='채널명이 변경되었습니다!'
                buttonTitle='확인'
                handleButtonClick={()=>{ setIs채널명변경성공팝업(false) }}/>
            {/* 채널명 변경 실패 팝업 */}
            <AlertModal
                isModalOpen={is채널명변경실패팝업}
                setIsModalOpen={setIs채널명변경실패팝업}
                isBackdropClickClose={true}
                content='채널 이름은 한글, 영문, 숫자만 가능합니다.'
                buttonTitle='확인'
                handleButtonClick={()=>{ setIs채널명변경실패팝업(false) }}/>
            {/* 채널 설명 변경 성공 팝업 */}
            <AlertModal
                isModalOpen={is채널설명변경성공팝업}
                setIsModalOpen={setIs채널설명변경성공팝업}
                isBackdropClickClose={true}
                content='채널 설명이 변경되었습니다!'
                buttonTitle='확인'
                handleButtonClick={()=>{ setIs채널설명변경성공팝업(false) }}/>
            {/* 채널 설명 변경 실패 팝업 */}
            <AlertModal 
                isModalOpen={is채널설명변경실패팝업}
                setIsModalOpen={setIs채널설명변경실패팝업}
                isBackdropClickClose={true}
                content='채널 설명 변경에 실패했습니다.'
                buttonTitle='확인'
                handleButtonClick={()=>{ setIs채널설명변경성공팝업(false) }}/>
            {/* 비밀번호 변경 성공 팝업 */}
            <AlertModal 
                isModalOpen={is비밀번호변경성공팝업}
                setIsModalOpen={setIs비밀번호변경성공팝업}
                isBackdropClickClose={true}
                content='비밀번호가 변경되었습니다!'
                buttonTitle='확인'
                handleButtonClick={()=>{ setIs비밀번호변경성공팝업(false) }}/>
            { /* 비밀번호 변경 실패 팝업 */ }
            <AlertModal 
                isModalOpen={is비밀번호변경실패팝업}
                setIsModalOpen={setIs비밀번호변경실패팝업}
                isBackdropClickClose={true}
                content='비밀번호 변경 실패했습니다.'
                buttonTitle='확인'
                handleButtonClick={()=>{ setIs비밀번호변경실패팝업(false) }}/>
            {/* 계좌정보 변경 성공 팝업 */}
            <AlertModal 
                isModalOpen={is계좌정보변경성공팝업}
                setIsModalOpen={setIs계좌정보변경성공팝업}
                isBackdropClickClose={true}
                content='계좌 정보가 변경되었습니다!'
                buttonTitle='확인'
                handleButtonClick={ ()=>{ setIs계좌정보변경성공팝업(false); }}/>
            {/* 계좌정보 변경 실패 팝업 */}
            <AlertModal 
                isModalOpen={is계좌정보변경실패팝업}
                setIsModalOpen={setIs계좌정보변경실패팝업}
                isBackdropClickClose={true}
                content='계좌 정보 변경 실패했습니다!'
                buttonTitle='확인'
                handleButtonClick={ ()=>{ setIs계좌정보변경실패팝업(false); }}/>
            <SettingContainer isDark={isDark}>
                <UserInfoContainer>
                    <SettingTitle isDark={isDark}>내 정보</SettingTitle>
                    <ImageInput
                        marginTop={globalTokens.Spacing8.value} 
                        label='프로필 이미지'/>
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
                    <SettingTitle isDark={isDark}>채널 정보</SettingTitle>
                    <Input
                        marginTop={globalTokens.Spacing8.value}
                        label='채널명'
                        type='text'
                        width='50vw'
                        name='channelName'
                        placeholder='채널명을 입력해 주세요.'
                        register={register}
                        required={true}
                        maxLength={20}
                        isButton={true}
                        buttonTitle='변경하기'
                        handleButtonClick={handleChannelNameUpdateClick}/>
                    <Textarea
                        marginTop={globalTokens.Spacing8.value}
                        label='채널 설명'
                        width='50vw'
                        name='channelDescription'
                        placeholder='채널 설명을 입력해 주세요.'
                        register={register}
                        required={true}
                        maxLength={200}
                        isButton={true}
                        buttonTitle='변경하기'
                        handleButtonClick={handleChannelDescriptionUpdateClick}/>
                    {
                        errors.channelDescription && errors.channelDescription.type==='required'
                            && <InputErrorTypo isDark={isDark} width='50vw'>채널 설명을 입력해 주세요.</InputErrorTypo>
                    }
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
                <SettingTitle isDark={isDark}>내 계좌 정보</SettingTitle>
                    <Input
                        marginTop={globalTokens.Spacing8.value}
                        label='예금주명'
                        width='50vw'
                        name='accountHolder'
                        type='text'
                        placeholder='예금주명을 입력해 주세요.'
                        register={register}
                        maxLength={20}
                        pattern={/^[A-Za-z가-힣]+$/}
                        validateFunc={()=>{
                            return true;
                        }}
                        isButton={false}
                        required />
                    { errors.accountHolder && errors.accountHolder.type==='required'
                        && <InputErrorTypo isDark={isDark} width='50vw'>예금주명을 입력해 주세요.</InputErrorTypo> }
                    <Input
                        marginTop={globalTokens.Spacing8.value}
                        label='은행'
                        width='50vw'
                        name='bank'
                        type='text'
                        placeholder='은행을 입력해 주세요.'
                        register={register}
                        maxLength={20}
                        pattern={/^[A-Za-z가-힣]+$/}
                        validateFunc={()=>{
                            return true;
                        }}
                        isButton={false}
                        required />
                    { errors.bank && errors.bank.type==='required'
                        && <InputErrorTypo isDark={isDark} width='50vw'>은행을 입력해 주세요.</InputErrorTypo> }
                    { errors.bank && (errors.bank.type==='maxLength' || errors.bank.type==='pattern' || errors.bank.type==='validate' )
                        && <InputErrorTypo isDark={isDark} width='50vw'>올바르지 않은 은행 정보입니다.</InputErrorTypo> }
                    <Input
                        marginTop={globalTokens.Spacing8.value}
                        label='계좌번호'
                        width='50vw'
                        name='accountNumber'
                        type='text'
                        placeholder='계좌번호를 입력해 주세요.'
                        register={register}
                        maxLength={30}
                        pattern={/^(\d{1,})(-(\d{1,})){1,}/}
                        validateFunc={()=>{
                            return true;
                        }}
                        isButton={true}
                        required
                        buttonTitle='변경하기'
                        handleButtonClick={handleAccountNumberClick}/>
                    { errors.accountNumber && errors.accountNumber.type==='required'
                        && <InputErrorTypo isDark={isDark} width='50vw'>계좌번호를 입력해 주세요.</InputErrorTypo> }
                    { errors.accountNumber && errors.accountNumber.type==='pattern'
                        && <InputErrorTypo isDark={isDark} width='50vw'>올바르지 않은 계좌번호 형식입니다.</InputErrorTypo> }
                <ExtraButtonContainer>
                    <a href="https://field-hellebore-58d.notion.site/c50fadcd93e04c4fb19580c0f99ca773?pvs=4">
                        <PositiveTextButton isDark={isDark} type='button'>이용약관 보기</PositiveTextButton>
                    </a>
                    <a href="https://field-hellebore-58d.notion.site/8263a881b70445d6a8defaef30648745?pvs=4">
                        <PositiveTextButton isDark={isDark} type='button'>개인정보 처리방침 보기</PositiveTextButton>
                    </a>
                    <NegativeTextButton isDark={isDark} type='button' onClick={handleDeleteUserClick}>회원 탈퇴하기</NegativeTextButton>
                </ExtraButtonContainer>
                </UserInfoContainer>
            </SettingContainer>
        </>
        );
    };


export default Setting;
