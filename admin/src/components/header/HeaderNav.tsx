import React from 'react';
import styled from 'styled-components';
import { TextButton } from '../../atoms/buttons/Buttons';
import { useSelector } from 'react-redux';
import { RootState } from '../../redux/Store';

const HeaderNav = () => {
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);

    return (
        <HeaderNavContainer>
            <HeaderNavTextButton isDark={isDark}>강의 관리</HeaderNavTextButton>
            <HeaderNavTextButton isDark={isDark}>회원 관리</HeaderNavTextButton>
            <HeaderNavTextButton isDark={isDark}>신고 내역 관리</HeaderNavTextButton>
            <HeaderNavTextButton isDark={isDark}>고객센터</HeaderNavTextButton>
        </HeaderNavContainer>
    );
};

export const HeaderNavContainer = styled.nav`
    flex-grow: 1;
    display: flex;
    flex-direction: row;
    justify-content: space-evenly;
    align-items: center;
`
export const HeaderNavTextButton = styled(TextButton)`
    
`

export default HeaderNav;