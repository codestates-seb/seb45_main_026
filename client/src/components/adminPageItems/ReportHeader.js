import React from 'react';
import { ReportCountTypo, ReportItemContainer, ReportStatusTypo, ReportTitleTypo, ReportedDateTypo } from './ReportItem';
import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json';
import { useSelector } from 'react-redux';

const globalTokens = tokens.global;

const ReportHeaderContainer = styled(ReportItemContainer)`
    background-color: ${props=>props.isDark?globalTokens.Black.value:globalTokens.Background.value};
    cursor: default;
    &:hover {
        background-color: ${props=>props.isDark?globalTokens.Black.value:globalTokens.Background.value};
    }
`
const ReportTitleHeader = styled(ReportTitleTypo)`
    text-align: center;
    font-weight: 400;
    color: ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
`
const ReportCountHeader = styled(ReportCountTypo)`
    text-align: center;
`
const ReportStatusHeader = styled(ReportStatusTypo)``
const ReportDateHeader = styled(ReportedDateTypo)`
    text-align: center;
`

const ReportHeader = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <ReportHeaderContainer isDark={isDark}>
            <ReportCountHeader isDark={isDark}>VideoId</ReportCountHeader>
            <ReportTitleHeader isDark={isDark}>강의명</ReportTitleHeader>
            <ReportCountHeader isDark={isDark}>누적 신고 수</ReportCountHeader>
            <ReportDateHeader isDark={isDark}>신고일시</ReportDateHeader>
            <ReportStatusHeader isDark={isDark}>폐쇄여부</ReportStatusHeader>
        </ReportHeaderContainer>
    );
};

export default ReportHeader;