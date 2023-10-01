import React from "react";
import { styled } from "styled-components";
import { useSelector } from "react-redux";
import {
  ReceiptGrayTypo,
  ReceiptItemContainer,
  ReceiptTitleTypo,
  ReceiptAmountTypo,
} from "../receiptPage/ReceiptItem.style";

export const RewardItemContainer = styled(ReceiptItemContainer)``;
export const RewardGrayTypo = styled(ReceiptGrayTypo)`
  width: 100%;
  max-width: 250px;
  margin-left: 50px;
  text-align: center;
`;
export const RewardTitleTypo = styled(ReceiptTitleTypo)`
  width: 100%;
  max-width: 250px;
  text-align: center;
`;
export const RewardPointTypo = styled(ReceiptAmountTypo)`
  width: 100%;
  max-width: 150px;
  text-align: center;
`;

const RewardItem = ({ item }) => {
  const isDark = useSelector((state) => state.uiSetting.isDark);
  let createDate = item.createdDate;
  let createDay = createDate.split("T")[0];
  let createTime = createDate.split("T")[1];

  return (
    <RewardItemContainer isDark={isDark}>
      <RewardGrayTypo isDark={isDark}>
        {`${createDay} ${createTime}`}
      </RewardGrayTypo>
      <RewardTitleTypo isDark={isDark}>
        {item.rewardType === "VIDEO"
          ? "강의 구매"
          : item.rewardType === "QUIZ"
          ? "문제 풀기"
          : item.rewardType === "REPLY"
          ? "리뷰 등록"
          : "기타"}
      </RewardTitleTypo>
      <RewardPointTypo isDark={isDark}>
        {item.rewardPoint > 0 && "+" + item.rewardPoint}P
      </RewardPointTypo>
    </RewardItemContainer>
  );
};

export default RewardItem;
