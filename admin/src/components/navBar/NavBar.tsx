import React, { useState } from "react";
import styled from "styled-components";
import NavButton from "./NavButton";
import tokens from "../../styles/tokens.json";
import { useSelector } from "react-redux";
import { RootState } from "../../redux/Store";
import { useNavigate } from "react-router-dom";

const globalTokens = tokens.global;

export type Nav = {
  NavType: string;
};

const NavBar: React.FC<Nav> = ({ NavType }) => {
  const navigate = useNavigate();
  const isDark = useSelector((state: RootState) => state.uiSetting.isDark);
  const [selected, setSelected] = useState<string>(NavType);

  const handleNavButtonClick = (category: string): void => {
    setSelected(category);

    switch (category) {
      case "비디오":
        navigate("/reports/videos");
        return;

      case "댓글":
        navigate("/reports/reviews");
        return;

      case "채널":
        navigate("/reports/channels");
        return;

      case "공지사항":
        navigate("/reports/notices");
        return;

      default:
        return;
    }
  };

  return (
    <NavBarContainer isDark={isDark}>
      <NavButton
        text="비디오"
        isSelected={selected === "비디오"}
        handleNavButtonClick={handleNavButtonClick}
      />
      <NavButton
        text="댓글"
        isSelected={selected === "댓글"}
        handleNavButtonClick={handleNavButtonClick}
      />
      <NavButton
        text="채널"
        isSelected={selected === "채널"}
        handleNavButtonClick={handleNavButtonClick}
      />
      <NavButton
        text="공지사항"
        isSelected={selected === "공지사항"}
        handleNavButtonClick={handleNavButtonClick}
      />
    </NavBarContainer>
  );
};

const NavBarContainer = styled.nav<{ isDark: boolean }>`
  width: 95%;
  margin: 10px 0px;
  box-shadow: 0 -2px 0 ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value} inset;
  display: flex;
  flex-direction: row;
`;

export default NavBar;
