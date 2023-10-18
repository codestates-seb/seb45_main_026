import React from "react";
import { styled } from "styled-components";
import {
  ReceiptAmountTypo,
  ReceiptItemContainer,
  ReceiptTitleTypo,
} from "../receiptPage/ReceiptItem.style";
import { useSelector } from "react-redux";

export const IncomeItemContainer = styled(ReceiptItemContainer)``;
export const IncomeIndexTypo = styled(ReceiptTitleTypo)`
  width: 70px;
  text-align: center;
`;
export const IncomeTitleTypo = styled(ReceiptTitleTypo)`
  text-align: center;
`;
export const IncomeAmountTypo = styled(ReceiptAmountTypo)`
  width: 180px;
`;

const IncomeItem = ({ item }) => {
  const isDark = useSelector((state) => state.uiSetting.isDark);

  return (
    <IncomeItemContainer isDark={isDark}>
      <IncomeIndexTypo isDark={isDark}>{item.videoId}</IncomeIndexTypo>
      <IncomeTitleTypo isDark={isDark}>{item.videoName}</IncomeTitleTypo>
      <IncomeAmountTypo isDark={isDark}>
        {item.totalSaleAmount.toLocaleString()}원
      </IncomeAmountTypo>
      <IncomeAmountTypo isDark={isDark}>
        {item.refundAmount.toLocaleString()}원
      </IncomeAmountTypo>
      <IncomeAmountTypo isDark={isDark}>
        {(item.totalSaleAmount - item.refundAmount).toLocaleString()}원
      </IncomeAmountTypo>
    </IncomeItemContainer>
  );
};

export default IncomeItem;
