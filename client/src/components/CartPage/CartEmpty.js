import { styled } from "styled-components";
import { SubmitBtn } from "./CartMyInfo/CartEditMode";

export const CartClearBox = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: start;
  padding-top: 200px;
`;

export const ClearGuide = styled.span`
  width: 100%;
  max-width: 500px;
  text-align: center;
  margin: 5px 0px;
  color: gray;
  font-weight: 600;
`;

export const ListNavBtn = styled(SubmitBtn)`
  width: 100%;
  max-width: 400px;
  margin: 10px 0px;
`;

const CartEmpty = () => {
  return (
    <CartClearBox>
      <ClearGuide>담긴 강의가 없습니다.</ClearGuide>
      <ClearGuide>나를 성장 시켜줄 좋은 강의들을 찾아보세요</ClearGuide>
      <ListNavBtn>강의리스트 보기</ListNavBtn>
    </CartClearBox>
  );
};

export default CartEmpty;
