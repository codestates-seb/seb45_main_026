import axios from "axios";
import { useState } from "react";
import { useSelector } from "react-redux";
import { styled } from "styled-components";
import { useToken } from "../../hooks/useToken";
import tokens from "../../styles/tokens.json";
import { ReactComponent as Cart } from "../../assets/images/icons/listItem/Cart.svg";
import { RoundButton } from "../../atoms/buttons/Buttons";
import { AlertModal } from "../../atoms/modal/Modal";

const AddCart = ({ videoId, isInCart, content = "", border = false }) => {
  const refreshToken = useToken();
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const token = useSelector((state) => state.loginInfo.accessToken);
  const [isCart, setCart] = useState(isInCart);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handlePahctCart = () => {
    return axios
      .patch(`https://api.itprometheus.net/videos/${videoId}/carts`, null, {
        headers: { Authorization: token.authorization },
      })
      .then((res) => {
        setCart(res.data.data);
      })
      .catch((err) => {
        if (err.response.data.message === "만료된 토큰입니다.") {
          refreshToken();
        } else if (err.response.data.code === 403) {
          setIsModalOpen(true)
        } else {
          console.log(err);
        }
      });
  };

  return (
    <>
      {border ? (
        <CartBox isDark={isDark} onClick={() => handlePahctCart()}>
          <CartImage isCart={isCart} /> {content}
        </CartBox>
      ) : (
        <CartImage isCart={isCart} onClick={() => handlePahctCart()} />
      )}
      <AlertModal
        isModalOpen={isModalOpen}
        setIsModalOpen={setIsModalOpen}
        isBackdropClickClose={true}
        content="로그인 시 이용 가능합니다."
        buttonTitle="확인"
        handleButtonClick={() => setIsModalOpen(false)}
      />
    </>
  );
};

export default AddCart;

const globalTokens = tokens.global;

export const CartImage = styled(Cart)`
  width: 20px;
  height: 20px;
  cursor: pointer;
  path {
    fill: ${(props) => (props.isCart ? "#ffe072" : "rgb(220,220,220)")};
    /* stroke: ${(props) => (props.isCart ? "black" : "black")}; */
  }
  &:hover {
    path {
      stroke: black;
    }
  }
  &:active {
    path {
      fill: #ffc700;
    }
  }
`;

export const CartBox = styled(RoundButton)`
  border-radius: 18px 0px 0px 18px;
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
