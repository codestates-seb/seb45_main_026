import React from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '../../redux/Store';
import { HeaderContainer } from './Header.style';
import HeaderLogo from './HeaderLogo';
import HeaderNav from './HeaderNav';
import HeaderProfile from './HeaderProfile';

const Header = () => {
    const isDark=useSelector((state:RootState)=>state.uiSetting.isDark);
    const isLogin = useSelector((state:RootState)=>state.loginInfo.isLogin);

    return (
        <HeaderContainer isDark={isDark}>
            <HeaderLogo/>
            { isLogin && <HeaderNav/>}
            { isLogin && <HeaderProfile/>}
        </HeaderContainer>
    );
};

export default Header;