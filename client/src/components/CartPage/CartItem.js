import { styled } from "styled-components";
import { CheckedBtn } from "../../pages/contents/CartPage/CartLeft";
import {
  Category,
  CategoryLists,
} from "../../pages/contents/DetailPage/DetailContent";
import axios from "axios";
import { useDispatch, useSelector } from "react-redux";
import { setChecked } from "../../redux/createSlice/CartsSlice";
import { priceToString } from "./CartPayInfo";
import tokens from "../../styles/tokens.json";
import { BodyTextTypo } from "../../atoms/typographys/Typographys";
import { NegativeTextButton } from "../../atoms/buttons/Buttons";

const globalTokens = tokens.global;

const CartItem = ({ el }) => {
  const dispatch = useDispatch();
  const checkedItems = useSelector((state) => state.cartSlice.checkedItem);
  const token = useSelector((state) => state.loginInfo.accessToken);
  const isDark = useSelector((state) => state.uiSetting.isDark);

  const handlePatchItemList = (videoId) => {
    return axios
      .patch(`https://api.itprometheus.net/videos/${videoId}/carts`, null, {
        headers: { Authorization: token.authorization },
      })
      .then((res) => {
        dispatch(setChecked([]));
      })
      .catch((err) => console.log(err));
  };

  const handleCheckChange = (checked, id) => {
    if (checked) {
      dispatch(setChecked([...checkedItems, id]));
    } else {
      dispatch(setChecked(checkedItems.filter((el) => el !== id)));
    }
  };

  return (
    <CartList isDark={isDark}>
      <CheckedBtn
        isDark={isDark}
        type="checkbox"
        checked={checkedItems.includes(el.videoId)}
        onChange={(e) => handleCheckChange(e.target.checked, el.videoId)}
      />
      <VideoImage src={el.thumbnailUrl} isDark={isDark} />
      <Content isDark={isDark}>
        <ItemTitle isDark={isDark}>{el.videoName}</ItemTitle>
        <ItemName isDark={isDark}>{el.channel.channelName}</ItemName>
        <Category isDark={isDark}>
          {el.videoCategories.map((el) => (
            <CategoryLists key={el.categoryId}>
              #{el.categoryName}
            </CategoryLists>
          ))}
        </Category>
        <CancelBtn
          isDark={isDark}
          onClick={() => handlePatchItemList(el.videoId)}
        >
          &times;
        </CancelBtn>
      </Content>
      <ItemPrice isDark={isDark}>{priceToString(el.price)}원</ItemPrice>
    </CartList>
  );
};

export default CartItem;

export const CartList = styled.li`
  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: start;
  border-bottom: 1px solid
    ${(props) =>
      props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
  width: 100%;
  margin: ${globalTokens.Spacing4.value}px 0px;
  padding: ${globalTokens.Spacing20.value}px ${globalTokens.Spacing16.value}px;
`;

export const VideoImage = styled.img`
  width: 100%;
  max-width: 180px;
  aspect-ratio: 8 / 5;
  margin: 0px ${globalTokens.Spacing16.value}px;
  border-radius: ${globalTokens.Spacing8.value}px;
`;

export const Content = styled.div`
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: start;

  border-right: 1px solid
    ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
  width: 100%;
  height: 100px;
  max-width: 400px;

  padding-left: ${globalTokens.Spacing8.value}px;
`;

export const ItemTitle = styled(BodyTextTypo)`
  width: 100%;
  margin: ${globalTokens.Spacing4.value}px 0px;
  font-weight: ${globalTokens.Bold.value};
`;

export const ItemName = styled(BodyTextTypo)`
  width: 100%;
  margin: ${globalTokens.Spacing4.value}px 0px;
  color: ${(props) =>
    props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
`;

export const CancelBtn = styled(NegativeTextButton)`
  position: absolute;
  top: 3%;
  right: 3%;
`;

export const ItemPrice = styled(BodyTextTypo)`
  width: 100%;
  max-width: 140px;
  height: 100px;
  padding-top: 70px;
  text-align: end;
  font-weight: bold;
`;
