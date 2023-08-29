import React from "react";
import styled from "styled-components"
import tokens from "../../styles/tokens.json";
import yellowStar from "../../assets/images/icons/star/starYellow.svg"
import blankStar from "../../assets/images/icons/star/starWhite.svg"

const globalTokens = tokens.global;

const ComponentBody = styled.li`
    width: 270px;
    height: 300px;
    border-radius: ${globalTokens.RegularRadius.value}px;
    padding: ${globalTokens.Spacing8.value}px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: space-between;
    background-color: white;
`
const ImageContainer = styled.img`
    width: 250px;
    height: 160px;
    background-color: lightgray;
    border-radius: ${globalTokens.RegularRadius.value}px;
`
const ItemTitle = styled.h2`
    font-size: ${globalTokens.BodyText.value}px;
    width: 250px;
    height: 40px;
    font-weight: ${globalTokens.Bold.value};
`  
const ItemInfors = styled.div`
    width: 250px;
    height: 60px;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: end;
`
const InforContainerLeft = styled.div`
    height: 60px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: start;
`
const InforContainerRight = styled.div`
    height: 60px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: end;
`
const AuthorInfor = styled.div`
    max-width: 150px;
    font-size: ${globalTokens.SmallText.value}px;
    font-weight: ${globalTokens.Bold.value};
    overflow: hidden;
`
const PriceInfor = styled.div`
    font-size: ${globalTokens.BodyText.value}px;
    font-weight: ${globalTokens.Bold.value};
`
const DateInfor = styled.div`
    font-size: ${globalTokens.SmallText.value}px;
`
const DateBold = styled.span`
  font-weight: ${globalTokens.Bold.value};
`;
const StarImage = styled.img`
    height: 20px;
    width: 20px;
`

export default function VerticalItem() { 
    return (
      <ComponentBody>
        <ImageContainer />
        <ItemTitle>대충 영상 제목</ItemTitle>
        <ItemInfors>
          <InforContainerLeft>
            <AuthorInfor>스터디파이</AuthorInfor>
            <PriceInfor>32450원</PriceInfor>
          </InforContainerLeft>
          <InforContainerRight>
            <div>
              <StarImage src={yellowStar}/>
              <StarImage src={yellowStar}/>
              <StarImage src={yellowStar}/>
              <StarImage src={yellowStar}/>
              <StarImage src={yellowStar}/>
            </div>
            <DateInfor>
              <DateBold>8월 29일</DateBold> 업로드됨
            </DateInfor>
          </InforContainerRight>
        </ItemInfors>
      </ComponentBody>
    );
}