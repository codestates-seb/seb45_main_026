import { styled } from "styled-components";

export const InfoWrite = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: space-between;
`;

export const InfoLabel = styled.label`
  margin: 15px 0px 5px 0px;
  font-weight: 600;
`;

export const InfoInput = styled.input`
  width: 100%;
  height: 45px;
  margin-right: 10px;
  padding-left: 10px;
  border-radius: 8px;
  border: 2px solid rgb(233, 233, 233);
`;

export const SubmitBtn = styled.button`
  width: 120px;
  height: 45px;
  border-radius: 8px;
  background-color: rgb(255, 200, 200);
  font-weight: 600;
  font-size: 14px;
`;

const CartEditMode = () => {
  return (
    <>
      <InfoLabel>이름</InfoLabel>
      <InfoInput type="text" />
      <InfoLabel>이메일</InfoLabel>
      <InfoWrite>
        <InfoInput type="text" />
        <SubmitBtn>인증 요청</SubmitBtn>
      </InfoWrite>
      <InfoLabel>인증코드</InfoLabel>
      <InfoWrite>
        <InfoInput type="text" />
        <SubmitBtn>인증</SubmitBtn>
      </InfoWrite>
    </>
  );
};

export default CartEditMode;
