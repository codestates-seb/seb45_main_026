import React, { useState } from 'react';
import { DropdownButton, DropdownButtonIcon, DropdownTextTypo, DropdownWrapper, ReceiptDropdownContainer } from '../receiptPage/ReceiptDropdown.style';
import { useSelector } from 'react-redux';
import { styled } from 'styled-components';
import { TextButton } from '../../atoms/buttons/Buttons';
import { BodyTextTypo } from '../../atoms/typographys/Typographys';
import arrowUp from '../../assets/images/icons/arrow/subscribe_arrow_up.svg';
import arrowDown from '../../assets/images/icons/arrow/subscribe_arrow_down.svg';
import tokens from '../../styles/tokens.json';

const globalTokens = tokens.global;

const IncomeDropdownWrapper = styled(DropdownWrapper)`
    display: flex;
    flex-direction: row;
    justify-content: start;
    align-items: center;
`
const IncomeDropdownButton = styled(DropdownButton)`
    width: 100px;
`
const IncomeDropdownUnitTypo = styled(BodyTextTypo)`
    margin: ${globalTokens.Spacing4.value}px ${globalTokens.Spacing8.value}px ${globalTokens.Spacing4.value}px ${globalTokens.Spacing4.value}px;
`

const IncomeCategory = ({year, setYear, month, setMonth}) => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const [isYearDropdownOpen, setIsYearDropdownOpen] = useState(false);
    const [isMonthDropdownOpen, setIsMonthDropdownOpen] = useState(false);

    return (
        <ReceiptDropdownContainer>
            <IncomeDropdownWrapper>
                <IncomeDropdownButton isDark={isDark}>
                    <DropdownTextTypo isDark={isDark}>{year}</DropdownTextTypo>
                    <DropdownButtonIcon src={isYearDropdownOpen?arrowUp:arrowDown}/>
                </IncomeDropdownButton>
                <IncomeDropdownUnitTypo isDark={isDark}>년</IncomeDropdownUnitTypo>
                <IncomeDropdownButton isDark={isDark}>
                <DropdownTextTypo isDark={isDark}>{month}</DropdownTextTypo>
                    <DropdownButtonIcon src={isMonthDropdownOpen?arrowUp:arrowDown}/>
                </IncomeDropdownButton>
                <IncomeDropdownUnitTypo isDark={isDark}>월</IncomeDropdownUnitTypo>
            </IncomeDropdownWrapper>
        </ReceiptDropdownContainer>
    );
};

export default IncomeCategory;