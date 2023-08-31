import { styled } from "styled-components";
import SubscribeBtn from "../../../components/DetailPage/SubscribeBtn";
import {
  RegularRedButton,
  RegularNavyButton,
} from "../../../atoms/buttons/Buttons";

const DetailVideo = () => {
  return (
    <VideoContainer>
      <VideoHeader>
        강의를 다 들었다면?
        <HeaderBtn>문제 풀러가기 →</HeaderBtn>
      </VideoHeader>

      <VideoWindow>
        <VideoCover>
          <PrevBtn>1분 미리보기</PrevBtn>
          <PurchaseBtn>구매하러 가기</PurchaseBtn>
        </VideoCover>
      </VideoWindow>

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

export const VideoContainer = styled.section`
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: start;
  flex-wrap: wrap;

  padding: 50px 50px 30px 50px;
  margin-bottom: 20px;
  background-color: white;
`;

export const VideoCover = styled.div`
  position: absolute;
  background-color: rgb(100, 100, 100, 90%);
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
`;

export const PrevBtn = styled(RegularRedButton)`
  margin: 20px;
  padding: 5px 10px;
`;

export const PurchaseBtn = styled(RegularNavyButton)`
  margin: 20px;
  padding: 5px 10px;
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
  position: relative;
  background-color: rgb(230, 230, 230);
  width: 100%;
  /* height: 500px; */
  aspect-ratio: 1.8/1;
  margin-top: 5px;
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
  justify-content: center;
  align-items: center;
  margin-right: 50px;
`;

export const ProfileImg = styled.img`
  width: 50px;
  height: 50px;
  margin-right: 10px;
  border-radius: 50%;
`;

export const ProfileRight = styled.div``;
export const ProfileName = styled.div``;

export const Subscribed = styled.div`
  font-size: small;
  color: gray;
`;
