import axios from "axios";
import { styled } from "styled-components";
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import SubscribeBtn from "../../../components/DetailPage/SubscribeBtn";
import {
  RegularRedButton,
  RegularNavyButton,
  NegativeTextButton,
  RoundButton,
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
import AddCart from "../../../components/DetailPage/AddCart";
import VideoPlayer from "../../../components/DetailPage/VideoPlayer";

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
        if (res.data.code === 200) {
          setChannelInfo(res.data.data);
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };

  const handlePurchase = () => {
    return axios
      .post(
        `https://api.itprometheus.net/orders`,
        { reward: 0, videoIds: [`${videoId}`] },
        {
          headers: { Authorization: token.authorization },
        }
      )
      .then((res) => {
        alert("성공적으로 강의가 구매 되었습니다.");
        window.location.reload();
      })
      .catch((err) => {
        if (err.response.data.message === "만료된 토큰입니다.") {
          refreshToken();
        } else {
          console.log(err);
        }
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

  const handleNavProblem = () => {
    if (!videoDatas.isPurchased && myId !== videoDatas.channel.memberId) {
      alert("강의를 먼저 구매해주세요.");
    } else {
      navigate(`/videos/${videoId}/problems`);
    }
  };

  return (
    <VideoContainer isDark={isDark}>
      <VideoHeader isDark={isDark}>
        강의를 다 들었다면?
        <HeaderBtn isDark={isDark} onClick={handleNavProblem}>
          문제 풀러가기 →
        </HeaderBtn>
      </VideoHeader>

      <VideoPlayer
        videoId={videoId}
        thumbnailUrl={videoDatas.thumbnailUrl}
        handleVideo={() => {}}
      />
      {videoDatas.isPurchased || myId === videoDatas.channel.memberId ? (
        <VideoWindow
          src={videoDatas.videoUrl}
          controls={true}
          loop={false}
          muted={false}
          autoPlay={false}
        />
      ) : (
        <VideoCover url={videoDatas.thumbnailUrl}>
          <PrevBtn
            onClick={() => {
              dispatch(setPrev(true));
              setTimeout(() => {
                dispatch(setPrev(false));
              }, 60000);
            }}
          >
            1분 미리보기
          </PrevBtn>
          <PurchaseBtn
            onClick={() => {
              if (videoDatas.price > 0) {
                handleCartNav();
              } else {
                handlePurchase();
              }
            }}
          >
            강의 구매하기
          </PurchaseBtn>
        </VideoCover>
      )}

      <VideoTitle isDark={isDark}>
        <span>{videoDatas.videoName}</span>
        {!videoDatas.isPurchased && myId !== videoDatas.channel.memberId && (
          <span>{videoDatas.price ? `${videoDatas.price}원` : "무료"}</span>
        )}
      </VideoTitle>
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

        {myId === videoDatas.channel.memberId || (
          <SubscribeBtn
            memberId={videoDatas.channel.memberId}
            channelInfo={channelInfo}
            setSub={setSub}
          />
        )}

        {!videoDatas.isPurchased && myId !== videoDatas.channel.memberId && (
          <CreditBox>
            {videoDatas.price > 0 && (
              <AddCart
                videoId={videoId}
                isInCart={videoDatas.isInCart}
                content="장바구니"
                border={true}
              />
            )}
            <PurchaseNav
              isDark={isDark}
              isFree={!videoDatas.price}
              onClick={() => {
                if (videoDatas.price > 0) {
                  handleCartNav();
                } else {
                  handlePurchase();
                }
              }}
            >
              강의 구매하기
            </PurchaseNav>
          </CreditBox>
        )}
      </VideoInfo>
    </VideoContainer>
  );
};

export default DetailVideo;

export const PurchaseNav = styled(RoundButton)`
  border-radius: ${(props) =>
    props.isFree ? "18px 18px 18px 18px;" : "0px 18px 18px 0px;"};
  position: relative;
  display: flex;
  flex-direction: row;
  justify-content: space-around;
  align-items: center;
  width: 120px;
  height: 35px;
  background-color: rgba(255, 255, 255, 0);
  color: ${(props) =>
    props.isDark ? globalTokens.White.value : globalTokens.Black.value};
  &:hover {
    background-color: ${(props) =>
      props.isDark ? "rgba(255,255,255,0.15)" : "rgba(0,0,0,0.15)"};
    color: ${(props) =>
      props.isDark ? globalTokens.White.value : globalTokens.Black.value};
  }
`;

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
  position: relative;

  &::before {
    content: "";
    background-image: ${(props) => props.url && `url(${props.url})`};
    background-size: cover;
    background-repeat: no-repeat;
    background-position: center;
    opacity: 0.5;
    position: absolute;
    top: 0px;
    left: 0px;
    right: 0px;
    bottom: 0px;
  }
`;

export const PrevBtn = styled(RegularRedButton)`
  z-index: 10;
  margin: 20px;
  padding: 5px 10px;
`;

export const PurchaseBtn = styled(RegularNavyButton)`
  z-index: 10;
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
  width: 100%;
  aspect-ratio: 1.8/1;
  margin-top: 5px;
  background-color: black;
`;
export const VideoTitle = styled(Heading5Typo)`
  width: 100%;
  padding: 15px 5px 5px 0px;
  display: flex;
  justify-content: space-between;
`;

export const VideoInfo = styled.div`
  position: relative;
  width: 100%;
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

export const CreditBox = styled.div`
  position: absolute;
  display: flex;
  justify-content: start;
  align-items: center;
  right: 0%;
  bottom: 15%;
`;
