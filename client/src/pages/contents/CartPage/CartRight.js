import { styled } from "styled-components";
import CartMyInfo from "../../../components/CartPage/CartMyInfo";
import CartPayInfo from "../../../components/CartPage/CartPayInfo";

export const CartSection = styled.section`
  @media screen and (min-width: 1170px) {
    max-width: 350px;
  }
  @media screen and (min-width: 0px) {
    max-width: 790px;
  }
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: start;
`;

export const PayInfo = styled.div`
  width: 100%;
  padding: 10px 20px;
  margin: 10px 0px;
  border: 1px solid rgb(236, 236, 236);
  background-color: white;
  border-radius: 10px;
  color: gray;
  font-size: 12px;
  font-weight: normal;
`;

const CartRight = () => {
  return (
    <CartSection>
      <CartMyInfo />
      <CartPayInfo />
      <PayInfo>
        회원 본인은 주문내용을 확인했으며, 구매조건 및 개인정보처리방침과 결제에
        동의합니다.
      </PayInfo>
    </CartSection>
  );
};

export default CartRight;
