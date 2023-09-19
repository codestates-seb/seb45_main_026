import React from 'react';
import tokens from '../../styles/tokens.json';
import { styled } from 'styled-components';
import { IncomeAmountTypo, IncomeItemContainer, IncomeTitleTypo } from './IncomeItem';
import { useSelector } from 'react-redux';

const globalTokens = tokens.global;

export const IncomeHeadContainer = styled(IncomeItemContainer)`
    background-color: ${props=>props.isDark?globalTokens.Black.value:globalTokens.Background.value};
    border: none;
`
export const IncomeTitleHeadContainer = styled(IncomeTitleTypo)`
    text-align: center;
    color: ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
    font-weight: 400;
`
export const IncomeAmountHeadTypo = styled(IncomeAmountTypo)`
    text-align: center;
    color: ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
`

const IncomeHeader = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <IncomeHeadContainer isDark={isDark}>
            <IncomeTitleHeadContainer isDark={isDark}>강의명</IncomeTitleHeadContainer>
            <IncomeAmountHeadTypo isDark={isDark}>누적 판매 금액</IncomeAmountHeadTypo>
            <IncomeAmountHeadTypo isDark={isDark}>누적 환불 금액</IncomeAmountHeadTypo>
            <IncomeAmountHeadTypo isDark={isDark}>실제 이윤</IncomeAmountHeadTypo>
        </IncomeHeadContainer>
    );
};

export default IncomeHeader;