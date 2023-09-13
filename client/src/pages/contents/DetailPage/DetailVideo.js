import { styled } from "styled-components";
import axios from "axios";
import { useDispatch, useSelector } from "react-redux";
import { Link, useNavigate, useParams } from "react-router-dom";
import SubscribeBtn from "../../../components/DetailPage/SubscribeBtn";
import {
  RegularRedButton,
  RegularNavyButton,
  NegativeTextButton,
} from "../../../atoms/buttons/Buttons";
import { setPrev } from "../../../redux/createSlice/VideoInfoSlice";
import { useToken } from "../../../hooks/useToken";
import tokens from "../../../styles/tokens.json";
import {
  BodyTextTypo,
  Heading5Typo,
  SmallTextTypo,
} from "../../../atoms/typographys/Typographys";
import profileGray from "../../../assets/images/icons/profile/profileGray.svg";
import { useEffect, useState } from "react";

const globalTokens = tokens.global;

const DetailVideo = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const refreshToken = useToken();
  const { videoId } = useParams();
  const myId = useSelector((state) => state.loginInfo.myid);
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const videoDatas = useSelector((state) => state.videoInfo.data);
  const token = useSelector((state) => state.loginInfo.accessToken);
  const [isSub, setSub] = useState("");
  const [channelInfo, setChannelInfo] = useState({});

  useEffect(() => {
    getChannelInfo();
  }, [isSub]);

  const getChannelInfo = () => {
    return axios
      .get(
        `https://api.itprometheus.net/channels/${videoDatas.channel.memberId}`,
        {
          headers: { Authorization: token.authorization },
        }
      )
      .then((res) => {
        console.log(res.data.data);
        if (res.data.code === 200) {
          setChannelInfo(res.data.data);
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };

  const handleCartNav = () => {
    return axios
      .patch(`https://api.itprometheus.net/videos/${videoId}/carts`, null, {
        headers: { Authorization: token.authorization },
      })
      .then((res) => {
        if (res.data.data) {
          navigate("/carts");
        } else {
          handleCartNav();
        }
      })
      .catch((err) => {
        if (err.response.data.message === "만료된 토큰입니다.") {
          refreshToken();
        } else {
          console.log(err);
        }
      });
  };

  return (
    <VideoContainer isDark={isDark}>
      <VideoHeader isDark={isDark}>
        {videoDatas.isPurchased || myId === videoDatas.channel.memberId ? (
          <>
            강의를 다 들었다면?
            <Link to={`/videos/${videoId}/problems`}>
              <HeaderBtn isDark={isDark}>문제 풀러가기 →</HeaderBtn>
            </Link>
          </>
        ) : (
          <>
            강의를 듣고 싶다면?
            <HeaderBtn isDark={isDark} onClick={handleCartNav}>
              구매하러 가기 →
            </HeaderBtn>
          </>
        )}
      </VideoHeader>

      {videoDatas.isPurchased || myId === videoDatas.channel.memberId ? (
        <VideoWindow
          src={videoDatas.videoUrl}
          controls
          loop={false}
          muted
          autoPlay={false}
        />
      ) : (
        <VideoCover>
          <PrevBtn onClick={() => dispatch(setPrev(true))}>
            1분 미리보기
          </PrevBtn>
          <PurchaseBtn onClick={handleCartNav}>구매하러 가기</PurchaseBtn>
        </VideoCover>
      )}

      <VideoTitle isDark={isDark}>{videoDatas.videoName}</VideoTitle>

      <VideoInfo>
        <Profile>
          <ProfileImg
            src={videoDatas.channel.imageUrl || profileGray}
            alt="프로필 이미지"
          />

          <ProfileRight>
            <ProfileName isDark={isDark}>
              {videoDatas.channel.channelName}
            </ProfileName>
            <Subscribed isDark={isDark}>
              구독자 {channelInfo.subscribers}명
            </Subscribed>
          </ProfileRight>
        </Profile>

        <SubscribeBtn
          memberId={videoDatas.channel.memberId}
          channelInfo={channelInfo}
          setSub={setSub}
        />
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
  border-radius: ${globalTokens.RegularRadius.value}px;
  background-color: ${(props) =>
    props.isDark ? "rgba(255,255,255,0.15)" : globalTokens.White.value};
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
  color: ${(props) =>
    props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
`;

export const HeaderBtn = styled(NegativeTextButton)`
  margin-left: 10px;
`;

export const VideoWindow = styled.video`
  position: relative;
  width: 100%;
  aspect-ratio: 1.8/1;
  margin-top: 5px;
`;
export const VideoTitle = styled(Heading5Typo)`
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
export const ProfileName = styled(BodyTextTypo)``;

export const Subscribed = styled(SmallTextTypo)`
  color: ${(props) =>
    props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
`;
