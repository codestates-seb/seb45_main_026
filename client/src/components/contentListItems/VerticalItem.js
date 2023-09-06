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

export default function VerticalItem({ lecture ,channel}) { 
  const {videoName,thumbnailUrl,createdDate,isPurchased,price,star}=lecture
  const date = new Date(createdDate);
  date.setHours(date.getHours() + 9);
  const month = (date.getMonth() + 1).toString().padStart(2, "0"); // 월은 0부터 시작하므로 +1
  const day = date.getDate().toString().padStart(2, "0");
    return (
      <ComponentBody>
        <ThumbnailContainer>
          <Thumbnail src={thumbnailUrl} />
        </ThumbnailContainer>
        <ItemTitle>{videoName}</ItemTitle>
        <ItemInfors>
          <InforContainerLeft>
            <AuthorInfor>
              <ImgContainer>
                <ProfileImg src={channel.imageUrl} />
              </ImgContainer>
              <AuthorName>{channel.channelName}</AuthorName>
            </AuthorInfor>
            <DateInfor>{month}월{day}일 업로드됨</DateInfor>
          </InforContainerLeft>
          <InforContainerRight>
            <ScoreContainer>
              <ScoreText>{star}</ScoreText>
              <StarImage src={yellowStar} />
              <StarImage src={yellowStar} />
              <StarImage src={yellowStar} />
              <StarImage src={yellowStar} />
              <StarImage src={yellowStar} />
            </ScoreContainer>
            {isPurchased ? (
              <PriceInfor>구매됨</PriceInfor>
            ) : (
              <PriceInfor>{price}원</PriceInfor>
            )}
          </InforContainerRight>
        </ItemInfors>
      </ComponentBody>
    );
}