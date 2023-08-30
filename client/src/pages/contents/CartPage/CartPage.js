import { styled } from "styled-components";
import { useSelector } from "react-redux";
import tokens from "../../../styles/tokens.json";
import { PageContainer } from "../../../atoms/layouts/PageContainer";
import CartLeft from "./CartLeft";
import CartRight from "./CartRight";

const globalTokens = tokens.global;

export const CartContainer = styled.div`
  width: 100%;
  max-width: 1170px;
  display: flex;
  flex-direction: column;
  justify-content: start;
  background-color: white;
`;

export const CartTitle = styled.h2`
  width: 100%;
  text-align: start;
  padding: 50px 4%;
`;

export const CartContent = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: center;
  flex-wrap: wrap;
`;

const CartPage = () => {
  const isDark = useSelector((state) => state.uiSetting.isDark);

  return (
    <PageContainer isDark={isDark}>
      <CartContainer>
        <CartTitle>수강 바구니</CartTitle>
        <CartContent>
          <CartLeft />
          <CartRight />
        </CartContent>
      </CartContainer>
    </PageContainer>
  );
};

export default CartPage;
