import { styled } from "styled-components";

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

const CartInfoMode = () => {
  return (
    <>
      <InfoBox>
        <InfoSubtitle>이름</InfoSubtitle>
        <InfoContnent>김둥구</InfoContnent>
      </InfoBox>
      <InfoBox>
        <InfoSubtitle>이메일</InfoSubtitle>
        <InfoContnent>test@google.com</InfoContnent>
      </InfoBox>
    </>
  );
};

export default CartInfoMode;
