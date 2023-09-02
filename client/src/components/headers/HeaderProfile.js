import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { HeaderProfileContainer, HeaderProfileImg } from './HeaderProfile.style';
import { BodyTextTypo } from '../../atoms/typographys/Typographys';
import ProfileGray from '../../assets/images/icons/profile/profileGray.svg'
import { setIsSideBar } from '../../redux/createSlice/UISettingSlice';

const HeaderProfile = () => {
    const dispatch = useDispatch();
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const isSideBar = useSelector(state=>state.uiSetting.isSideBar);
    const userInfo = useSelector(state=>state.loginInfo.loginInfo);
    
    const handleHeaderProfileClick = () => {
        dispatch(setIsSideBar(!isSideBar));
    }

    return (
        <HeaderProfileContainer onClick= {handleHeaderProfileClick}>
            <BodyTextTypo isDark={isDark}>{userInfo.nickname}</BodyTextTypo>
            <HeaderProfileImg src={ProfileGray}/>
        </HeaderProfileContainer>
    );
};

export default HeaderProfile;