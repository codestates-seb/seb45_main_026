import { styled } from "styled-components";
import bell from "../../assets/images/icons/bell.svg";
import arrowup from "../../assets/images/icons/arrow/subscribe_arrow_up.svg";
import arrowdown from "../../assets/images/icons/arrow/subscribe_arrow_down.svg";
import { RoundButton } from "../../atoms/buttons/Buttons";
import { useSelector } from "react-redux";
import tokens from '../../styles/tokens.json';

const globalTokens = tokens.global;

export const Subscribed = styled(RoundButton)`
  display: flex;
  flex-direction: row;
  justify-content: space-around;
  align-items: center;
  width: 120px;
  height: 35px;
  background-color: rgba(255,255,255,0);
  color: ${props=>props.isDark?globalTokens.White.value:globalTokens.Black.value};
  &:hover {
    background-color: ${props=>props.isDark?'rgba(255,255,255,0.15)':'rgba(0,0,0,0.15)'};
    color: ${props=>props.isDark?globalTokens.White.value:globalTokens.Black.value};
  }
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
  const isDark = useSelector(state=>state.uiSetting.isDark);

  return (
    <Subscribed isDark={isDark}>
      <BellImg src={bell} alt="구독버튼" />
      구독중
      <ArrowImg isOpened={false} alt="구독버튼 열기" />
    </Subscribed>
  );
};

export default SubscribeBtn;
