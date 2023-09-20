import React from 'react';
import { styled } from 'styled-components';
import { ReceiptItemContainer } from '../receiptPage/ReceiptItem.style';
import { useSelector } from 'react-redux';
import { BodyTextTypo } from '../../atoms/typographys/Typographys';

/*
createdDate, email, memberId, nickname, reportContent, reportId
*/

export const ReportDetailItemContainer = styled(ReceiptItemContainer)`
    padding-left: 10px;
`
export const ReportContentTypo = styled(BodyTextTypo)`
    width: 50%;
`
export const ReportAuthorTypo = styled(BodyTextTypo)`
    width: 25%;
    text-align: center;
`

const ReportDetailItem = ({item}) => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <ReportDetailItemContainer isDark={isDark}>
            <ReportContentTypo isDark={isDark}>{item.reportContent}</ReportContentTypo>
            <ReportAuthorTypo isDark={isDark}>{item.nickname}</ReportAuthorTypo>
        </ReportDetailItemContainer>
    );
};

export default ReportDetailItem;