import React from 'react';
import { styled } from 'styled-components';
import { ReceiptGrayTypo, ReceiptItemContainer, ReceiptTitleTypo } from '../receiptPage/ReceiptItem.style';
import { useSelector } from 'react-redux';

export const ReportItemContainer = styled(ReceiptItemContainer)``

export const ReportTitleTypo = styled(ReceiptTitleTypo)``

export const ReportCountTypo = styled(ReceiptGrayTypo)`
    width: 120px;
    text-align: center;
`
export const ReportedDateTypo = styled(ReceiptGrayTypo)`
`
export const ReportItem = ({item}) => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const reportedDate = item.lastReportedDate;
    const reportedDay = reportedDate.split('T')[0];
    const reportedTime = reportedDate.split('T')[1];
    return (
        <ReportItemContainer>
            <ReportCountTypo isDark={isDark}>{item.videoId}</ReportCountTypo>
            <ReportTitleTypo isDark={isDark}>{ item.videoName }</ReportTitleTypo>
            <ReportCountTypo isDark={isDark}>{ item.reportCount }</ReportCountTypo>
            <ReportedDateTypo isDark={isDark}>{ `${reportedDay} ${reportedTime}` }</ReportedDateTypo>
        </ReportItemContainer>
    );
};

export default ReportItem;