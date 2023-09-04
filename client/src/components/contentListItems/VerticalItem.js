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
const ThumbnailContainer = styled.div`
    width: 250px;
    min-width: 250px;
    height: 160px;
    border-radius: ${globalTokens.RegularRadius.value}px;
    background-color: ${globalTokens.White.value};
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;
`
const Thumbnail = styled.img`
    object-fit: cover;
    height: 100%;
    width: 100%;
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
    width: 120px;
    height: 30px;
    overflow: hidden;
    display: flex;
    flex-direction: row;
    align-items: center;
`
const ProfileImg = styled.img`
    max-height: 24px;
    height: auto;
    width: auto;
`
const ImgContainer = styled.span`
    width: 24px;
    height: 24px;
    border-radius: ${globalTokens.CircleRadius.value}px;
    background-color: ${globalTokens.White.value};
    display: flex;
    justify-content: center;
    align-items: center;
    border: 1px solid lightgray;
    overflow: hidden;
`
const AuthorName = styled.span`
  height: 30px;
  font-size: ${globalTokens.SmallText.value}px;
  font-weight: ${globalTokens.Bold.value};
  padding-top: ${globalTokens.Spacing4.value}px;
  overflow: hidden;
  white-space: nowrap;
`;
const PriceInfor = styled.div`
    font-size: ${globalTokens.BodyText.value}px;
    font-weight: ${globalTokens.Bold.value};
`
const DateInfor = styled.div`
    font-size: ${globalTokens.SmallText.value}px;
`
const StarImage = styled.img`
    height: 20px;
    width: 20px;
`
const ScoreContainer = styled.div`
  display: flex;
  align-items: center;
  height: 24px;
`
const ScoreText = styled.span`
  font-size: ${globalTokens.SmallText.value}px;
`

export default function VerticalItem() { 
    return (
      <ComponentBody>
        <ThumbnailContainer>
          <Thumbnail src="https://cdn.inflearn.com/public/courses/329922/cover/364e7406-3569-437b-b719-7f146cad3d60/thumbnail-js.png" />
        </ThumbnailContainer>
        <ItemTitle>대충 영상 제목대충 영상 제목</ItemTitle>
        <ItemInfors>
          <InforContainerLeft>
            <AuthorInfor>
              <ImgContainer>
                <ProfileImg src="https://avatars.githubusercontent.com/u/50258232?v=4" />
              </ImgContainer>
              <AuthorName>HyerimKimm</AuthorName>
            </AuthorInfor>
            <DateInfor>8월 29일 업로드됨</DateInfor>
          </InforContainerLeft>
          <InforContainerRight>
            <ScoreContainer>
              <ScoreText>4.6</ScoreText>
              <StarImage src={yellowStar} />
              <StarImage src={yellowStar} />
              <StarImage src={yellowStar} />
              <StarImage src={yellowStar} />
              <StarImage src={yellowStar} />
            </ScoreContainer>
            <PriceInfor>32,450원</PriceInfor>
          </InforContainerRight>
        </ItemInfors>
      </ComponentBody>
    );
}