import React from 'react';
import { useSelector } from 'react-redux';
import { HeaderProfileContainer, HeaderProfileImg } from './HeaderProfile.style';
import { BodyTextTypo } from '../../atoms/typographys/Typographys';
import ProfileGray from '../../assets/images/icons/profile/profileGray.svg'

const HeaderProfile = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const userNickname = useSelector(state=>state.loginInfo.loginInfo).nickname;

    return (
        <HeaderProfileContainer>
            <BodyTextTypo isDark={isDark}>{userNickname}</BodyTextTypo>
            <HeaderProfileImg src={ProfileGray}/>
        </HeaderProfileContainer>
    );
};

export default HeaderProfile;