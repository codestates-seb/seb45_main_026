import { useState } from "react";
import { useSelector } from "react-redux";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import axios from "axios";
import { AlertModal } from "../modal/Modal";

const ListToggle = ({ OnOff, videoId }) => {
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const token = useSelector((state) => state.loginInfo.accessToken);
  const [isOnOff, setOnOff] = useState(OnOff);
  const [isModalOpen, setIsModalOpen] = useState({
    isModalOpen: false,
    content: "",
  });

  const patchVideoStatus = () => {
    return axios
      .patch(`https://api.itprometheus.net/videos/${videoId}/status`, null, {
        headers: { Authorization: token.authorization },
      })
      .then((res) => {
        setOnOff(!res.data.data);
        if (res.data.data) {
          setIsModalOpen({
            ...isModalOpen,
            isModalOpen: true,
            content: "강의를 활성화 했습니다.",
          });
        } else {
          setIsModalOpen({
            ...isModalOpen,
            isModalOpen: true,
            content: "강의를 비활성화 했습니다.",
          });
        }
      })
      .catch((err) => {
        setIsModalOpen({
          ...isModalOpen,
          isModalOpen: true,
          content: "강의 비활성화를 실패했습니다.",
        });
      });
  };

  return (
    <>
      <ToggleWrapper
        onClick={() => {
          patchVideoStatus();
        }}
      >
        <ToggleContainer isDark={isDark} isOnOff={isOnOff}>
          <ToggleCircle isOnOff={isOnOff} />
        </ToggleContainer>
      </ToggleWrapper>
      <AlertModal
        isModalOpen={isModalOpen.isModalOpen}
        setIsModalOpen={setIsModalOpen}
        isBackdropClickClose={false}
        content={isModalOpen.content}
        buttonTitle="확인"
        handleButtonClick={() =>
          setIsModalOpen({
            ...isModalOpen,
            isModalOpen: false,
          })
        }
      />
    </>
  );
};

export default ListToggle;

const globalTokens = tokens.global;

export const ToggleWrapper = styled.div`
  cursor: pointer;
  @media (max-width: 700px) {
    display: none;
  }
`;
export const ToggleContainer = styled.div`
  position: relative;
  top: 0;
  left: 0; // "rgba(24,35,51,0.7)"
  background-color: ${(props) =>
    props.isDark && !props.isOnOff
      ? globalTokens.LightNavy.value
      : !props.isDark && !props.isOnOff
      ? globalTokens.Negative.value
      : props.isDark && props.isOnOff
      ? "rgba(255,255,255,0.1)"
      : "rgba(0,0,0,0.15)"};
  border-radius: ${globalTokens.BigRadius.value}px;
  width: 48px;
  height: 24px;
  padding: ${globalTokens.Spacing4.value}px ${globalTokens.Spacing12.value}px;
  box-shadow: ${globalTokens.RegularShadow.value.x}px
    ${globalTokens.RegularShadow.value.y}px
    ${globalTokens.RegularShadow.value.blur}px
    ${globalTokens.RegularShadow.value.spread}px
    ${(props) => (props.isDark ? "rgba(255,255,255,0.15)" : "rgba(0,0,0,0.15)")};
`;
export const ToggleCircle = styled.div`
  position: absolute;
  top: 3px;
  left: 3px;
  border-radius: ${globalTokens.CircleRadius.value}%;
  background-color: ${globalTokens.White.value};
  width: 18px;
  height: 18px;
  transform: ${(props) => props.isOnOff && "translateX(24px)"};
  transition: 300ms;
`;
