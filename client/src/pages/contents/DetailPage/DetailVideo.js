import { styled } from "styled-components";
import SubscribeBtn from "../../../components/DetailPage/SubscribeBtn";
import {
  RegularRedButton,
  RegularNavyButton,
} from "../../../atoms/buttons/Buttons";
import { useDispatch, useSelector } from "react-redux";
import { setPrev } from "../../../redux/createSlice/VideoInfoSlice";
import { Link } from "react-router-dom";

const DetailVideo = () => {
  const dispatch = useDispatch();
  const videoDatas = useSelector((state) => state.videoInfo.data);
  console.log(videoDatas);

  return (
    <VideoContainer>
      <VideoHeader>
        강의를 다 들었다면?
        <Link to="/videos/1/problems">
          <HeaderBtn>문제 풀러가기 →</HeaderBtn>
        </Link>
      </VideoHeader>

      {videoDatas.isPurchased ? (
        <VideoWindow
          src={videoDatas.videoUrl}
          controls
          loop
          muted
          autoPlay={false}
        />
      ) : (
        <VideoCover>
          <PrevBtn onClick={() => dispatch(setPrev(true))}>
            1분 미리보기
          </PrevBtn>
          <PurchaseBtn>구매하러 가기</PurchaseBtn>
        </VideoCover>
      )}

      <VideoTitle>{videoDatas.videoName}</VideoTitle>

      <VideoInfo>
        <Profile>
          <ProfileImg src={videoDatas.channel.imageUrl} alt="프로필 이미지" />

          <ProfileRight>
            <ProfileName>{videoDatas.channel.channelName}</ProfileName>
            <Subscribed>구독자 {videoDatas.channel.subscribes}명</Subscribed>
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
  background-color: rgb(100, 100, 100, 90%);
  width: 100%;
  aspect-ratio: 1.8/1;
  margin-top: 5px;
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

  font-size: 16px;
  color: gray;
`;

export const HeaderBtn = styled.button`
  font-size: 16px;
  color: red;
  margin-left: 10px;
`;

export const VideoWindow = styled.video`
  position: relative;
  width: 100%;
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
