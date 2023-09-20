import React from 'react';
import { styled } from 'styled-components';
import { ReportAuthorTypo, ReportContentTypo, ReportDetailItemContainer } from './ReportDetailItem';
import tokens from '../../styles/tokens.json';
import { useSelector } from 'react-redux';

const globalTokens = tokens.global;

const ReportDetailHeaderContainer = styled(ReportDetailItemContainer)`
    background-color: ${props=>props.isDark?globalTokens.Black.value:globalTokens.Background.value};
    text-align: center;
    margin-top: ${globalTokens.Spacing12.value}px;
`
const ReportContentHeader = styled(ReportContentTypo)`
    color: ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
`
const ReportAuthorHeader = styled(ReportAuthorTypo)`
    color: ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
`

const ReportDetailHeader = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <ReportDetailHeaderContainer isDark={isDark}>
            <ReportContentHeader isDark={isDark}>신고내용</ReportContentHeader>
            <ReportAuthorHeader isDark={isDark}>작성자</ReportAuthorHeader>
        </ReportDetailHeaderContainer>
    );
};

export default ReportDetailHeader;