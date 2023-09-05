import { styled } from "styled-components";
import { useSelector } from "react-redux";
import { useState } from "react";

const CartPayInfo = () => {
  const cartsData = useSelector((state) => state.cartSlice.data);
  const checkedItems = useSelector((state) => state.cartSlice.checkedItem);
  const myCartInfo = useSelector((state) => state.cartSlice.myCartInfo);

  const getTotal = () => {
    const cartsArr = cartsData.map((el) => el.videoId);
    let total = 0;
    for (let i = 0; i < cartsArr.length; i++) {
      if (checkedItems.includes(cartsArr[i])) {
        total = total + cartsData[i].price;
      }
    }
    return total;
  };
  const totalPrice = getTotal();

  const [isDiscount, setDiscount] = useState(0);

  const handleChangeDiscount = (reward) => {
    setDiscount(reward);
  };

  const handleBlurDiscount = (reward) => {
    if (reward > parseInt(myCartInfo.reward)) {
      alert(`잔여 포인트는 ${myCartInfo.reward}포인트 입니다.`);
      setDiscount(parseInt(myCartInfo.reward));
    }
    if (isDiscount < 1000) {
      alert("1,000포인트 이상 사용 가능합니다.");
      setDiscount(0);
    }
  };

  return (
    <PayForm>
      <PointBox>
        <PointLabel>포인트</PointLabel>
        <Point>보유 : {myCartInfo.reward}</Point>
      </PointBox>
      <PointBox>
        <PointInput
          type="number"
          placeholder="1,000원 이상 사용가능"
          value={isDiscount}
          onChange={(e) => handleChangeDiscount(e.target.value)}
          onBlur={(e) => handleBlurDiscount(e.target.value)}
        />
        <SubmitBtn>전액 사용</SubmitBtn>
      </PointBox>
      <Payment>
        <PriceInfo>
          <Selected>선택 상품 금액</Selected>
          <Selected>{totalPrice}원</Selected>
        </PriceInfo>
        <PriceInfo>
          <Discount>할인 금액</Discount>
          <Discount>{isDiscount}원</Discount>
        </PriceInfo>
        <PriceInfo>
          <Amount>총 결제금액</Amount>
          <Amount>{totalPrice}원</Amount>
        </PriceInfo>
      </Payment>
      <PayBtn onClick={(e) => e.preventDefault()}>결제하기</PayBtn>
    </PayForm>
  );
};

export default CartPayInfo;

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
  margin-bottom: 0px;
`;

export const PointLabel = styled.label`
  margin-bottom: 5px;
  font-weight: 600;
`;

export const Point = styled.span`
  font-size: small;
  color: gray;
  margin-right: 10px;
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

export const Payment = styled.div`
  margin-top: 10px;
`;

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

export const SubmitBtn = styled.button`
  width: 100%;
  max-width: 80px;
  border-radius: 8px;
  background-color: rgb(255, 200, 200);
  font-weight: 600;
  font-size: 14px;
`;
