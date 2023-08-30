import React, { useEffect } from 'react';
import { SNSLoginButtonForGitHub, SNSLoginButtonForGoogle, SNSLoginButtonForKakao, SNSLoginButtonIcon, SNSLoginButtonText, SNSLoginButtonTextWhite } from './SNSLogin.style';
import { useSelector } from 'react-redux';
import googleIcon from '../../assets/images/icons/snsLogin/google.svg'
import githubIcon from '../../assets/images/icons/snsLogin/github.svg'
import kakaoIcon from '../../assets/images/icons/snsLogin/kakao.svg';
import { oauthLoginService } from '../../services/authServices';

export const GoogleLoginButton = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    

    const handleGoogleButonClick = () => {
        window.location.assign(
            'https://accounts.google.com/o/oauth2/v2/auth?client_id=577159361441-tdcsm80fn4rodt1m16r05qtb48cakrkp.apps.googleusercontent.com&redirect_uri=http://localhost:3000/login&response_type=code&scope=email'
        );
    }
    
    useEffect(()=>{
        const url = new URL(window.location.href);
        const authorizationCode = url.searchParams.get('code');
        
        if(authorizationCode) {
            oauthLoginService(authorizationCode).then((res)=>{
                
            })
        }
    },[]);

    return (
        <SNSLoginButtonForGoogle isDark={isDark} onClick={handleGoogleButonClick}>
            <SNSLoginButtonIcon src={googleIcon}/>
            <SNSLoginButtonText>Sign in with Google</SNSLoginButtonText>
        </SNSLoginButtonForGoogle>
    );
};

export const GitHubLoginButton = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <SNSLoginButtonForGitHub isDark={isDark}>
        <SNSLoginButtonIcon src={githubIcon}/>
        <SNSLoginButtonTextWhite>Sign in with GitHub</SNSLoginButtonTextWhite>
        </SNSLoginButtonForGitHub>
    );
};


export const KaKaoLoginButton = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <SNSLoginButtonForKakao isDark={isDark}>
        <SNSLoginButtonIcon src={kakaoIcon}/>
        <SNSLoginButtonText>Sign in with Kakao</SNSLoginButtonText>
        </SNSLoginButtonForKakao>
    );
};




