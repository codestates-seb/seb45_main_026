import { Link } from "react-router-dom";
import { styled } from "styled-components";
import { BodyTextTypo } from "../../atoms/typographys/Typographys";
import { useSelector } from "react-redux";
import tokens from "../../styles/tokens.json";
import { BigButton } from "../../atoms/buttons/Buttons";

const globalTokens = tokens.global;

const CartEmpty = () => {
  const isDark = useSelector((state) => state.uiSetting.isDark);

  return (
    <CartClearBox>
      <ClearGuide isDark={isDark}>담긴 강의가 없습니다.</ClearGuide>
      <ClearGuide isDark={isDark}>
        나를 성장 시켜줄 좋은 강의들을 찾아보세요
      </ClearGuide>
      <Link to="/lecture">
        <ListNavBtn isDark={isDark}>강의리스트 보기</ListNavBtn>
      </Link>
    </CartClearBox>
  );
};

export default CartEmpty;

export const CartClearBox = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: start;
  padding-top: 200px;
`;

export const ClearGuide = styled(BodyTextTypo)`
  width: 100%;
  max-width: 500px;
  text-align: center;
  margin: 5px 0px;
  color: ${(props) =>
    props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
`;

export const ListNavBtn = styled(BigButton)`
  width: 100%;
  max-width: 400px;
  height: 45px;
  margin: 10px 0px;
  border-radius: 8px;
`;
