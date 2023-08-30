import { styled } from "styled-components";
import { useSelector } from "react-redux";
import tokens from "../../../styles/tokens.json";
import { PageContainer } from "../../../atoms/layouts/PageContainer";

const globalTokens = tokens.global;

const CartItemPage = () => {
  const isDark = useSelector((state) => state.uiSetting.isDark);

  return (
    <PageContainer isDark={isDark}>
      <div>장바구니 페이지 입니다.</div>
    </PageContainer>
  );
};

export default CartItemPage;