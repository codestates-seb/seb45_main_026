import React from 'react';
import { styled } from 'styled-components';
import { useSelector } from 'react-redux';
import { BodyTextTypo } from '../../atoms/typographys/Typographys'
import profileGray from '../../assets/images/icons/profile/profileGray.svg'
import help from '../../assets/images/icons/sideBar/help.svg';
import helpWhite from '../../assets/images/icons/sideBar/helpWhite.svg';
import logout from '../../assets/images/icons/sideBar/logout.svg';
import logoutWhite from '../../assets/images/icons/sideBar/logoutWhite.svg'
import purchase from '../../assets/images/icons/sideBar/purchase.svg';
import purchaseWhite from '../../assets/images/icons/sideBar/purchaseWhite.svg';
import subscribe from '../../assets/images/icons/sideBar/subscribe.svg';
import subscribeWhite from '../../assets/images/icons/sideBar/subscribeWhite.svg';
import basket from '../../assets/images/icons/sideBar/basket.svg';
import basketWhite from '../../assets/images/icons/sideBar/basketWhite.svg';
import write from '../../assets/images/icons/sideBar/write.svg';
import writeWhite from '../../assets/images/icons/sideBar/writeWhite.svg';
import tokens from '../../styles/tokens.json'
import watchWhite from '../../assets/images/icons/sideBar/watchWhite.svg';
import watch from '../../assets/images/icons/sideBar/watch.svg';
import money from '../../assets/images/icons/sideBar/money.svg';
import moneyWhite from '../../assets/images/icons/sideBar/moneyWhite.svg';

import { useNavigate } from 'react-router-dom';
import { useLogout } from '../../hooks/useLogout';
import Toggle from '../../atoms/buttons/Toggle';

const globalTokens = tokens.global;

export const SideBarContainer = styled.aside`
    position: fixed;
    top: 60px;
    bottom: 0;
    right: ${(props)=>props.isSideBar? 0 : -251 }px;
    z-index: 999;
    width: 250px;
    background-color: ${(props)=>props.isDark?globalTokens.Black.value:globalTokens.Header.value};
    box-shadow: 
        ${(props)=>props.isDark?-1:-4}px 
        ${(props)=>props.isDark?1:10}px 
        ${(props)=>props.isDark ? globalTokens.RegularWhiteShadow.value.blur : globalTokens.RegularShadow.value.blur}px
        ${(props)=>props.isDark ? globalTokens.RegularWhiteShadow.value.spread : globalTokens.RegularShadow.value.spread}px
        ${(props)=>props.isDark ? globalTokens.RegularWhiteShadow.value.color : globalTokens.RegularShadow.value.color };
    display: flex;
    flex-direction: column;
    justify-content: start;
    align-items: center;
    transition: 600ms;
`
export const SideBarButtonContainer = styled.div`
    width: 100%;
    height: 45px;
    padding: ${globalTokens.Spacing4.value}px ${globalTokens.Spacing16.value}px;
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    transition: 300ms;
    &:hover {
        opacity: 0.3;
    }
`
export const SideBarButtonIcon = styled.img`
    width: 25px;
`
export const SideBarButtonTitle = styled(BodyTextTypo)`
    flex-grow: 1;
    text-align: center;
`
export const DivisionLine = styled.div`
    width: 100%;
    height: 1px;
    background-color: ${ (props)=>props.isDark?globalTokens.LightGray.value:globalTokens.DarkGray.value };
    margin: ${globalTokens.Spacing8.value}px 0px;
`

export const SideBar = () => {
    const logoutHook = useLogout();
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const isSideBar = useSelector(state=>state.uiSetting.isSideBar);
    const userId = useSelector(state=>state.loginInfo.myid);
    const navigate = useNavigate();

    const handleMyChannelClick = () => {
        navigate(`/channels/${userId}`);
    }
    const handleActivityClick = () => {
        navigate('/activity/reward');
    }
    const handleWatchClick = () => {
        navigate(`/watched`);
    }
    const handleLogoutClick = () => {
        logoutHook();
        navigate('/');
    }
    const handleHelpClick = () => {
         window.location.href = 'mailto:helim01033@naver.com';
    }
    const handlePurchaseClick = () => {
        navigate('/purchased');
    }
    const handleBasketClick = () => {
        navigate('/carts');
    }
    const handleSubscribeClick = () => {
        navigate('/channellist')
    }
    const handleWriteClick = () => {
        navigate('/upload/course');
    }

    return (
        <SideBarContainer isDark={isDark} isSideBar={isSideBar}>
            <SideBarButtonContainer onClick={handleMyChannelClick}>
                <SideBarButtonIcon src={profileGray}/>
                <SideBarButtonTitle isDark={isDark}>내 채널</SideBarButtonTitle>
            </SideBarButtonContainer>
            <SideBarButtonContainer onClick={handleActivityClick}>
                <SideBarButtonIcon src={isDark?moneyWhite:money}/>
                <SideBarButtonTitle isDark={isDark}>나의 활동</SideBarButtonTitle>
            </SideBarButtonContainer>
            <SideBarButtonContainer onClick={handleWatchClick}>
                <SideBarButtonIcon src={isDark?watchWhite:watch}/>
                <SideBarButtonTitle isDark={isDark}>시청 기록</SideBarButtonTitle>
            </SideBarButtonContainer>
            <SideBarButtonContainer onClick={handleHelpClick}>
                <SideBarButtonIcon src={isDark?helpWhite:help}/>
                <SideBarButtonTitle isDark={isDark}>문의하기</SideBarButtonTitle>
            </SideBarButtonContainer>
            <SideBarButtonContainer onClick={handleLogoutClick}>
                <SideBarButtonIcon src={isDark?logoutWhite:logout}/>
                <SideBarButtonTitle isDark={isDark}>로그아웃</SideBarButtonTitle>
            </SideBarButtonContainer>
            <DivisionLine isDark={isDark}/>
            <SideBarButtonContainer onClick={handlePurchaseClick}>
                <SideBarButtonIcon src={isDark?purchaseWhite:purchase}/>
                <SideBarButtonTitle isDark={isDark}>구매한 강의</SideBarButtonTitle>
            </SideBarButtonContainer>
            <SideBarButtonContainer onClick={handleBasketClick}>
                <SideBarButtonIcon src={isDark?basketWhite:basket}/>
                <SideBarButtonTitle isDark={isDark}>장바구니</SideBarButtonTitle>
            </SideBarButtonContainer>
            <SideBarButtonContainer onClick={handleSubscribeClick}>
                <SideBarButtonIcon src={isDark?subscribeWhite:subscribe}/>
                <SideBarButtonTitle isDark={isDark}>구독</SideBarButtonTitle>
            </SideBarButtonContainer>
            <DivisionLine isDark={isDark}/>
            <SideBarButtonContainer onClick={handleWriteClick}>
                <SideBarButtonIcon src={isDark?writeWhite:write}/>
                <SideBarButtonTitle isDark={isDark}>강의 올리기</SideBarButtonTitle>
            </SideBarButtonContainer>
        </SideBarContainer>
    );
};

export default SideBar;