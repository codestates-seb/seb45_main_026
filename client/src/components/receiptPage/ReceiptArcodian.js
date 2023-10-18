import React from "react";
import { useSelector } from "react-redux";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import ReceiptArcodianItem, {
  ReceiptArcodianHeader,
} from "./ReceiptArcodianItem";

const globalTokens = tokens.global;

const ReceiptArcodianContainer = styled.div`
  display: flex;
  flex-direction: column;
  border: 1px solid
    ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
  margin: ${globalTokens.Spacing8.value}px;
  padding: ${globalTokens.Spacing20.value}px;
  width: 85%;
  border-radius: ${globalTokens.RegularRadius.value}px;
`;

const ReceiptArcodian = ({ orderId, videos }) => {
  const isDark = useSelector((state) => state.uiSetting.isDark);

  return (
    <ReceiptArcodianContainer isDark={isDark}>
      <ReceiptArcodianHeader />
      {videos.map((e) => (
        <ReceiptArcodianItem item={e} orderId={orderId} />
      ))}
    </ReceiptArcodianContainer>
  );
};

export default ReceiptArcodian;
