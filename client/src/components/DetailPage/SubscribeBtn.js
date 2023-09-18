import axios from "axios";
import { useSelector } from "react-redux";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import bell from "../../assets/images/icons/bell.svg";
import { RoundButton } from "../../atoms/buttons/Buttons";

const SubscribeBtn = ({ memberId, setSub, channelInfo }) => {
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const token = useSelector((state) => state.loginInfo.accessToken);

  const handleSubscribe = () => {
    return axios
      .patch(
        `https://api.itprometheus.net/channels/${memberId}/subscribe`,
        null,
        {
          headers: { Authorization: token.authorization },
        }
      )
      .then((res) => {
        if (res.data.code === 200) {
          setSub(res.data.data);
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };

  return (
    <>
      <Subscribed isDark={isDark} onClick={() => handleSubscribe()}>
        {!channelInfo.isSubscribed ? (
          <>구독하기</>
        ) : (
          <>
            <BellImg src={bell} alt="구독버튼" />
            구독중
          </>
        )}
      </Subscribed>
    </>
  );
};

export default SubscribeBtn;

const globalTokens = tokens.global;

export const Subscribed = styled(RoundButton)`
  position: relative;
  display: flex;
  flex-direction: row;
  justify-content: space-around;
  align-items: center;
  width: 120px;
  height: 35px;
  background-color: rgba(255, 255, 255, 0);
  color: ${(props) =>
    props.isDark ? globalTokens.White.value : globalTokens.Black.value};
  &:hover {
    background-color: ${(props) =>
      props.isDark ? "rgba(255,255,255,0.15)" : "rgba(0,0,0,0.15)"};
    color: ${(props) =>
      props.isDark ? globalTokens.White.value : globalTokens.Black.value};
  }
`;

export const BellImg = styled.img.attrs({
  src: `${bell}`,
})`
  width: 20px;
  height: 20px;
`;
