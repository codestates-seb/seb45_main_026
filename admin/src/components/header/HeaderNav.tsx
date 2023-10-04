import React from 'react';
import styled from 'styled-components';
import { TextButton } from '../../atoms/buttons/Buttons';
import { useSelector } from 'react-redux';
import { RootState } from '../../redux/Store';
import { useNavigate } from 'react-router-dom';

const HeaderNav = () => {
    const navigate = useNavigate();
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);

    return (
        <HeaderNavContainer>
            <HeaderNavTextButton 
                isDark={isDark}
                onClick={()=>{ navigate('/') }}>강의 관리</HeaderNavTextButton>
            <HeaderNavTextButton 
                isDark={isDark}
                onClick={()=>{ navigate('/members') }}>회원 관리</HeaderNavTextButton>
            <HeaderNavTextButton 
                isDark={isDark}
                onClick={()=>{ navigate('/reports/videos') }}>신고 내역 관리</HeaderNavTextButton>
            <HeaderNavTextButton 
                isDark={isDark}
                onClick={()=>{  }}>고객센터</HeaderNavTextButton>
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