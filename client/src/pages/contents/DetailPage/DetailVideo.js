import { styled } from "styled-components";
import SubscribeBtn from "../../../components/DetailPage/SubscribeBtn";

export const VideoContainer = styled.section`
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: start;
  flex-wrap: wrap;

  padding: 20px;
  margin-bottom: 20px;
  background-color: white;
  border: 1px solid red;
`;

export const VideoHeader = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: end;
  align-items: center;
  width: 100%;

  font-size: small;
  color: gray;
  font-size: 16px;
`;

export const HeaderBtn = styled.button`
  background: none;
  font-size: small;
  color: red;
  font-size: 16px;
  margin-left: 10px;
`;

export const VideoWindow = styled.div`
  background-color: rgb(230, 230, 230);
  width: 100%;
  height: 500px;
`;
export const VideoTitle = styled.h2`
  margin: 10px 0px;
`;

export const VideoInfo = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: center;
  margin-top: 5px;
`;

export const Profile = styled.div`
  display: flex;
  flex-direction: row;
  /* width: ; */
  justify-content: center;
  align-items: center;
  margin-right: 50px;

  /* border: 1px solid black; */
`;

export const ProfileImg = styled.img`
  width: 50px;
  height: 50px;
  margin-right: 10px;
  border-radius: 50%;

  /* border: 1px solid gray; */
`;

export const ProfileRight = styled.div``;

export const ProfileName = styled.div``;

export const Subscribed = styled.div`
  font-size: small;
  color: gray;
`;

const DetailVideo = () => {
  return (
    <VideoContainer>
      <VideoHeader>
        강의를 다 들었다면?
        <HeaderBtn>문제 풀러가기 →</HeaderBtn>
      </VideoHeader>

      <VideoWindow />

      <VideoTitle>React 서버 통신에 회의가 든다면 - RTK Query</VideoTitle>

      <VideoInfo>
        <Profile>
          <ProfileImg src="../logo512.png" alt="프로필 이미지" />

          <ProfileRight>
            <ProfileName>코딩생활</ProfileName>
            <Subscribed>구독자 33.6만명</Subscribed>
          </ProfileRight>
        </Profile>
        
        <SubscribeBtn />
      </VideoInfo>
    </VideoContainer>
  );
};

export default DetailVideo;
