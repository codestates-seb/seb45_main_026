import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import { useDispatch, useSelector } from "react-redux";
import { setIsDark } from "../../redux/createSlice/UISettingSlice";

const globalTokens = tokens.global;

export const ToggleWrapper = styled.div`
  cursor: pointer;
`;
export const ToggleContainer = styled.div`
  position: relative;
  top: 0;
  left: 0;
  background-color: ${(props) =>
    props.isDark ? "rgba(255,255,255,0.15)" : "rgba(205,5,5,0.25)"};
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
  transform: ${(props) => props.isDark && "translateX(24px)"};
  transition: 300ms;
`;

const Toggle = () => {
  const dispatch = useDispatch();
  const isDark = useSelector((state) => state.uiSetting.isDark);

  const handleToggleClick = () => {
    dispatch(setIsDark(!isDark));
  };

  return (
        <ToggleWrapper onClick={handleToggleClick}>
            <ToggleContainer 
                isDark={isDark}>
                <ToggleCircle 
                    isDark={isDark}/>
            </ToggleContainer>
        </ToggleWrapper>
    );
};

export default Toggle;
