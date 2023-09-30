import React from "react";
import { styled } from "styled-components";
import { useSelector } from "react-redux";
import tokens from "../../styles/tokens.json";
import {
  ReceiptGrayTypo,
  ReceiptItemContainer,
  ReceiptTitleTypo,
  ReceiptAmountTypo,
  ReceiptStatusTypo,
} from "./ReceiptItem.style";

const globalTokens = tokens.global;

export const ReceiptItemHeadContainer = styled(ReceiptItemContainer)`
  background-color: ${(props) =>
    props.isDark ? globalTokens.Black.value : globalTokens.Background.value};
  border: none;
`;
export const ReceiptDateHeadTypo = styled(ReceiptGrayTypo)`
  margin: 0px 10px;
  text-align: center;
`;
export const ReceiptTitleHeadTypo = styled(ReceiptTitleTypo)`
  text-align: center;
  color: ${(props) =>
    props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
  font-weight: 400;
`;
export const ReceiptAmountHeadTypo = styled(ReceiptAmountTypo)`
  padding: 0;
  color: ${(props) =>
    props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
`;
export const ReceiptStatusHeadTypo = styled(ReceiptStatusTypo)``;

const ReceiptListHeader = () => {
  const isDark = useSelector((state) => state.uiSetting.isDark);

  return (
    <ReceiptItemHeadContainer isDark={isDark}>
      <ReceiptDateHeadTypo isDark={isDark}>결제일시</ReceiptDateHeadTypo>
      <ReceiptTitleHeadTypo isDark={isDark}>상품명</ReceiptTitleHeadTypo>
      <ReceiptAmountHeadTypo isDark={isDark}>결제금액</ReceiptAmountHeadTypo>
      <ReceiptStatusHeadTypo isDark={isDark}>결제상태</ReceiptStatusHeadTypo>
      <div style={{ width: "49px" }} />
    </ReceiptItemHeadContainer>
  );
};

export default ReceiptListHeader;
