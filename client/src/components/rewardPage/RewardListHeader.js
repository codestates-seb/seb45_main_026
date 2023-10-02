import React from "react";
import {
  ReceiptAmountHeadTypo,
  ReceiptDateHeadTypo,
  ReceiptItemHeadContainer,
  ReceiptTitleHeadTypo,
} from "../receiptPage/ReceiptListHeader";
import { styled } from "styled-components";
import { useSelector } from "react-redux";
import tokens from "../../styles/tokens.json";

const globalTokens = tokens.global;

export const RewardItemHeadContainer = styled(ReceiptItemHeadContainer)`
  margin-top: ${globalTokens.Spacing8.value}px;
`;
export const RewardDateHeadTypo = styled(ReceiptDateHeadTypo)`
  width: 100%;
  max-width: 250px;
  margin-left: 50px;
`;
export const RewardTitleHeadTypo = styled(ReceiptTitleHeadTypo)`
  width: 100%;
  max-width: 250px;
`;
export const RewardPointHeadTypo = styled(ReceiptAmountHeadTypo)`
  width: 100%;
  max-width: 150px;
`;
const RewardListHeader = () => {
  const isDark = useSelector((state) => state.uiSetting.isDark);

  return (
    <RewardItemHeadContainer isDark={isDark}>
      <RewardDateHeadTypo isDark={isDark}>적립일시</RewardDateHeadTypo>
      <RewardTitleHeadTypo isDark={isDark}>활동명</RewardTitleHeadTypo>
      <RewardPointHeadTypo isDark={isDark}>적립 포인트 </RewardPointHeadTypo>
    </RewardItemHeadContainer>
  );
};

export default RewardListHeader;
