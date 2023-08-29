import { styled } from "styled-components";
import bell from "../../assets/images/icons/bell.svg";
import arrowup from "../../assets/images/icons/arrow/subscribe_arrow_up.svg";
import arrowdown from "../../assets/images/icons/arrow/subscribe_arrow_down.svg";

export const Subscribed = styled.button`
  display: flex;
  flex-direction: row;
  justify-content: space-around;
  align-items: center;
  width: 120px;
  height: 35px;
  border: 1px solid gray;
  border-radius: 20px;
`;

export const BellImg = styled.img.attrs({
  src: `${bell}`,
})`
  width: 20px;
  height: 20px;
`;

export const ArrowImg = styled.img.attrs((props) => ({
  src: `${props.isOpened ? arrowup : arrowdown}`,
}))`
  width: 15px;
  height: 15px;
`;

const SubscribeBtn = () => {
  return (
    <Subscribed>
      <BellImg src={bell} alt="구독버튼" />
      구독중
      <ArrowImg isOpened={false} alt="구독버튼 열기" />
    </Subscribed>
  );
};

export default SubscribeBtn;
