import React, { useState } from 'react';
import { styled } from 'styled-components';
import { ReceiptGrayTypo, ReceiptItemContainer, ReceiptTitleTypo } from '../receiptPage/ReceiptItem.style';
import { useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';

export const ReportItemContainer = styled(ReceiptItemContainer)`
    cursor: pointer;
    transition: 300ms;
    
    &:hover {
        background-color: ${props=>props.isDark?'rgba(255,255,255,0.15)':'rgba(0,0,0,0.15)'};
    }
`
export const ReportTitleTypo = styled(ReceiptTitleTypo)`
`
export const ReportCountTypo = styled(ReceiptGrayTypo)`
    width: 120px;
    text-align: center;
`
export const ReportStatusTypo = styled(ReceiptGrayTypo)`
    width: 150px;
    text-align: center;
`
export const ReportedDateTypo = styled(ReceiptGrayTypo)`

`
export const ReportItem = ({item}) => {
    const navigate = useNavigate();
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const reportedDate = item.lastReportedDate;
    const reportedDay = reportedDate.split('T')[0];
    const reportedTime = reportedDate.split('T')[1];

    const handleReportItemClick = (e) => {
        navigate(`/admin/${item.videoId}?videoName=${item.videoName}?status=${item.videoStatus}`);
    }

    return (
        <ReportItemContainer onClick={handleReportItemClick}>
            <ReportCountTypo isDark={isDark}>{item.videoId}</ReportCountTypo>
            <ReportTitleTypo isDark={isDark}>{ item.videoName }</ReportTitleTypo>
            <ReportCountTypo isDark={isDark}>{ item.reportCount }</ReportCountTypo>
            <ReportedDateTypo isDark={isDark}>{ `${reportedDay} ${reportedTime}` }</ReportedDateTypo>
            <ReportStatusTypo isDark={isDark}>
            { 
                item.videoStatus==='CLOSED'? '폐쇄됨'
                : item.videoStatus==='ADMIN_CLOSED'? '관리자에 의해 폐쇄됨'
                : '폐쇄되지 않음'  
            }
            </ReportStatusTypo>
        </ReportItemContainer>
    );
};

export default ReportItem;