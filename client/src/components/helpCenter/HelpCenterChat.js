import React from 'react';
import styled from 'styled-components';
import tokens from '../../styles/tokens.json';
import { useSelector } from 'react-redux';
import { useQuery } from '@tanstack/react-query';
import axios from 'axios';
import { ROOT_URL } from '../../services';

const globalTokens = tokens.global;

const HelpCenterChat = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const accessToken = useSelector(state=>state.loginInfo.accessToken);

    return (
        <HelpCenterChatContainer isDark={isDark}>
            
        </HelpCenterChatContainer>
    );
};

export const HelpCenterChatContainer = styled.section`
    border-radius: ${globalTokens.RegularRadius.value}px;
    border: 1px solid ${props=>props.isDark?globalTokens.Gray.value : globalTokens.LightGray.value};
    width: 95%;
    max-height: 80vh;
    min-height: 10vh;
    margin: ${globalTokens.Spacing4.value}px 0 ${globalTokens.Spacing12.value}px 0;
`

export default HelpCenterChat;