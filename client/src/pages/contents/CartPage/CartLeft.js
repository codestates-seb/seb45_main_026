import { styled } from "styled-components";
import { useDispatch, useSelector } from "react-redux";
import axios from "axios";
import tokens from "../../../styles/tokens.json";
import CartItem from "../../../components/CartPage/CartItem";
import CartEmpty from "../../../components/CartPage/CartEmpty";
import { setChecked } from "../../../redux/createSlice/CartsSlice";
import { BodyTextTypo } from "../../../atoms/typographys/Typographys";
import { NegativeTextButton } from "../../../atoms/buttons/Buttons";

const globalTokens = tokens.global;

const CartLeft = () => {
  const dispatch = useDispatch();
  const cartsData = useSelector((state) => state.cartSlice.data);
  const checkedItems = useSelector((state) => state.cartSlice.checkedItem);
  const token = useSelector((state) => state.loginInfo.accessToken);
  const isDark = useSelector((state) => state.uiSetting.isDark);

  const handleAllCheck = (checked) => {
    if (checked) {
      dispatch(setChecked(cartsData.map((el) => el.videoId)));
    } else {
      dispatch(setChecked([]));
    }
  };

  const handlePatchItemList = () => {
    return axios
      .delete(`https://api.itprometheus.net/videos/carts`, {
        headers: { Authorization: token.authorization },
        data: { videoIds: checkedItems },
      })
      .then((res) => {
        console.log(res.data);
        dispatch(setChecked([]))
      })
      .catch((err) => console.log(err));
  };

  return (
    <CartItems isDark={isDark}>
      <CartHeader isDark={isDark}>
        <WholeCheck>
          <CheckedBtn
            type="checkbox"
            checked={checkedItems.length === cartsData.length}
            onChange={(e) => handleAllCheck(e.target.checked)}
          />
          <Checklabel isDark={isDark}>
            전체선택 {checkedItems.length}/{cartsData.length}
          </Checklabel>
        </WholeCheck>
        <RemoveBtn isDark={isDark} onClick={handlePatchItemList}>
          &times; 선택 삭제
        </RemoveBtn>
      </CartHeader>
      <CartLists isScroll={cartsData.length}>
        {cartsData.length ? (
          cartsData.map((el) => <CartItem key={el.videoId} el={el} />)
        ) : (
          <CartEmpty />
        )}
      </CartLists>
      <CartCautions isDark={isDark}>
        장바구니 상품 안내
        <CartCaution isDark={isDark}>
          장바구니에 담은 상품은 최대 20개까지 보관됩니다.
        </CartCaution>
      </CartCautions>
    </CartItems>
  );
};

export default CartLeft;

export const CartItems = styled.div`
  width: 100%;
  max-width: 790px;
  min-height: 750px;
  margin: 20px 0px;
  padding: 0px 10px;
  border: 1px solid
    ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
  border-radius: 8px;
  position: relative;
  background-color: ${(props) =>
    props.isDark ? "rgba(255,255,255,0.15)" : globalTokens.White.value};
`;

export const CartHeader = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  padding: 15px 15px 10px 15px;
  border-bottom: 1px solid
    ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
`;

export const WholeCheck = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: center;
`;

export const CheckedBtn = styled.input`
  min-width: 15px;
  height: 15px;
  accent-color: rgb(255, 90, 90);
`;

export const Checklabel = styled(BodyTextTypo)`
  width: 300px;
  height: 30px;
  margin-left: 10px;
  padding-top: 2px;
`;

export const RemoveBtn = styled(NegativeTextButton)`
  border-radius: 8px;
  width: 100px;
  height: 35px;
  font-size: 14px;
`;

export const CartLists = styled.ul`
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: center;
  overflow-y: ${(props) => (props.isScroll > 3 ? "scroll" : "none")};
  height: 580px;
`;

export const CartCautions = styled.ul`
  position: absolute;
  list-style-type: "-";
  bottom: 2%;
  left: 2%;
  padding-left: 15px;
  font-size: ${globalTokens.BodyText.value};
  font-weight: ${globalTokens.Bold.value};
  color: ${(props) =>
    props.isDark ? globalTokens.White.value : globalTokens.Black.value};
`;

export const CartCaution = styled.li`
  margin: ${globalTokens.Spacing4.value}px 0px;
  padding-left: 10px;
  color: ${(props) =>
    props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
  font-size: ${globalTokens.BodyText.value}px;
  font-weight: normal;
`;
