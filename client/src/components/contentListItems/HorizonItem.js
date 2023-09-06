import React from "react";
import tokens from "../../styles/tokens.json";
import { styled } from "styled-components";
import yellowStar from "../../assets/images/icons/star/starYellow.svg"

const globalTokens = tokens.global;

const ComponentBody = styled.li`
    width: 100%;
    height: 200px;
    display: flex;
    flex-direction: row;
    border-radius: ${globalTokens.RegularRadius.value}px;
`
const ThumbnailContainer = styled.div`
    width: 300px;
    min-width: 300px;
    height: 200px;
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
    background-color: lightgray;
`
const ItemInfors = styled.div`
    flex-grow: 1;
    height: 200px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    padding: ${globalTokens.Spacing8.value}px;
    background-color: ${globalTokens.White.value};
    border-radius: 0px ${globalTokens.RegularRadius.value}px ${globalTokens.RegularRadius.value}px 0px;

`
const Title = styled.h3`
    width: 100%;
    height: 60px;
    font-size: ${globalTokens.Heading5.value}px;
    padding: ${globalTokens.Spacing4.value}px;
`
const Description = styled.div`
    width: 100%;
    height: 50px;
    font-size: ${globalTokens.BodyText.value}px;
    padding: ${globalTokens.Spacing4.value}px;
`
const InforContainer = styled.div`
    width: 100%;
    min-width: 450px;
    height: 70px;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: end;
    overflow: hidden;
`
const InforContainerLeft = styled.div`
    height: 70px;
    display: flex;
    flex-direction: column;
    justify-content: end;
    align-items: start;
`
const InforContainerRight = styled.div`
    height: 70px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: end;
    gap: ${globalTokens.Spacing12.value}px;
`
const AuthorContainer = styled.div`
    height: 50px;
    max-width: 500px;
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: ${globalTokens.Spacing4.value}px;
`
const ProfileImg = styled.img`
    max-height: 40px;
    height: auto;
    width: auto;
`
const ImgContainer = styled.div`
    width: 40px;
    min-width: 40px;
    height: 40px;
    border-radius: ${globalTokens.CircleRadius.value}px;
    background-color: ${globalTokens.White.value};
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;
    border: 1px solid lightgray;
`

const AuthorName = styled.span`
    height: 30px;
    font-weight: ${globalTokens.Bold.value};
    font-size: ${globalTokens.BodyText.value}px;
    margin-right: ${globalTokens.Spacing8.value}px;
`
const CreatedAt = styled.span`
    height: 20px;
    font-size: ${globalTokens.SmallText.value}px;
`
const StarImage = styled.img`
    height: 30px;
    width: 30px;
`
const ScoreContainer = styled.div`
    height: 30px;
    display: flex;
    flex-direction: row;
    align-items: center;
`
const ScoreText = styled.span`
    font-size: ${globalTokens.BodyText.value}px;
    padding-top: ${globalTokens.Spacing4.value}px;
    height: 30px;
`
const PriceText = styled.div`
    font-size: ${globalTokens.Heading5.value}px;
    font-weight: ${globalTokens.Bold.value};
    height: 30px;
`

export default function HorizonItem({lecture, channel}) {
  const {videoName,thumbnailUrl,createdDate,isPurchased,price,star,description}=lecture
  const date = new Date(createdDate);
  date.setHours(date.getHours() + 9);
  const month = (date.getMonth() + 1).toString().padStart(2, "0"); // 월은 0부터 시작하므로 +1
  const day = date.getDate().toString().padStart(2, "0");
    return (
      <ComponentBody>
        <ThumbnailContainer>
          <Thumbnail src={thumbnailUrl} alt="thumbnail" />
        </ThumbnailContainer>
        <ItemInfors>
          <Title>{videoName}</Title>
          <Description>{description}</Description>
          <InforContainer>
            <InforContainerLeft>
              <AuthorContainer>
                <ImgContainer>
                  <ProfileImg src={channel.imageUrl} alt="profile" />
                </ImgContainer>
                <AuthorName>{channel.channelName}</AuthorName>
                <CreatedAt>{month}월{day}일 업로드됨</CreatedAt>
              </AuthorContainer>
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
              {isPurchased?<PriceText>구매됨</PriceText>:<PriceText>{price}원</PriceText>}
            </InforContainerRight>
          </InforContainer>
        </ItemInfors>
      </ComponentBody>
    );
}