import { styled } from "styled-components";
import tokens from "../../../styles/tokens.json";

const globalTokens = tokens.global;

export const ToggleWrapper = styled.div`
  cursor: pointer;
  position: absolute;
  bottom: 4%;
  left: 5%;
`;
export const ToggleContainer = styled.div`
  position: relative;
  top: 0;
  left: 0;
  background-color: ${(props) =>
    props.isMode ? "rgba(24,35,51,0.7)" : "rgba(205,5,5,0.25)"};
  border-radius: ${globalTokens.RegularRadius.value}px;
  width: 110px;
  height: 28px;
  padding: ${globalTokens.Spacing4.value}px ${globalTokens.Spacing12.value}px;
  box-shadow: ${globalTokens.RegularShadow.value.x}px
    ${globalTokens.RegularShadow.value.y}px
    ${globalTokens.RegularShadow.value.blur}px
    ${globalTokens.RegularShadow.value.spread}px
    ${(props) => (props.isMode ? "rgba(255,255,255,0.15)" : "rgba(0,0,0,0.15)")};
`;
export const ToggleCircle = styled.div`
  position: absolute;
  top: 3px;
  left: 3px;
  font-size: 14px;
  font-weight: 600;
  text-align: center;
  border-radius: ${globalTokens.RegularRadius.value}px;
  background-color: ${globalTokens.White.value};
  width: 55px;
  height: 22px;
  transform: ${(props) => props.isMode && "translateX(48px)"};
  transition: 300ms;
`;

const SelectToggle = ({ selectMode, setSelectMode, initProblem }) => {
  return (
    <ToggleWrapper
      onClick={() => {
        setSelectMode(!selectMode);
        initProblem();
      }}
    >
      <ToggleContainer isMode={!selectMode}>
        <ToggleCircle isMode={!selectMode}>
          {selectMode ? "객관식" : "주관식"}
        </ToggleCircle>
      </ToggleContainer>
    </ToggleWrapper>
  );
};

export default SelectToggle;
