import { styled } from "styled-components";
import { SubmitBtn } from "./CartMyInfo/CartEditMode";

export const PayForm = styled.form`
  width: 100%;
  padding: 20px 20px;
  margin: 15px 0px;
  border: 1px solid rgb(236, 236, 236);
  border-radius: 10px;
  background-color: white;

  display: flex;
  flex-direction: column;
  justify-content: start;
`;

export const PointBox = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  margin-bottom: 10px;
`;

export const PointLabel = styled.label`
  margin-bottom: 5px;
  font-weight: 600;
`;

export const PointInput = styled.input`
  width: 100%;
  height: 45px;
  border-radius: 8px;
  border: 2px solid rgb(233, 233, 233);
  margin-right: 10px;
  padding-right: 10px;
  text-align: end;
`;

export const Payment = styled.div``;

export const PriceInfo = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  margin: 10px 0px;
`;

export const Selected = styled.span`
  font-weight: 600;
  color: rgb(190, 190, 190);
`;

export const Discount = styled.span`
  font-weight: 600;
  color: rgb(255, 150, 150);
`;

export const Amount = styled.span`
  font-weight: 600;
`;

export const PayBtn = styled.button`
  @media screen and (min-width: 1170px) {
    max-width: 310px;
  }
  @media screen and (min-width: 0px) {
    /* max-width: 310px; */
  }
  width: 100%;
  height: 45px;
  border-radius: 8px;
  background-color: rgb(255, 200, 200);
  margin-top: 10px;
  font-weight: 600;
  font-size: 16px;
`;

const CartPayInfo = () => {
  return (
    <PayForm>
      <PointLabel for="point">포인트</PointLabel>
      <PointBox>
        <PointInput
          id="point"
          type="text"
          placeholder="1,000원 이상 사용가능"
        />
        <SubmitBtn>전액 사용</SubmitBtn>
      </PointBox>
      <Payment>
        <PriceInfo>
          <Selected>선택 상품 금액</Selected>
          <Selected>19,000원</Selected>
        </PriceInfo>
        <PriceInfo>
          <Discount>할인 금액</Discount>
          <Discount>1,900원</Discount>
        </PriceInfo>
        <PriceInfo>
          <Amount>총 결제금액</Amount>
          <Amount>17,100원</Amount>
        </PriceInfo>
      </Payment>
      <PayBtn onClick={(e) => e.preventDefault()}>결제하기</PayBtn>
    </PayForm>
  );
};

export default CartPayInfo;
