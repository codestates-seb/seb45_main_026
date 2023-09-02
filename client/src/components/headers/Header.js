import React from 'react';
import { useSelector } from 'react-redux';
import HeaderLogo from './HeaderLogo';
import { HeaderContainer, HeaderLoginButton, MainPageHeaderContainer } from './Header.style';
import { useNavigate } from 'react-router-dom';
import HeaderProfile from './HeaderProfile';
import SideBar from '../sideBar/SideBar';

export const MainPageHeader = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const isLogin = useSelector(state=>state.loginInfo.isLogin);
    const navigate = useNavigate();
    
    const handleLoginButtonClick = () => {
        navigate('/login');
    }
    return (
        <MainPageHeaderContainer isDark={isDark}>
            <HeaderLogo/>
            {
                !isLogin
                    ?<HeaderLoginButton isDark={isDark} onClick={handleLoginButtonClick}>로그인</HeaderLoginButton>
                    :<HeaderProfile/>
            }
        </MainPageHeaderContainer>
    );
}

export const Header = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const isLogin = useSelector(state=>state.loginInfo.isLogin);
    const navigate = useNavigate();

    const handleLoginButtonClick = () => {
        navigate('/login');
    }

    return (
        <HeaderContainer isDark={isDark}>
            <HeaderLogo/>
            {
                !isLogin
                    ?<HeaderLoginButton isDark={isDark} onClick={handleLoginButtonClick}>로그인</HeaderLoginButton>
                    :<HeaderProfile/>
            }
            <SideBar/>
        </HeaderContainer>
    );
};

export default Header;