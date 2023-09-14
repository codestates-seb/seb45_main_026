import React from "react";
import styled from "styled-components"
import tokens from "../../styles/tokens.json";
import Stars from "./Stars";
import profileGray from "../../assets/images/icons/profile/profileGray.svg";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import { BodyTextTypo, SmallTextTypo } from "../../atoms/typographys/Typographys";

const globalTokens = tokens.global;

const ComponentBody = styled.li`
    width: 270px;
    height: 300px;
    border-radius: ${globalTokens.RegularRadius.value}px;
    padding: ${globalTokens.Spacing8.value}px;
    display: flex;
    flex-direction: column;
    justify-content: start;
    align-items: center;
    /* justify-content: space-between; */
`
const ThumbnailContainer = styled.div`
    width: 250px;
    min-width: 250px;
    height: 170px;
    margin-bottom: ${globalTokens.Spacing8.value}px;
    border-radius: ${globalTokens.RegularRadius.value}px;
    background-color: ${globalTokens.White.value};
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;
    &:hover{
      cursor: pointer;
    }
`
const Thumbnail = styled.img`
    object-fit: cover;
    height: 100%;
    width: 100%;
`
const ItemTitle = styled(BodyTextTypo)`
    width: 250px;
    /* height: 40px; */
    /* background-color: red; */
    font-weight: ${globalTokens.Bold.value};
    &:hover{
      cursor: pointer;
    }
`  
const ItemInfors = styled.div`
    width: 250px;
    height: 60px;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    align-items: end;
`
const InforContainerLeft = styled.div`
    height: 55px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: start;
`
const InforContainerRight = styled.div`
    height: 55px;
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
    object-fit: cover;
    height: 100%;
    width: 100%;
`
const ImgContainer = styled.span`
    width: 24px;
    height: 24px;
    border-radius: ${globalTokens.CircleRadius.value}px;
    background-color: ${globalTokens.White.value};
    display: flex;
    justify-content: center;
    align-items: center;
    border: 1px solid ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
    overflow: hidden;
    &:hover{
      cursor: pointer;
    }
`
const AuthorName = styled(SmallTextTypo)`
  height: 30px;
  font-weight: ${globalTokens.Bold.value};
  padding-top: ${globalTokens.Spacing4.value}px;
  overflow: hidden;
  white-space: nowrap;
  &:hover{
    cursor: pointer;
  }
`;
const PriceInfor = styled(BodyTextTypo)`
    font-weight: ${globalTokens.Bold.value};
`
const DateInfor = styled(SmallTextTypo)`
  color: ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
`
const ScoreContainer = styled.div`
  display: flex;
  align-items: center;
  height: 24px;
`
const ScoreText = styled(SmallTextTypo)`
`
const StarContainer = styled.div`
  height: 20px;
  width: 100px;
  position: relative;
`

export default function VerticalItem({ lecture ,channel}) { 
  const isDark = useSelector(state=>state.uiSetting.isDark);
  const {videoName,thumbnailUrl,createdDate,modifiedDate,isPurchased,price,star}=lecture
  const navigate=useNavigate()
  const date = new Date(modifiedDate||createdDate);
  date.setHours(date.getHours() + 9);
  const month = (date.getMonth() + 1).toString().padStart(2, "0"); // 월은 0부터 시작하므로 +1
  const day = date.getDate().toString().padStart(2, "0");
  const ChannelNavigateHandler = (memberId) => {
    if (memberId !== null) {
      navigate(`/channels/${memberId}`)
    }
  }
    return (
      <ComponentBody isDark={isDark}>
        <ThumbnailContainer
           isDark={isDark}
          onClick={() => navigate(`/videos/${lecture.videoId}`)}
        >
          <Thumbnail src={thumbnailUrl} />
        </ThumbnailContainer>
        <ItemTitle isDark={isDark} onClick={() => navigate(`/videos/${lecture.videoId}`)}>
          {videoName}
        </ItemTitle>
        <ItemInfors isDark={isDark}>
          <InforContainerLeft>
            <AuthorInfor isDark={isDark}>
              <ImgContainer
                isDark={isDark}
                onClick={() => ChannelNavigateHandler(channel.memberId)}
              >
                <ProfileImg
                  src={channel.imageUrl ? channel.imageUrl : profileGray}
                />
              </ImgContainer>
              <AuthorName
                isDark={isDark}
                onClick={() => ChannelNavigateHandler(channel.memberId)}>
                {channel.channelName}
              </AuthorName>
            </AuthorInfor>
            <DateInfor isDark={isDark}>{createdDate?`${month}월${day}일 업로드됨`:`${month}월${day}일 시청함`}</DateInfor>

          </InforContainerLeft>
          <InforContainerRight>
            <ScoreContainer>
              <ScoreText isDark={isDark}>{star}/10</ScoreText>
              <StarContainer>
                <Stars score={star} />
              </StarContainer>
            </ScoreContainer>
            {isPurchased ? (
              <PriceInfor isDark={isDark}>구매됨</PriceInfor>
            ) : (
              <PriceInfor isDark={isDark}>
                {price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",")}원
              </PriceInfor>
            )}
          </InforContainerRight>
        </ItemInfors>
      </ComponentBody>
    );
}