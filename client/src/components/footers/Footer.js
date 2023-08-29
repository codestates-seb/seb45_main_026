import React from 'react';
import { styled } from 'styled-components';
import { useSelector } from 'react-redux';
import tokens from '../../styles/tokens.json';
import { SmallTextTypo } from '../../atoms/typographys/Typographys';
import lightLogo from '../../assets/images/logos/lightLogo.png'; 
import logo from '../../assets/images/logos/logo.png'; 

const globalTokens = tokens.global;

export const FooterContainer = styled.footer`
    min-height: 10vh;
    padding: ${globalTokens.Spacing20.value}px;
    background-color: ${(props)=>props.isDark?globalTokens.Black.value:globalTokens.DarkGray.value};
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

const Footer = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <FooterContainer isDark={isDark}>
            <FooterLogo src={isDark ? lightLogo : logo}/>
            <FooterInfo isDark={isDark}>(주)오펜하이머    |   사업자번호 : 809-27-01650</FooterInfo>
            <FooterInfo isDark={isDark}>@Oppenheimer All Right Reserve.</FooterInfo>
        </FooterContainer>
    );
};

export default Footer;