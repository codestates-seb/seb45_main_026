import { styled } from "styled-components";
import { CheckedBtn } from "../../pages/contents/CartPage/CartLeft";
import {
  Category,
  CategoryLists,
} from "../../pages/contents/DetailPage/DetailContent";

export const CartList = styled.li`
  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: start;
  border-bottom: 1px solid rgb(236, 236, 236);
  width: 100%;
  margin: 10px 0px;
  padding: 15px;
`;

export const VideoImage = styled.div`
  width: 100%;
  max-width: 170px;
  aspect-ratio: 8 / 5;
  background-color: rgb(200, 200, 200);
  margin: 0px 10px;
`;

export const Content = styled.div`
  position: relative;

  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: start;

  border-right: 1px solid rgb(236, 236, 236);
  width: 100%;
  max-width: 400px;
  height: 100px;
`;

export const ItemTitle = styled.span`
  width: 100%;
  margin: 5px 0px;
  font-weight: bold;
`;
export const ItemName = styled.span`
  width: 100%;
  margin: 5px 0px;
`;

export const CancelBtn = styled.button`
  position: absolute;
  top: 3%;
  right: 3%;
`;

export const ItemPrice = styled.div`
  width: 100%;
  max-width: 140px;
  height: 100px;
  padding-top: 70px;
  text-align: end;
  font-weight: bold;
`;

const CartItem = () => {
  return (
    <CartList>
      <CheckedBtn type="checkbox"/>
      <VideoImage />
      <Content>
        <ItemTitle>TypeScript 고급반 (TypeScript란?)</ItemTitle>
        <ItemName>코딩사과</ItemName>
        <Category>
          <CategoryLists>#React</CategoryLists>
          <CategoryLists>#TypeScript</CategoryLists>
          <CategoryLists>#Web</CategoryLists>
        </Category>
        <CancelBtn>&times;</CancelBtn>
      </Content>
      <ItemPrice>15,000원</ItemPrice>
    </CartList>
  );
};

export default CartItem;
