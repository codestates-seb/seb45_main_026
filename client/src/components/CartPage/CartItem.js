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

const CartItem = ({ el }) => {
  const dispatch = useDispatch();
  const checkedItems = useSelector((state) => state.cartSlice.checkedItem);
  const token = useSelector((state) => state.loginInfo.accessToken);
  const headers = {
    Authorization: token.authorization,
    refresh: token.refresh,
  };

  const handlePatchItemList = (videoId) => {
    return axios
      .patch(`https://api.itprometheus.net/videos/${videoId}/carts`, null, {
        headers,
      })
      .then((res) => console.log(res.data))
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
    <CartList>
      <CheckedBtn
        type="checkbox"
        checked={checkedItems.includes(el.videoId)}
        onChange={(e) => handleCheckChange(e.target.checked, el.videoId)}
      />
      <VideoImage src={el.thumbnailUrl} />
      <Content>
        <ItemTitle>{el.videoName}</ItemTitle>
        <ItemName>{el.channel.channelName}</ItemName>
        <Category>
          {[{ categoryId: 1234, categoryName: "데이터 불러와야함." }].map(
            (el) => (
              <CategoryLists key={el.categoryId}>
                #{el.categoryName}
              </CategoryLists>
            )
          )}
        </Category>
        <CancelBtn onClick={() => handlePatchItemList(el.videoId)}>
          &times;
        </CancelBtn>
      </Content>
      <ItemPrice>{priceToString(el.price)}원</ItemPrice>
    </CartList>
  );
};

export default CartItem;

export const CartList = styled.li`
  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: start;
  border-bottom: 1px solid rgb(236, 236, 236);
  width: 100%;
  margin: 5px 0px;
  padding: 20px 15px;
`;

export const VideoImage = styled.img`
  width: 100%;
  max-width: 180px;
  aspect-ratio: 8 / 5;
  margin: 0px 15px;
  border-radius: 8px;
`;

export const Content = styled.div`
  position: relative;

  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: start;

  border-right: 1px solid rgb(236, 236, 236);
  width: 100%;
  height: 100px;
  max-width: 400px;

  padding-left: 10px;
`;

export const ItemTitle = styled.span`
  width: 100%;
  margin: 5px 0px;
  font-weight: bold;
`;

export const ItemName = styled.span`
  width: 100%;
  margin: 5px 0px;
  font-weight: 600;
  color: gray;
`;

export const CancelBtn = styled.button`
  position: absolute;
  top: 3%;
  right: 3%;
`;

export const ItemPrice = styled.div`
  width: 100%;
  max-width: 140px;
  height: 100px;
  padding-top: 70px;
  text-align: end;
  font-weight: bold;
`;
