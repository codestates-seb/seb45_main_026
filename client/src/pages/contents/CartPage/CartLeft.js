import { styled } from "styled-components";
import CartItem from "../../../components/CartPage/CartItem";
import CartEmpty from "../../../components/CartPage/CartEmpty";

export const CartItems = styled.div`
  width: 100%;
  max-width: 790px;
  min-height: 750px;
  margin: 20px 0px;
  padding: 0px 10px;
  border: 1px solid rgb(236, 236, 236);
  border-radius: 8px;
  position: relative;
  background-color: white;
`;

export const CartHeader = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  padding: 15px 15px 10px 15px;
  border-bottom: 2px solid rgb(236, 236, 236);
`;

export const WholeCheck = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: center;
`;

export const CheckedBtn = styled.input`
  width: 20px;
  height: 20px;
  accent-color: rgb(255, 90, 90);
`;

export const Checklabel = styled.label`
  width: 100px;
  height: 30px;
  margin-left: 10px;
  padding-top: 2px;
`;

export const RemoveBtn = styled.button`
  border-radius: 8px;
  background-color: rgb(255, 200, 200);
  width: 100px;
  height: 35px;
  font-size: 14px;
`;

export const CartLists = styled.ul`
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: center;
`;

export const CartCautions = styled.ul`
  position: absolute;
  list-style-type: "-";
  bottom: 2%;
  left: 2%;
  padding-left: 15px;
  font-size: 14px;
  font-weight: bold;
`;

export const CartCaution = styled.li`
  margin: 10px 0px;
  padding-left: 10px;
  color: gray;
  font-weight: normal;
`;

const CartLeft = () => {
  // const CartDummyData = [];
  const CartDummyData = [0, 1, 2];

  return (
    <CartItems>
      <CartHeader>
        <WholeCheck>
          <CheckedBtn id="whole" type="checkbox" />
          <Checklabel for="whole">전체선택 1/2</Checklabel>
        </WholeCheck>
        <RemoveBtn>&times; 선택 삭제</RemoveBtn>
      </CartHeader>
      <CartLists>
        {CartDummyData.length ? (
          CartDummyData.map((el, idx) => <CartItem key={idx} el={el} />)
        ) : (
          <CartEmpty />
        )}
      </CartLists>
      <CartCautions>
        장바구니 상품 안내
        <CartCaution>
          장바구니에 담은 상품은 최대 10개까지 보관됩니다.
        </CartCaution>
      </CartCautions>
    </CartItems>
  );
};

export default CartLeft;
