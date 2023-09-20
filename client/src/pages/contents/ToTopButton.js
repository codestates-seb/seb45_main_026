import React from "react";
import tokens from "../../styles/tokens.json";
import { styled } from "styled-components";
import { useSelector } from "react-redux";
import arrowUp from "../../assets/images/icons/arrow/arrowToUp.svg";
import { useMatch } from "react-router";
    
    
    
export default function ToTopButton() {
    const isDark = useSelector((state) => state.uiSetting.isDark);
    const match = useMatch("/");
    const scrollToTop = () => {
      window.scrollTo({
        top: 0,
        behavior: "smooth", // 부드러운 스크롤을 사용하려면 'smooth'를 지정합니다.
      });
    };

    return (

        <ToTopButtonContainer>
            {match===null?<ToButton isDark={isDark} onClick={()=>scrollToTop()}>
                <TopButtonImg src={arrowUp}/>
            </ToButton>:<></>}
        </ToTopButtonContainer>
    )
}

const globalTokens = tokens.global;

const ToTopButtonContainer = styled.div`
  width: 50px;
  height: 60vh;
  position: fixed;
  bottom: 5%;
  right: 2%;
  display: flex;
  flex-direction: column;
  justify-content: end;
`
const ToButton = styled.button`
  width: 50px;
  height: 50px;
  background-color: ${(props) =>
    props.isDark ? "rgba(255,255,255,0.15)" : globalTokens.White.value};
  border-radius: 50%;
`
const TopButtonImg = styled.img`
  height: 30px;
  object-fit: contain;
`