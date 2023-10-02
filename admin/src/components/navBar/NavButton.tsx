import React from 'react';
import styled from 'styled-components';
import { TextButton } from '../../atoms/buttons/Buttons';
import { useSelector } from 'react-redux';
import { RootState } from '../../redux/Store';
import tokens from '../../styles/tokens.json';
import { useNavigate } from 'react-router-dom';

const globalTokens = tokens.global;

export const NavButtonContainer = styled.div<{isSelected:boolean, isDark:boolean}>`
    box-shadow: 0 -2px 0 ${
        props=>props.isDark && props.isSelected? globalTokens.LightGray.value
        : props.isDark && !props.isSelected? globalTokens.Gray.value 
        : !props.isDark && props.isSelected? globalTokens.Gray.value
        : globalTokens.LightGray.value} 
        inset; 
`
export const NavTextButton = styled(TextButton)<{isSelected:boolean}>`
    font-weight: ${props=>props.isSelected?globalTokens.Bold.value:400};
`

type navButtonPropsType = {
    text: string;
    isSelected: boolean;
}

const NavButton = ({ text, isSelected } : navButtonPropsType) => {
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);

    const handleNavButtonClick = () => {
        switch(text) {
            default: break;
        }
    }

    return (
        <NavButtonContainer isDark={isDark} isSelected={isSelected}>
            <NavTextButton 
                isDark={isDark} 
                isSelected={isSelected}
                onClick={handleNavButtonClick}>{text}</NavTextButton>
        </NavButtonContainer>
    );
};


export default NavButton;