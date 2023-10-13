import React, { useState } from "react";
import tokens from "../../styles/tokens.json";
import { styled } from "styled-components";
import { useSelector } from "react-redux";
import arrowUp from "../../assets/images/icons/arrow/arrowToUp.svg";
import { useMatch } from "react-router";
import { RoundButton } from "../../atoms/buttons/Buttons";
import HelpCenter from "../../components/helpCenter/HelpCenter";

export default function ToTopButton() {
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const match = useMatch("/");
  const [isHelpClick, setIsHelpClick] = useState(false);

  const scrollToTop = () => {
    window.scrollTo({
      top: 0,
      behavior: "smooth", // 부드러운 스크롤을 사용하려면 'smooth'를 지정합니다.
    });
  };

  return (
    <>
      {match === null ? (
        <ToTopButtonContainer>
          {isHelpClick && (
            <HelpCenter
              isHelpClick={isHelpClick}
              setIsHelpClick={setIsHelpClick}
            />
          )}
          <HelpButton
            isDark={isDark}
            onClick={() => {
              setIsHelpClick(true);
            }}
          >
            고객센터
          </HelpButton>
          <ToButton isDark={isDark} onClick={() => scrollToTop()}>
            <TopButtonImg src={arrowUp} />
          </ToButton>
        </ToTopButtonContainer>
      ) : (
        <></>
      )}
    </>
  );
}

const globalTokens = tokens.global;

const ToTopButtonContainer = styled.div`
  height: 100vh;
  position: fixed;
  z-index: 1;
  bottom: 5%;
  right: 2%;
  display: flex;
  flex-direction: column;
  justify-content: end;
  align-items: center;
  gap: ${globalTokens.Spacing8.value}px;
`;
const ToButton = styled.button`
  width: 50px;
  height: 50px;
  background-color: ${(props) =>
    props.isDark ? "rgba(255,255,255,0.15)" : globalTokens.White.value};
  border-radius: 50%;
  box-shadow: ${globalTokens.RegularShadow.value.x}px
    ${globalTokens.RegularShadow.value.y}px
    ${globalTokens.RegularShadow.value.blur}px
    ${globalTokens.RegularShadow.value.spread}px
    ${(props) =>
      props.isDark
        ? "rgba(255,255,255,0.15)"
        : globalTokens.RegularShadow.value.color};
`;
const TopButtonImg = styled.img`
  height: 30px;
  object-fit: contain;
`;
const HelpButton = styled(RoundButton)`
  font-size: ${globalTokens.SmallText.value}px;
`;
