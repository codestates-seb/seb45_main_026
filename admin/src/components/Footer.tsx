import React from 'react';
import tokens from '../styles/tokens.json';
import styled from 'styled-components';
import { SmallTextTypo } from '../atoms/typographys/Typographys';
import { useSelector } from 'react-redux';
import { RootState } from '../redux/Store';
import logo from '../assets/images/logos/logo.png';
import lightLogo from '../assets/images/logos/lightLogo.png';

const globalTokens = tokens.global;

const Footer = () => {
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);

    return (
        <FooterContainer>
             <FooterLogo src={isDark ? lightLogo : logo}/>
             <FooterInfo isDark={isDark}>(주)오펜하이머    |   사업자번호 : 809-27-01650</FooterInfo>
                <FooterInfo isDark={isDark}>@Oppenheimer All Right Reserve.</FooterInfo>
        </FooterContainer>
    );
};

export const FooterContainer = styled.footer`
    min-height: 150px;
    height: 15vh;
    padding: ${globalTokens.Spacing20.value}px;
    background-color: ${globalTokens.DarkGray.value};
    transition: 300ms;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
`
export const FooterLogo = styled.img`
    width: 50px;
    transition: 300ms;
`
export const FooterInfo = styled(SmallTextTypo)`
    padding: ${globalTokens.Spacing4.value}px 0px 0px 0px;
    color: ${globalTokens.White.value};
    white-space: pre;
`

export default Footer;