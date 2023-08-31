import React from 'react';
import { styled } from 'styled-components';
import { TextButton } from '../../atoms/buttons/Buttons';
import { useSelector } from 'react-redux';
import tokens from '../../styles/tokens.json'
import { BodyTextTypo } from '../../atoms/typographys/Typographys';
import { useNavigate } from 'react-router-dom';

const globalTokens = tokens.global;

export const ExtraButtonContainer = styled.div`
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
`
export const ExtraTextButton = styled(TextButton)`
    font-size: ${globalTokens.BodyText.value}px;
`
export const ExtraButton = () => {
    const navigate = useNavigate();
    const isDark = useSelector(state=>state.uiSetting.isDark);

    const handleSignupButtonClick = () => {
        navigate('/signup');
    }

    return (
        <ExtraButtonContainer>
            <ExtraTextButton isDark={isDark} onClick={handleSignupButtonClick}>회원가입</ExtraTextButton>
            <BodyTextTypo isDark={isDark}>|</BodyTextTypo>
            <ExtraTextButton isDark={isDark}>비밀번호 찾기</ExtraTextButton>
        </ExtraButtonContainer>
    );
};