import React from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '../../redux/Store';
import { HeaderContainer } from './Header.style';
import HeaderLogo from './HeaderLogo';

const Header = () => {
    const isDark=useSelector((state:RootState)=>state.uiSetting.isDark);

    return (
        <HeaderContainer isDark={isDark}>
            <HeaderLogo/>
        </HeaderContainer>
    );
};

export default Header;