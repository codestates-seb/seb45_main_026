import React from "react";
import { styled } from "styled-components";
import { NavyItem } from "../contentListItems/ChannelNav";
import tokens from "../../styles/tokens.json";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";

const globalTokens = tokens.global;

export const RewardNavContainer = styled.nav`
  width: 100%;
  margin-bottom: ${globalTokens.Spacing16.value}px;
  margin: ${globalTokens.Spacing24.value}px 0 ${globalTokens.Spacing36.value}px
    0;
  padding: 0 ${globalTokens.Spacing28.value}px;
  display: flex;
  flex-direction: row;
  align-items: end;
`;
export const RewardNavItem = styled(NavyItem)`
  padding: ${globalTokens.Spacing4.value}px ${globalTokens.Spacing24.value}px;
  font-weight: ${(props) => (props.isSelect ? globalTokens.Bold.value : 400)};
  border-bottom: 3px solid
    ${(props) =>
      props.isSelect && props.isDark
        ? globalTokens.LightGray.value
        : props.isSelect && !props.isDark
        ? globalTokens.Gray.value
        : !props.isSelect && props.isDark
        ? globalTokens.Gray.value
        : globalTokens.LightGray.value};
  width: fit-content;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
`;
export const BorderBox = styled.div`
  width: 100px;
  height: 3px;
  background-color: ${(props) =>
    props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
`;

const RewardCategory = ({ category }) => {
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const navigate = useNavigate();

  return (
    <RewardNavContainer isDark={isDark}>
      <RewardNavItem
        isDark={isDark}
        isSelect={category === "receipt" ? true : false}
        onClick={() => {
          navigate("/activity/receipt");
        }}
      >
        결제 내역
      </RewardNavItem>
      <RewardNavItem
        isDark={isDark}
        isSelect={category === "reward" ? true : false}
        onClick={() => {
          navigate("/activity/reward");
        }}
      >
        포인트 적립내역
      </RewardNavItem>
      <RewardNavItem
        isDark={isDark}
        isSelect={category === "income" ? true : false}
        onClick={() => {
          navigate("/activity/income");
        }}
      >
        정산 내역
      </RewardNavItem>
      <RewardNavItem
        isDark={isDark}
        isSelect={category === "account" ? true : false}
        onClick={() => {
          navigate("/activity/account");
        }}
      >
        설정
      </RewardNavItem>
      {/* <BorderBox isDark={isDark} /> */}
    </RewardNavContainer>
  );
};

export default RewardCategory;
