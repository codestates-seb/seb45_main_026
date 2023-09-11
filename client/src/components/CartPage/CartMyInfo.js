import { styled } from "styled-components";
import { useSelector } from "react-redux";
import { BodyTextTypo } from '../../atoms/typographys/Typographys'
import tokens from '../../styles/tokens.json';

const globalTokens = tokens.global;

const CartMyInfo = () => {
  const isDark = useSelector(state=>state.uiSetting.isDark);
  const myCartInfo = useSelector((state) => state.cartSlice.myCartInfo);

  return (
    <CartInfo isDark={isDark}>
      <InfoTitle isDark={isDark}>
        <Info isDark={isDark}>구매자 정보</Info>
      </InfoTitle>
      <InfoBox isDark={isDark}>
        <InfoSubtitle isDark={isDark}>이름</InfoSubtitle>
        <InfoContnent isDark={isDark}>{myCartInfo.nickname}</InfoContnent>
      </InfoBox>
      <InfoBox>
        <InfoSubtitle isDark={isDark}>이메일</InfoSubtitle>
        <InfoContnent isDark={isDark}>{myCartInfo.email}</InfoContnent>
      </InfoBox>
      <InfoBox>
        <InfoSubtitle isDark={isDark}>등급</InfoSubtitle>
        <InfoContnent isDark={isDark}>{myCartInfo.grade}</InfoContnent>
      </InfoBox>
    </CartInfo>
  );
};

export default CartMyInfo;

export const CartInfo = styled.form`
  width: 100%;
  padding: 20px;
  margin: 15px 0px;
  /* border: 1px solid ${props=>props.isDark?globalTokens.Gray.value:globalTokens.LightGray.value};; */
  border-radius: ${globalTokens.Spacing8.value}px;
  background-color: ${props=>props.isDark?'rgba(255,255,255,0.15)':globalTokens.White.value};

  display: flex;
  flex-direction: column;
  justify-content: start;
`;

export const InfoTitle = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  padding-bottom: 10px;
  border-bottom: 1px solid ${props=>props.isDark?globalTokens.Gray.value:globalTokens.LightGray.value};
  font-weight: bold;
`;

export const Info = styled(BodyTextTypo)`

`;

export const InfoBox = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: center;
  margin-top: 10px;
  color: ${props=>props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
`;

export const InfoSubtitle = styled(BodyTextTypo)`
  width: 80px;
  color: ${props=>props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
`;
export const InfoContnent = styled(BodyTextTypo)`
  color: ${props=>props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
`;
