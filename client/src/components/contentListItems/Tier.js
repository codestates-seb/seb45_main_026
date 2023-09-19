import React,{useState} from "react";
import { styled } from "styled-components";
import { useSelector } from "react-redux";
import tokens from "../../styles/tokens.json";
import BRONZE from "../../assets/images/icons/tier/bronze.svg"
import SILVER from "../../assets/images/icons/tier/silver.svg";
import GOLD from "../../assets/images/icons/tier/gold.svg";
import PLATINUM from "../../assets/images/icons/tier/platinum.svg";
import DIAMOND from "../../assets/images/icons/tier/diamond.svg";

const globalTokens = tokens.global;

const TierContainer = styled.div`
    position: relative;
`
const TierImage = styled.img`
    height: 25px;
    object-fit: contain;
    margin-top: ${globalTokens.Spacing4.value}px;
`
const ModalBody = styled.div`
    position: absolute;
    display: flex;
    flex-direction: column;
    align-items: center;
    left: 180%;
    top: -15px;
    width: 270px;
    height: 180px;
    border: ${globalTokens.ThinHeight.value}px solid ${globalTokens.LightGray.value};
    border-radius: ${globalTokens.BigRadius.value}px;
    color: ${(props)=>props.isDark ? globalTokens.White.value : globalTokens.Black.value};
    background-color: ${(props) =>
    props.isDark ? globalTokens.Black.value : globalTokens.White.value};
    padding: ${globalTokens.Spacing12.value}px;
    z-index: 1;
`
const ModalItem = styled.div`
    height: 35px;
    width: 100%;
    display: flex;
    align-items: center;
    gap: ${globalTokens.Spacing8.value}px;
`
const TierName = styled.span`
    font-size: ${globalTokens.BodyText.value}px;
    width: 80px;
`
const TierDescription = styled.span`
    font-size: ${globalTokens.SmallText.value}px;
    font-weight: 500;
    margin-left: ${globalTokens.Spacing4.value}px;
`

export default function Tier({ tier }) {
    const tiers = { BRONZE, SILVER, GOLD, PLATINUM, DIAMOND };
    const isDark = useSelector((state) => state.uiSetting.isDark);
    const [isOpen, setIsOpen] = useState(false)
    const blurHandler = () => {
        setIsOpen(false)
    }
    return (
      <TierContainer>
        <button onClick={()=>setIsOpen(!isOpen)} onBlur={blurHandler}>
            <TierImage src={tiers[tier]}/>
        </button>
        {isOpen&&
        <ModalBody isDark={isDark}>
          <ModalItem>
            <TierImage src={BRONZE} />
            <TierName>Bronze</TierName>
          </ModalItem>
          <ModalItem>
            <TierImage src={SILVER} />
            <TierName>Silver</TierName>
            <TierDescription>누적 포인트 100 이상</TierDescription>
          </ModalItem>
          <ModalItem>
            <TierImage src={GOLD} />
            <TierName>Gold</TierName>
            <TierDescription>누적 포인트 1000 이상</TierDescription>
          </ModalItem>
          <ModalItem>
            <TierImage src={PLATINUM} />
            <TierName>Platinum</TierName>
            <TierDescription>누적 포인트 3000 이상</TierDescription>
          </ModalItem>
          <ModalItem>
            <TierImage src={DIAMOND} />
            <TierName>Diamond</TierName>
            <TierDescription>누적 포인트 10000 이상</TierDescription>
          </ModalItem>
        </ModalBody>}
      </TierContainer>
    );
}