import React, { useState } from "react";
import tokens from "../../styles/tokens.json";
import { styled } from "styled-components";
import profileGray from "../../assets/images/icons/profile/profileGray.svg";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import {
  BodyTextTypo,
  Heading5Typo,
  SmallTextTypo,
} from "../../atoms/typographys/Typographys";
import Stars from "./Stars";
import AddCart from "../DetailPage/AddCart";
import VideoPlayer from "../DetailPage/VideoPlayer";
import ListToggle from "../../atoms/buttons/ListToggle";

export default function HorizonItem({ lecture, channel }) {
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const myId = useSelector((state) => state.loginInfo.myid);
  const {
    videoName,
    thumbnailUrl,
    createdDate,
    isPurchased,
    isInCart,
    price,
    star,
    description,
    videoId,
    videoStatus,
  } = lecture;
  const navigate = useNavigate();
  const date = new Date(createdDate);
  date.setHours(date.getHours() + 9);
  const month = (date.getMonth() + 1).toString().padStart(2, "0");
  const day = date.getDate().toString().padStart(2, "0");
  const ChannelNavigateHandler = (memberId) => {
    if (memberId !== null) {
      navigate(`/channels/${memberId}`);
    }
  };
  const [isPrevMode, setPrevMode] = useState(false);

  return (
    <ComponentBody isDark={isDark}>
      <ThumbnailContainer
        onClick={() => navigate(`/videos/${lecture.videoId}`)}
      >
        {isPrevMode ? (
          <VideoPlayer
            videoId={videoId}
            thumbnailUrl={thumbnailUrl}
            Playing={true}
            muted={true}
            isPrevMode={true}
            controlBar={false}
            onMouseOut={() => {
              setPrevMode(false);
            }}
          />
        ) : (
          <Thumbnail
            src={thumbnailUrl}
            alt="thumbnail"
            onMouseOver={() => {
              setPrevMode(true);
            }}
          />
        )}
      </ThumbnailContainer>
      <ItemInfors>
        <Title isDark={isDark}>
          <VideoName
            isDark={isDark}
            onClick={() => {
              navigate(`/videos/${lecture.videoId}`);
            }}
          >
            {videoName}
          </VideoName>
          {myId === channel.memberId && (
            <ListToggle OnOff={videoStatus !== "CREATED"} videoId={lecture.videoId}/>
          )}
          {!isPurchased && price > 0 && myId !== channel.memberId && (
            <AddCart videoId={videoId} isInCart={isInCart} />
          )}
        </Title>
        <Description isDark={isDark}>{description}</Description>
        <InforContainer>
          <InforContainerLeft>
            <AuthorContainer>
              <ImgContainer
                onClick={() => ChannelNavigateHandler(channel.memberId)}
              >
                <ProfileImg
                  src={channel.imageUrl ? channel.imageUrl : profileGray}
                  alt="profile"
                />
              </ImgContainer>
              <AuthorName
                isDark={isDark}
                onClick={() => ChannelNavigateHandler(channel.memberId)}
              >
                {channel.channelName}
              </AuthorName>
              <CreatedAt isDark={isDark}>
                {month}월{day}일 업로드됨
              </CreatedAt>
            </AuthorContainer>
          </InforContainerLeft>
          <InforContainerRight>
            {isPurchased ? (
              <PriceText isDark={isDark}>구매됨</PriceText>
            ) : isPurchased === false ? (
              <PriceText isDark={isDark}>
                {price > 0
                  ? `${price
                      .toString()
                      .replace(/\B(?=(\d{3})+(?!\d))/g, ",")}원`
                  : "무료"}
              </PriceText>
            ) : (
              <></>
            )}
            <ScoreContainer>
              <ScoreText isDark={isDark}>{star}/10</ScoreText>
              <StarContainer>
                <Stars score={star} />
              </StarContainer>
            </ScoreContainer>
          </InforContainerRight>
        </InforContainer>
      </ItemInfors>
    </ComponentBody>
  );
}

const globalTokens = tokens.global;

const ComponentBody = styled.li`
  width: 95%;
  display: flex;
  flex-direction: row;
  align-items: center;
  border-top: 1px solid
    ${(props) =>
      props.isDark
        ? globalTokens.LightGray.value
        : globalTokens.LightGray.value};
  margin: ${globalTokens.Spacing4.value}px 0;
  padding-top: ${globalTokens.Spacing24.value}px;
`;
const ThumbnailContainer = styled.div`
  width: 250px;
  min-width: 250px;
  height: 170px;
  border-radius: ${globalTokens.RegularRadius.value}px;
  background-color: ${globalTokens.White.value};
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  &:hover {
    cursor: pointer;
  }
`;
const Thumbnail = styled.img`
  object-fit: cover;
  height: 100%;
  width: 100%;
  background-color: lightgray;
`;
const ItemInfors = styled.div`
  flex-grow: 1;
  height: 170px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: ${globalTokens.Spacing8.value}px;
  border-radius: 0px ${globalTokens.RegularRadius.value}px
    ${globalTokens.RegularRadius.value}px 0px;
`;
const Title = styled.div`
  width: 100%;
  display: flex;
  justify-content: space-between;
`;

const VideoName = styled(Heading5Typo)`
  height: 40px;
  padding: ${globalTokens.Spacing4.value}px;
  overflow: hidden;
  &:hover {
    cursor: pointer;
  }
`;

const Description = styled(BodyTextTypo)`
  width: 100%;
  height: 50px;
  overflow: hidden;
  font-size: ${globalTokens.BodyText.value}px;
  padding: ${globalTokens.Spacing4.value}px;
`;
const InforContainer = styled.div`
  width: 100%;
  /* min-width: 450px; */
  height: 70px;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: end;
  overflow: hidden;
`;
const InforContainerLeft = styled.div`
  height: 70px;
  display: flex;
  flex-direction: column;
  justify-content: end;
  align-items: start;
`;
const InforContainerRight = styled.div`
  height: 70px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: end;
  @media (max-width: 800px) {
    display: none;
  }
`;
const AuthorContainer = styled.div`
  height: 50px;
  max-width: 500px;
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: ${globalTokens.Spacing4.value}px;
`;
const ProfileImg = styled.img`
  object-fit: cover;
  height: 100%;
  width: 100%;
`;
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
  &:hover {
    cursor: pointer;
  }
`;

const AuthorName = styled(BodyTextTypo)`
  height: 30px;
  font-weight: ${globalTokens.Bold.value};
  font-size: ${globalTokens.BodyText.value}px;
  margin-right: ${globalTokens.Spacing8.value}px;
  &:hover {
    cursor: pointer;
  }
`;
const CreatedAt = styled(SmallTextTypo)`
  height: 20px;
  font-size: ${globalTokens.SmallText.value}px;
`;
const ScoreContainer = styled.div`
  flex-grow: 1;
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: ${globalTokens.Spacing4.value}px;
`;
const ScoreText = styled(SmallTextTypo)`
  font-size: ${globalTokens.BodyText.value}px;
  padding-top: ${globalTokens.Spacing4.value}px;
  height: 30px;
`;
const StarContainer = styled.div`
  height: 24px;
  width: 120px;
  position: relative;
`;
const PriceText = styled(Heading5Typo)`
  font-size: ${globalTokens.Heading5.value}px;
  font-weight: ${globalTokens.Bold.value};
  height: 30px;
  min-width: 100px;
  display: flex;
  justify-content: end;
  align-items: center;
  gap: 4px;
  padding: 0px 5px 5px 0px;
`;
