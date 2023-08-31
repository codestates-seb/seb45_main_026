import { styled } from "styled-components";
import { useSelector } from "react-redux";
// import tokens from "../../../styles/tokens.json";
import { PageContainer } from "../../../atoms/layouts/PageContainer";
import CartLeft from "./CartLeft";
import CartRight from "./CartRight";

// const globalTokens = tokens.global;

export const CartContainer = styled.div`
  width: 100%;
  max-width: 1170px;
  display: flex;
  flex-direction: column;
  justify-content: start;
  /* background-color: white; */
`;

export const CartTitle = styled.h2`
  width: 100%;
  text-align: start;
  padding: 50px 4%;
`;

export const CartContent = styled.div`
  width: 100%;
  display: grid;
  justify-items: center;
  /* place-items: center; */
  flex-wrap: wrap;

  @media screen and (min-width: 0px) {
    grid-template-columns: repeat(1, 1fr);
  }

  @media screen and (min-width: 1170px) {
    grid-template-columns: 790px 350px;
    grid-column-gap: 30px;
  }
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
