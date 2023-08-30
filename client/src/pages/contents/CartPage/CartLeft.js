import { styled } from "styled-components";
import CartItem from "../../../components/CartPage/CartItem";
import CartEmpty from "../../../components/CartPage/CartEmpty";

export const CartItems = styled.div`
  width: 100%;
  max-width: 750px;
  
  border: 1px solid red;
`;

export const CartHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 15px 0px 15px;

  border-bottom: 2px solid rgb(236, 236, 236);
`;

export const WholeCheck = styled.div``;

export const CheckedBtn = styled.input`
  width: 20px;
  height: 20px;
`;

export const Checklabel = styled.span`
  margin-left: 10px;
`;

export const RemoveBtn = styled.button`
  background: none;
`;

export const CartLists = styled.ul`
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: center;
`;

export const CartCautions = styled.ul``;

export const CartCaution = styled.li``;

const CartLeft = () => {
  const CartDummyData = [0, 1, 2];

  return (
    <CartItems>
      <CartHeader>
        <WholeCheck>
          <CheckedBtn type="checkbox" />
          <Checklabel>전체선택 1/2</Checklabel>
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
