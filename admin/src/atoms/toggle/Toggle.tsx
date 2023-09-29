import styled from "styled-components";
import tokens from '../../styles/tokens.json';
import { useSelector } from "react-redux";
import { RootState } from "../../redux/Store";

const globalTokens = tokens.global;

export const ToggleWrapper = styled.div`
    cursor: pointer;
    width: fit-content;
`
export const ToggleContainer = styled.div<{isDark:boolean}>`
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
export const ToggleCircle = styled.div<{isOn:boolean}>`
  position: absolute;
  top: 3px;
  left: 3px;
  border-radius: ${globalTokens.CircleRadius.value}%;
  background-color: ${globalTokens.White.value};
  width: 18px;
  height: 18px;
  transform: ${(props) => props.isOn && "translateX(24px)"};
  transition: 300ms;
`;

export type togglePropsType = {
    isOn: boolean;
    setIsOn: React.Dispatch<React.SetStateAction<boolean>>;
}

export const Toggle = ({isOn, setIsOn}:togglePropsType) => {
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);

    return (
        <ToggleWrapper onClick={()=>{ setIsOn(!isOn) }}>
            <ToggleContainer isDark={isDark}>
                <ToggleCircle isOn={isOn}/>
            </ToggleContainer>
        </ToggleWrapper>
    );
}