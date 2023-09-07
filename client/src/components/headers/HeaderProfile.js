import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { HeaderProfileContainer, HeaderProfileImg, HeaderProfileImgContainer } from './HeaderProfile.style';
import { BodyTextTypo } from '../../atoms/typographys/Typographys';
import ProfileGray from '../../assets/images/icons/profile/profileGray.svg'
import { setIsSideBar } from '../../redux/createSlice/UISettingSlice';
import SideBar from '../sideBar/SideBar';

const HeaderProfile = () => {
    const dispatch = useDispatch();
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const isSideBar = useSelector(state=>state.uiSetting.isSideBar);
    const userInfo = useSelector(state=>state.loginInfo.loginInfo);
    
    const handleHeaderProfileClick = () => {
        dispatch(setIsSideBar(!isSideBar));
    }

    const handleHeaderProfileBlur = () => {
        dispatch(setIsSideBar(false));

    }

    return (
        <HeaderProfileContainer 
            onClick= {handleHeaderProfileClick} 
            onBlur={handleHeaderProfileBlur}>
                <BodyTextTypo isDark={isDark}>{userInfo.nickname}</BodyTextTypo>
                <HeaderProfileImgContainer>
                    <HeaderProfileImg 
                        src={userInfo.imgUrl?userInfo.imgUrl:ProfileGray}/>
                </HeaderProfileImgContainer>
                <SideBar/>
        </HeaderProfileContainer>
    );
};

export default HeaderProfile;