import { useState } from "react";
import { styled } from "styled-components";
import CartEditMode from "./CartMyInfo/CartEditMode";
import CartInfoMode from "./CartMyInfo/CartInfoMode";

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

const CartMyInfo = () => {
  const [isEdit, setEdit] = useState(false);

  return (
    <CartInfo>
      <InfoTitle>
        <Info>구매자 정보</Info>
        <InfoSave
          onClick={(e) => {
            e.preventDefault();
            setEdit(!isEdit);
          }}
        >
          {isEdit ? "저장" : "수정"}
        </InfoSave>
      </InfoTitle>
      {isEdit ? <CartEditMode /> : <CartInfoMode />}
    </CartInfo>
  );
};

export default CartMyInfo;
