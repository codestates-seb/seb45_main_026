import React, { useEffect, useState } from 'react';
import { SNSLoginButtonForGitHub, SNSLoginButtonForGoogle, SNSLoginButtonForKakao, SNSLoginButtonIcon, SNSLoginButtonText, SNSLoginButtonTextWhite } from './SNSLogin.style';
import { useDispatch, useSelector } from 'react-redux';
import googleIcon from '../../assets/images/icons/snsLogin/google.svg'
import githubIcon from '../../assets/images/icons/snsLogin/github.svg'
import kakaoIcon from '../../assets/images/icons/snsLogin/kakao.svg';
import { oauthLoginService } from '../../services/authServices';
import { setProvider, setToken } from '../../redux/createSlice/LoginInfoSlice';
import { useNavigate } from 'react-router-dom';
import useConfirm from '../../hooks/useConfirm';

export const SNSLogin = () => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const provider = useSelector(state=>state.loginInfo.oAuthProvider);
    const loginFailConfirm = useConfirm('로그인 실패했습니다.');
    
    const handleGoogleButonClick = () => {
        dispatch(setProvider('google'));
        window.location.assign(
            'https://accounts.google.com/o/oauth2/v2/auth?client_id=577159361441-tdcsm80fn4rodt1m16r05qtb48cakrkp.apps.googleusercontent.com&redirect_uri=https://www.itprometheus.net/login&response_type=code&scope=email'
        );
    }

    const handleGithubButtonClick = () => {
        dispatch(setProvider('github'));
        window.location.assign(
            'https://github.com/login/oauth/authorize?client_id=9b59d1e4333c5a338c6f&redirect_uri=https://www.itprometheus.net/login&scope=user:email,read:user'
        );
    }

    const handleKakaoButtonClick = () => {
        dispatch(setProvider('kakao'));
        window.location.assign(
            'https://kauth.kakao.com/oauth/authorize?client_id=655f82c50a175820dccd0357df745c73&scope=profile_nickname,account_email&response_type=code&redirect_uri=https://www.itprometheus.net/login'
        );
    }

    useEffect(()=>{
        const url = new URL(window.location.href);
        const authorizationCode = url.searchParams.get('code');
                
        if(authorizationCode) {
            console.log(`provider: ${provider}`);
            console.log(`authorizationCode: ${authorizationCode}`);
            oauthLoginService(provider,authorizationCode).then((response)=>{
                if(response.status==='success') {
                    const authorization = response.authorization;
                    const refresh = response.refresh;
                    
                    dispatch(setToken({
                        authorization: authorization,
                        refresh: refresh
                    }));
                    navigate('/lecture');
                } else {
                    loginFailConfirm();
                }
            })
        }
    },[]);

    return (
        <>
            <SNSLoginButtonForGoogle isDark={isDark} onClick={handleGoogleButonClick}>
                <SNSLoginButtonIcon src={googleIcon}/>
                <SNSLoginButtonText>Sign in with Google</SNSLoginButtonText>
            </SNSLoginButtonForGoogle>
            <SNSLoginButtonForGitHub isDark={isDark} onClick={handleGithubButtonClick}>
                <SNSLoginButtonIcon src={githubIcon}/>
                <SNSLoginButtonTextWhite>Sign in with GitHub</SNSLoginButtonTextWhite>
            </SNSLoginButtonForGitHub>
            <SNSLoginButtonForKakao isDark={isDark} onClick={handleKakaoButtonClick}>
                <SNSLoginButtonIcon src={kakaoIcon}/>
                <SNSLoginButtonText>Sign in with Kakao</SNSLoginButtonText>
            </SNSLoginButtonForKakao>
        </>
    )
}