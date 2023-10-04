import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import { BodyTextTypo } from "../../atoms/typographys/Typographys";
import { NegativeTextButton } from "../../atoms/buttons/Buttons";

const globalTokens = tokens.global;

export const ReceiptArcodianHeaderContainer = styled.div`
  padding: ${globalTokens.Spacing4.value}px ${globalTokens.Spacing8.value}px;
  background-color: ${(props) =>
    props.isDark ? globalTokens.Black.value : globalTokens.Background.value};
  display: flex;
  flex-direction: row;
  align-items: center;
`;
export const ReceiptArcodianItemContainer = styled.div`
  padding: ${globalTokens.Spacing8.value}px;
  border-bottom: 1px solid
    ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
  display: flex;
  flex-direction: row;
`;
export const ReceiptArcodianItemTitleTypo = styled(BodyTextTypo)`
  width: 28%;
`;
export const ReceiptArcodianItemChannelTypo = styled(BodyTextTypo)`
  width: 18%;
  text-align: center;
`;
export const ReceiptArcodianItemPriceTypo = styled(BodyTextTypo)`
  width: 18%;
  text-align: center;
`;
export const ReceiptArcodianItemStatusTypo = styled(BodyTextTypo)`
  width: 18%;
  text-align: center;
`;
export const ReceiptArcodianCancelButton = styled(NegativeTextButton)`
  width: 18%;
  padding: 0;
`;
export const ReceiptArcodianHeaderinTitleTypo = styled(
  ReceiptArcodianItemTitleTypo
)`
  text-align: center;
  color: ${(props) =>
    props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
`;
export const ReceiptArcodianHeaderChannelTypo = styled(
  ReceiptArcodianItemChannelTypo
)`
  color: ${(props) =>
    props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
`;
export const ReceiptArcodianHeaderPriceTypo = styled(
  ReceiptArcodianItemPriceTypo
)`
  color: ${(props) =>
    props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
`;
export const ReceiptArcodianHeaderStatusTypo = styled(
  ReceiptArcodianItemStatusTypo
)`
  color: ${(props) =>
    props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
`;
