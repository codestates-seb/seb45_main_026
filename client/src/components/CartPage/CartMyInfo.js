import { styled } from "styled-components";
import { useSelector } from "react-redux";

const CartMyInfo = () => {
  const myCartInfo = useSelector((state) => state.cartSlice.myCartInfo);

  return (
    <CartInfo>
      <InfoTitle>
        <Info>구매자 정보</Info>
      </InfoTitle>
      <InfoBox>
        <InfoSubtitle>이름</InfoSubtitle>
        <InfoContnent>{myCartInfo.nickname}</InfoContnent>
      </InfoBox>
      <InfoBox>
        <InfoSubtitle>이메일</InfoSubtitle>
        <InfoContnent>{myCartInfo.email}</InfoContnent>
      </InfoBox>
      <InfoBox>
        <InfoSubtitle>등급</InfoSubtitle>
        <InfoContnent>{myCartInfo.grade}</InfoContnent>
      </InfoBox>
    </CartInfo>
  );
};

export default CartMyInfo;

export const CartInfo = styled.form`
  width: 100%;
  padding: 20px;
  margin: 15px 0px;
  border: 1px solid rgb(236, 236, 236);
  border-radius: 10px;
  background-color: white;

  display: flex;
  flex-direction: column;
  justify-content: start;
`;

export const InfoTitle = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  padding-bottom: 10px;
  border-bottom: 2px solid rgb(236, 236, 236);
  font-weight: bold;
`;

export const Info = styled.span``;

export const InfoSave = styled.button`
  color: rgb(260, 100, 120);
  text-decoration: underline;
`;

export const InfoBox = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: center;

  margin-top: 10px;
  color: gray;
`;

export const InfoSubtitle = styled.span`
  width: 80px;
`;
export const InfoContnent = styled.span``;
