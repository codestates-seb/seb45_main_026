import React from 'react';
import { styled } from 'styled-components';
import { ReceiptAmountTypo, ReceiptGrayTypo, ReceiptItemContainer, ReceiptTitleTypo } from '../receiptPage/ReceiptItem.style';
import { useSelector } from 'react-redux';

export const IncomeItemContainer = styled(ReceiptItemContainer)`
`
export const IncomeGrayTypo = styled(ReceiptGrayTypo)`
`
export const IncomeTitleTypo = styled(ReceiptTitleTypo)`
`
export const IncomeAmountTypo = styled(ReceiptAmountTypo)`
    width: 130px;
`

const IncomeItem = ({item}) => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <IncomeItemContainer isDark={isDark}>
            <IncomeTitleTypo isDark={isDark}>{item.videoName}</IncomeTitleTypo>
            <IncomeAmountTypo isDark={isDark}>{item.totalSaleAmount}원</IncomeAmountTypo>
            <IncomeAmountTypo isDark={isDark}>{item.refundAmount}원</IncomeAmountTypo>
            <IncomeAmountTypo isDark={isDark}>{item.totalSaleAmount-item.refundAmount}원</IncomeAmountTypo>
        </IncomeItemContainer>
    );
};

export default IncomeItem;