import React from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '../../redux/Store';
import { HeaderContainer } from './Header.style';

const Header = () => {
    const isDark=useSelector((state:RootState)=>state.uiSetting.isDark);

    return (
        <HeaderContainer isDark={isDark}>
            This is Header.
        </HeaderContainer>
    );
};

export default Header;