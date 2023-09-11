import { styled } from "styled-components";
import { useSelector } from "react-redux";
import { useState } from "react";
import tokens from '../../styles/tokens.json';
import { BodyTextTypo, SmallTextTypo } from "../../atoms/typographys/Typographys";
import { BigButton, RegularButton } from '../../atoms/buttons/Buttons'
import { RegularInput } from "../../atoms/inputs/Inputs";

const globalTokens = tokens.global;

export const priceToString = (price) => {
  return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};

const CartPayInfo = () => {
  const isDark = useSelector(state=>state.uiSetting.isDark);
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
    <PayForm isDark={isDark}>
      <PointBox>
        <PointLabel isDark={isDark}>포인트</PointLabel>
        <Point isDark={isDark}>보유 : {priceToString(myCartInfo.reward)}</Point>
      </PointBox>
      <PointBox>
        <PointInput
          isDark={isDark}
          type="text"
          placeholder="사용하실 포인트를 입력해주세요."
          value={isDiscount}
          onChange={(e) => handleChangeDiscount(e.target.value)}
          onBlur={(e) => handleBlurDiscount(e.target.value)}
        />
        <SubmitBtn
         isDark={isDark}
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
          <Selected isDark={isDark}>선택 상품 금액</Selected>
          <Selected isDark={isDark}>{priceToString(totalPrice)}원</Selected>
        </PriceInfo>
        <PriceInfo>
          <Discount isDark={isDark}>할인 금액</Discount>
          <Discount isDark={isDark}>{priceToString(isDiscount)}원</Discount>
        </PriceInfo>
        <PriceInfo>
          <Amount isDark={isDark}>총 결제금액</Amount>
          <Amount isDark={isDark}>{priceToString(totalPrice - isDiscount)}원</Amount>
        </PriceInfo>
      </Payment>
      <PayBtn isDark={isDark} onClick={(e) => e.preventDefault()}>결제하기</PayBtn>
    </PayForm>
  );
};

export default CartPayInfo;

export const PayForm = styled.form`
  width: 100%;
  padding: 20px 20px;
  margin: 15px 0px;
  /* border: 1px solid ${props=>props.isDark?globalTokens.Gray.value:globalTokens.LightGray.value}; */
  border-radius: 10px;
  background-color: ${props=>props.isDark?'rgba(255,255,255,0.15)':globalTokens.White.value};

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

export const PointLabel = styled(BodyTextTypo)`
  margin-bottom: 5px;
  font-weight: ${globalTokens.Bold.value};
`;

export const Point = styled(SmallTextTypo)`
  color: ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
  margin-right: 10px;
`;

export const PointInput = styled(RegularInput)`
  width: 100%;
  height: 45px;
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

export const Selected = styled(BodyTextTypo)`
`;

export const Discount = styled(BodyTextTypo)`
  color: ${props=>props.isDark?globalTokens.LightRed.value:globalTokens.MainRed.value};
`;

export const Amount = styled(BodyTextTypo)`
  font-weight: ${globalTokens.Bold.value};
`;

export const PayBtn = styled(BigButton)`
  @media screen and (min-width: 1170px) {
    max-width: 310px;
  }
  @media screen and (min-width: 0px) {
    /* max-width: 310px; */
  }
  width: 100%;
  height: 45px;
  border-radius: 8px;
  margin-top: 10px;
  font-weight: 600;
  font-size: 16px;
`;

export const SubmitBtn = styled(RegularButton)`
  width: 100%;
  max-width: 80px;
  font-size: ${globalTokens.SmallText.value}px;
`;
