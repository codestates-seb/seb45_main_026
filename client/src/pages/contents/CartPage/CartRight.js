import { styled } from "styled-components";
import axios from "axios";
import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import CartMyInfo from "../../../components/CartPage/CartMyInfo";
import CartPayInfo from "../../../components/CartPage/CartPayInfo";
import { setMyInfo } from "../../../redux/createSlice/CartsSlice";
import tokens from '../../../styles/tokens.json';

const globalTokens = tokens.global;

const CartRight = () => {
  const isDark = useSelector(state=>state.uiSetting.isDark);
  const dispatch = useDispatch();
  const token = useSelector((state) => state.loginInfo.accessToken);
  const headers = {
    Authorization: token.authorization,
    refresh: token.refresh,
  };

  useEffect(() => {
    axios
      .get(`https://api.itprometheus.net/members`, { headers })
      .then((res) => dispatch(setMyInfo(res.data.data)))
      .catch((err) => console.log(err));
  });

  return (
    <CartSection>
      <CartMyInfo />
      <CartPayInfo />
      <PayInfo isDark={isDark}>
        회원 본인은 주문내용을 확인했으며, 구매조건 및 개인정보처리방침과 결제에
        동의합니다.
      </PayInfo>
    </CartSection>
  );
};

export default CartRight;

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
  margin: 10px 0px 25px 0px;
  border: 1px solid ${props=>props.isDark?globalTokens.Gray.value:globalTokens.LightGray.value};
  background-color: ${ props=>props.isDark? 'rgba(255,255,255,0.15)' : globalTokens.White.value };
  border-radius: 10px;
  color: ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
  font-size: 12px;
  font-weight: normal;
`;
