import React from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '../../redux/Store';
import styled from 'styled-components';
import tokens from '../../styles/tokens.json';
import { RoundButton } from '../../atoms/buttons/Buttons';
import { BodyTextTypo } from '../../atoms/typographys/Typographys';
import { useLogout } from '../../hooks/useLogout';

const globalTokens = tokens.global;

const HeaderProfile = () => {
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);
    const isLogin = useSelector((state:RootState)=>state.loginInfo.isLogin);
    const loginInfo = useSelector((state:RootState)=>state.loginInfo.loginInfo);
    const logout = useLogout();

    return (
        <HeaderProfileContainer>
        { !isLogin && <RoundButton isDark={isDark}>로그인</RoundButton> }
        { isLogin && 
            <>
                <BodyTextTypo isDark={isDark}>{`${loginInfo.nickname}님`}</BodyTextTypo>
                <RoundButton 
                    isDark={isDark}
                    onClick={logout}>로그아웃</RoundButton>
            </> }
        </HeaderProfileContainer>
    );
};

const HeaderProfileContainer = styled.section`
    margin: 0 ${globalTokens.Spacing12.value}px;
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: ${globalTokens.Spacing4.value}px;
`

export default HeaderProfile;