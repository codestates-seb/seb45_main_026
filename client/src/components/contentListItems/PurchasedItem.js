import React,{useState} from "react";
import tokens from "../../styles/tokens.json";
import { styled } from "styled-components";
import arrowDown from "../../assets/images/icons/arrow/subscribe_arrow_down.svg";
import arrowUp from "../../assets/images/icons/arrow/subscribe_arrow_up.svg";
import HorizonItem from "./HorizonItem";

const globalTokens = tokens.global;

const ItemBody = styled.div`
    width: 100%;
    padding: ${globalTokens.Spacing28.value}px;
    gap: ${globalTokens.Spacing28.value}px;
    border: 1px lightgray solid;
    border-radius: ${globalTokens.RegularRadius.value}px;
    display: flex;
    flex-direction: column;
`
const ProfileContainer = styled.div`
    height: 50px;
    display: flex;
    flex-direction: row;
    align-items: center;   
    gap: ${globalTokens.Spacing8.value}px;
`
const ProfileImg = styled.img`
    max-height: 50px;
    height: auto;
    width: auto;
`
const ImgContainer = styled.span`
    width: 50px;
    height: 50px;
    border-radius: ${globalTokens.CircleRadius.value}px;
    background-color: ${globalTokens.White.value};
    display: flex;
    justify-content: center;
    align-items: center;
    border: 1px solid lightgray;
    overflow: hidden;
`
const TextInfor = styled.div`
    height: 50px;
    display: flex;
    flex-direction: column;
    justify-content: center;
`
const AuthorName = styled.div`
    font-size: ${globalTokens.Heading5.value}px;
    font-weight: ${globalTokens.Bold.value};
`
const Subscribers = styled.div`
    font-size: ${globalTokens.BodyText.value}px;
`
const ContentContainer = styled.div`
    width: 100%;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    padding: 12px 0;
`
const TopContainer = styled.div`
    width: 100%;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
`
const LectureCount = styled.div`
    font-size: ${globalTokens.BodyText.value}px;
    display: flex;
`
const CountNum = styled.div`
    font-weight: ${globalTokens.Bold.value};
`
const AccordionButton = styled.button`
    width: 30px;
    height: 20px;
    background-image: url(${arrowDown});
    background-repeat: no-repeat;
    background-size: contain;
`
const HorizonItemContainer = styled.ul`
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: ${globalTokens.Spacing16.value}px;
    margin-bottom: ${globalTokens.Spacing28.value}px;
`


export default function PurchasedItem() {
    const [isOpen,setIsOpen]=useState(false)
    return (
        <ItemBody>
            <ProfileContainer>
            <ImgContainer>
                <ProfileImg src="https://avatars.githubusercontent.com/u/50258232?v=4" />
            </ImgContainer>
            <TextInfor>
                <AuthorName>HyerimKimm</AuthorName>
                <Subscribers>구독자 5명</Subscribers>
            </TextInfor>
            </ProfileContainer>
            <TopContainer> 
                <LectureCount><CountNum>5</CountNum>개의 강의</LectureCount>
                <AccordionButton onClick={()=>setIsOpen(!isOpen)}/>
            </TopContainer>
            <ContentContainer>
                {isOpen?<HorizonItemContainer><HorizonItem/><HorizonItem/><HorizonItem/></HorizonItemContainer>:<></>}
            </ContentContainer>
        </ItemBody>
    )
}