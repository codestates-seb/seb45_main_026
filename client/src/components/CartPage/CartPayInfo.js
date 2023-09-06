import { styled } from "styled-components";
import { useSelector } from "react-redux";
import { useState } from "react";

export const priceToString = (price) => {
  return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};

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
    const regExp = /[a-z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣]/g;
    if (regExp.test(reward)) {
      setDiscount(0);
      return alert("숫자만 입력할 수 있습니다.");
    }

    if (totalPrice < parseInt(reward)) {
      setDiscount(totalPrice);
      return alert("선택하신 상품 금액을 넘어서 포인트를 사용할 수 없습니다.")
    }

    if (reward > myCartInfo.reward) {
      alert(`현재 사용할 수 있는 포인트는 ${myCartInfo.reward}포인트 입니다.`);
      setDiscount(myCartInfo.reward);
    }
  };

  return (
    <PayForm>
      <PointBox>
        <PointLabel>포인트</PointLabel>
        <Point>보유 : {priceToString(myCartInfo.reward)}</Point>
      </PointBox>
      <PointBox>
        <PointInput
          type="text"
          placeholder="사용하실 포인트를 입력해주세요."
          value={isDiscount}
          onChange={(e) => handleChangeDiscount(e.target.value)}
          onBlur={(e) => handleBlurDiscount(e.target.value)}
        />
        <SubmitBtn
          onClick={(e) => {
            e.preventDefault();
            handleChangeDiscount(myCartInfo.reward);
          }}
        >
          전액 사용
        </SubmitBtn>
      </PointBox>
      <Payment>
        <PriceInfo>
          <Selected>선택 상품 금액</Selected>
          <Selected>{priceToString(totalPrice)}원</Selected>
        </PriceInfo>
        <PriceInfo>
          <Discount>할인 금액</Discount>
          <Discount>{priceToString(isDiscount)}원</Discount>
        </PriceInfo>
        <PriceInfo>
          <Amount>총 결제금액</Amount>
          <Amount>{priceToString(totalPrice - isDiscount)}원</Amount>
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
