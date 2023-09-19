import { styled } from "styled-components";
import { useSelector } from "react-redux";
import { useMemo, useState } from "react";
import tokens from "../../styles/tokens.json";
import {
  BodyTextTypo,
  SmallTextTypo,
} from "../../atoms/typographys/Typographys";
import { RegularInput } from "../../atoms/inputs/Inputs";
import { RegularButton } from "../../atoms/buttons/Buttons";
import PaymentBtn from "../../pages/contents/CartPage/TossPayment";
import { AlertModal } from "../../atoms/modal/Modal";

const globalTokens = tokens.global;

// 1,000 천 단위 (,) 추가하기
export const priceToString = (price) => {
  return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};

const CartPayInfo = () => {
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const cartsData = useSelector((state) => state.cartSlice.data);
  const myCartInfo = useSelector((state) => state.cartSlice.myCartInfo);
  const checkedItems = useSelector((state) => state.cartSlice.checkedItem);
  const [isDiscount, setDiscount] = useState(0);
  const [countModal, setCountModal] = useState(false);
  const [overPointModal, setOverPointModal] = useState(false);
  const [spendPointModal, setSpendPointModal] = useState(false);

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

  const handleChangeDiscount = (reward) => {
    setDiscount(reward);
  };

  const handleAllDiscount = (reward) => {
    if (totalPrice > parseInt(reward)) {
      setDiscount(reward);
    } else if (totalPrice < parseInt(reward)) {
      setDiscount(totalPrice);
    }
  };

  const handleBlurDiscount = (reward) => {
    const regExp = /[a-z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣]/g;
    if (regExp.test(reward)) {
      setCountModal(true);
      return setDiscount(0);
    }

    if (totalPrice / 2 < parseInt(reward)) {
      setOverPointModal(true);
      if (totalPrice / 2 < myCartInfo.reward) {
        return setDiscount(totalPrice / 2);
      } else {
        setSpendPointModal(true);
        return setDiscount(myCartInfo.reward);
      }
    }

    if (reward > myCartInfo.reward) {
      setSpendPointModal(true);
      return setDiscount(myCartInfo.reward);
    }
  };

  useMemo(() => {
    if ((totalPrice - isDiscount) <= 0) {
      setDiscount(0);
    }
  }, [totalPrice]);

  return (
    <>
      <PayForm isDark={isDark}>
        <PointBox>
          <PointLabel isDark={isDark}>포인트</PointLabel>
          <Point isDark={isDark}>
            보유 : {priceToString(myCartInfo.reward)}
          </Point>
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
              handleAllDiscount(myCartInfo.reward);
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
            <Amount isDark={isDark}>
              {priceToString(totalPrice - isDiscount)}원
            </Amount>
          </PriceInfo>
        </Payment>
        <PaymentBtn isDiscount={isDiscount} />
      </PayForm>
      <AlertModal
        isModalOpen={countModal}
        setIsModalOpen={setCountModal}
        isBackdropClickClose={true}
        content="숫자만 입력할 수 있습니다."
        buttonTitle="확인"
        handleButtonClick={() => setCountModal(false)}
      />
      <AlertModal
        isModalOpen={overPointModal}
        setIsModalOpen={setOverPointModal}
        isBackdropClickClose={true}
        content="선택한 상품 금액의 최대 50%의 포인트를 사용할 수 있습니다."
        buttonTitle="확인"
        handleButtonClick={() => setOverPointModal(false)}
      />
      <AlertModal
        isModalOpen={spendPointModal}
        setIsModalOpen={setSpendPointModal}
        isBackdropClickClose={true}
        content={`현재 보유 포인트는 ${myCartInfo.reward}포인트 입니다.`}
        buttonTitle="확인"
        handleButtonClick={() => setSpendPointModal(false)}
      />
    </>
  );
};

export default CartPayInfo;

export const PayForm = styled.form`
  width: 100%;
  padding: 20px 20px;
  margin: 15px 0px;
  border: 1px solid
    ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
  border-radius: 10px;
  background-color: ${(props) =>
    props.isDark ? "rgba(255,255,255,0.15)" : globalTokens.White.value};

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
  color: ${(props) =>
    props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
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

export const Selected = styled(BodyTextTypo)``;

export const Discount = styled(BodyTextTypo)`
  color: ${(props) =>
    props.isDark ? globalTokens.LightRed.value : globalTokens.MainRed.value};
`;

export const Amount = styled(BodyTextTypo)`
  font-weight: ${globalTokens.Bold.value};
`;

export const SubmitBtn = styled(RegularButton)`
  width: 100%;
  max-width: 80px;
  font-size: ${globalTokens.SmallText.value}px;
`;
