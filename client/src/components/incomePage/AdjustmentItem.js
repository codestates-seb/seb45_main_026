import { useSelector } from "react-redux";
import styled from "styled-components";
import {
  ReceiptAmountTypo,
  ReceiptItemContainer,
  ReceiptTitleTypo,
} from "../receiptPage/ReceiptItem.style";

const AdjustmentItem = ({ item }) => {
  const isDark = useSelector((state) => state.uiSetting.isDark);

  const adjustmentStatus =
    item.adjustmentStatus === "ADJUSTING"
      ? "정산 중"
      : item.adjustmentStatus === "ADJUSTED"
      ? "정산 완료"
      : item.adjustmentStatus === "FAILED"
      ? "정산 실패"
      : item.adjustmentStatus === "NO_ADJUSTMENT"
      ? "정산 미진행"
      : item.adjustmentStatus === "NOT_ADJUSTED" && "정산 전";

  const adjustmentNotice =
    item.adjustmentStatus === "ADJUSTING"
      ? "정산 처리 중 입니다."
      : item.adjustmentStatus === "ADJUSTED"
      ? "정산이 완료 되었습니다."
      : item.adjustmentStatus === "FAILED"
      ? "정산에 실패하였습니다. 자세한 내용은 고객센터에 문의해주세요."
      : item.adjustmentStatus === "NO_ADJUSTMENT"
      ? "정산 될 내역이 없습니다."
      : item.adjustmentStatus === "NOT_ADJUSTED" &&
        "추후 정산이 될 예정입니다.";

  return (
    <IncomeItemContainer isDark={isDark}>
      <IncomeMonthTypo isDark={isDark}>{item.month}월</IncomeMonthTypo>
      <IncomeAmountTypo isDark={isDark}>
        {item.amount ? `${item.amount.toLocaleString()}원` : "-"}
      </IncomeAmountTypo>
      <IncomeStatusTypo isDark={isDark}>{adjustmentStatus}</IncomeStatusTypo>
      <IncomeReasonTypo isDark={isDark}>{adjustmentNotice}</IncomeReasonTypo>
    </IncomeItemContainer>
  );
};

export default AdjustmentItem;

export const IncomeItemContainer = styled(ReceiptItemContainer)``;
export const IncomeMonthTypo = styled(ReceiptTitleTypo)`
  text-align: center;
  width: 70px;
`;
export const IncomeAmountTypo = styled(ReceiptAmountTypo)`
  width: 200px;
`;
export const IncomeStatusTypo = styled(ReceiptAmountTypo)`
  width: 180px;
`;
export const IncomeReasonTypo = styled(ReceiptAmountTypo)`
  width: 100%;
  max-width: 500px;
`;
