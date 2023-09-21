import axios from "axios";
import { styled } from "styled-components";
import { useEffect, useMemo, useState } from "react";
import { useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import SubscribeBtn from "../../../components/DetailPage/SubscribeBtn";
import {
  RegularRedButton,
  RegularNavyButton,
  NegativeTextButton,
  RoundButton,
} from "../../../atoms/buttons/Buttons";
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
import { AlertModal, ReportModal } from "../../../atoms/modal/Modal";
import { PositiveTextButton } from "../../../atoms/buttons/Buttons";
import { priceToString } from "../../../components/CartPage/CartPayInfo";

const DetailVideo = ({ videoDatas }) => {
  const navigate = useNavigate();
  const refreshToken = useToken();
  const { videoId } = useParams();
  const myId = useSelector((state) => state.loginInfo.myid);
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const token = useSelector((state) => state.loginInfo.accessToken);
  const [isSub, setSub] = useState("");
  const [channelInfo, setChannelInfo] = useState({});
  const [isPrevMode, setPrevMode] = useState(false);
  const [isPrevCover, setPrevCover] = useState(!videoDatas.isPurchased);
  const [purchaseModal, setPurchaseModal] = useState(false);
  const [alertModal, setAlertModal] = useState(false);
  const [reportedModal, setReportedModal] = useState(false);
  const [alreadyReportedModal, setAlreadyReportedModal] = useState(false);
  const [reportModal, setReportModal] = useState(false);
  const [reportContent,setReportContent] = useState("")
  const [alertLogin, setAlertLogin] = useState(false);

  const getVideoInfo = () => {
    return axios
      .get(`https://api.itprometheus.net/videos/${videoId}`, {
        headers: { Authorization: token.authorization },
      })
      .then((res) => {
        setChannelInfo({ ...channelInfo, ...res.data.data.channel });
        console.log(res.data.data.channel);
      })
      .catch((err) => {
        if (err.response.data.message === "만료된 토큰입니다.") {
          refreshToken();
        } else {
          console.log(err);
        }
      });
  };

  useMemo(() => {
    getVideoInfo();
  }, [isSub]);

  const handlePurchase = () => {
    if (!token.authorization) {
      return setAlertLogin(true);
    }
    return axios
      .post(
        `https://api.itprometheus.net/orders`,
        { reward: 0, videoIds: [`${videoId}`] },
        {
          headers: { Authorization: token.authorization },
        }
      )
      .then((res) => {
        setPurchaseModal(true);
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
    if (!token.authorization) {
      return setAlertLogin(true);
    }
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
    if (!token.authorization) {
      return setAlertLogin(true);
    }
    if (!videoDatas.isPurchased && myId !== videoDatas.channel?.memberId) {
      setAlertModal(true);
    } else {
      navigate(`/videos/${videoId}/problems`);
    }
  };


  const handleNavChannel = () => {
    return navigate(`/channels/${videoDatas.channel.memberId}`);
  };
    
  const handleReportVideo = () => {
    if (reportContent !== "") {
      axios.post(
        `https://api.itprometheus.net/videos/${videoId}/reports`,
        {
          reportContent: reportContent,
        },
        {
          headers: { Authorization: token.authorization },
        }
      ).then(res => {
        if (res.data.data) {
          setReportModal(false);
          setReportedModal(true);
        } else {
          setReportModal(false);
          setAlreadyReportedModal(true);
        }
      }).catch((err) => {
        if (err.response.data.message === "만료된 토큰입니다.") {
          refreshToken();
        } else {
          console.log(err);
        }
      })
    }
  }

  const reportContentHandler = (e) => {
    setReportContent(e.target.value)
  }

  return (
    <>
      <VideoContainer isDark={isDark}>
        <BackButton isDark={isDark} onClick={()=>navigate('/lecture')}>← 목록으로</BackButton>
        <VideoHeader isDark={isDark}>
          <HeaderBtnContainer>
            부적절한 영상인가요?
            <HeaderBtn isDark={isDark} onClick={() => setReportModal(true)}>
              신고하기
            </HeaderBtn>
          </HeaderBtnContainer>
          <HeaderBtnContainer>
            강의를 다 들었다면?
            <HeaderBtn isDark={isDark} onClick={handleNavProblem}>
              문제 풀러가기 →
            </HeaderBtn>
          </HeaderBtnContainer>
        </VideoHeader>

        {!isPrevCover ||
        videoDatas.isPurchased ||
        myId === videoDatas.channel?.memberId ? (
          <VideoPlayer
            videoId={videoId}
            thumbnailUrl={videoDatas.thumbnailUrl}
            isPrevMode={isPrevMode}
            controlBar={true}
          />
        ) : (
          <VideoCover url={videoDatas.thumbnailUrl}>
            <PrevBtn
              onClick={() => {
                setPrevMode(true);
                setPrevCover(false);
                setTimeout(() => {
                  setPrevCover(false);
                }, 61000);
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
          {!videoDatas.isPurchased && myId !== videoDatas.channel?.memberId && (
            <span>
              {videoDatas.price
                ? `${priceToString(videoDatas.price)}원`
                : "무료"}
            </span>
          )}
        </VideoTitle>
        <VideoInfo>
          <Profile onClick={handleNavChannel}>
            <ProfileImg
              src={videoDatas.channel?.imageUrl || profileGray}
              alt="프로필 이미지"
            />

            <ProfileRight>
              <ProfileName isDark={isDark}>
                {videoDatas.channel?.channelName}
              </ProfileName>
              <Subscribed isDark={isDark}>
                구독자 {channelInfo.subscribes}명
              </Subscribed>
            </ProfileRight>
          </Profile>

          {myId === videoDatas.channel?.memberId || (
            <SubscribeBtn
              memberId={videoDatas.channel?.memberId}
              channelInfo={channelInfo}
              setSub={setSub}
            />
          )}

          {!videoDatas.isPurchased && myId !== videoDatas.channel?.memberId && (
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
      <AlertModal
        isModalOpen={purchaseModal}
        setIsModalOpen={setPurchaseModal}
        isBackdropClickClose={false}
        content="성공적으로 강의가 구매 되었습니다."
        buttonTitle="확인"
        handleButtonClick={() => {
          setPurchaseModal(false);
          window.location.reload();
        }}
      />
      <AlertModal
        isModalOpen={alertModal}
        setIsModalOpen={setAlertModal}
        isBackdropClickClose={false}
        content="강의를 먼저 구매해주세요."
        buttonTitle="확인"
        handleButtonClick={() => setAlertModal(false)}
      />
      <AlertModal
        isModalOpen={reportedModal}
        setIsModalOpen={setReportedModal}
        isBackdropClickClose={true}
        content="비디오가 신고 되었습니다."
        buttonTitle="확인"
        handleButtonClick={() => setReportedModal(false)}
      />
      <AlertModal
        isModalOpen={alreadyReportedModal}
        setIsModalOpen={setAlreadyReportedModal}
        isBackdropClickClose={true}
        content="이미 신고한 비디오입니다."
        buttonTitle="확인"
        handleButtonClick={() => setAlreadyReportedModal(false)}
      />
      <ReportModal
        reportContent={reportContent}
        setReportContent={reportContentHandler}
        isModalOpen={reportModal}
        setIsModalOpen={setReportModal}
        isBackdropClickClose={false}
        negativeButtonTitle="신고"
        positiveButtonTitle="취소"
        handleNegativeButtonClick={() => handleReportVideo()}
        handlePositiveButtonClick={() => setReportModal(false)}
       />
      <AlertModal
        isModalOpen={alertLogin}
        setIsModalOpen={setAlertLogin}
        isBackdropClickClose={false}
        content="로그인 후 이용해주세요."
        buttonTitle="확인"
        handleButtonClick={() => setAlertLogin(false)}
      />
    </>
  );
};

export default DetailVideo;

const globalTokens = tokens.global;

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
  padding: 28px 50px 30px 50px;
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
  justify-content: space-between;
  align-items: center;
  width: 100%;
  color: ${(props) =>
    props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
`;
export const HeaderBtnContainer = styled.div`
  display: flex;
  flex-direction: row;
  align-items: center;
`

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

export const VideoInfo = styled.button`
  position: relative;
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: center;
  margin-top: 5px;
`;

export const Profile = styled.div`
  min-width: 180px;
  height: 60px;
  padding: 0px 10px;
  display: flex;
  justify-content: start;
  align-items: center;
  margin-right: 50px;
  cursor: pointer;
  &:hover {
    background-color: ${globalTokens.Gray.value};
  }
`;

export const ProfileImg = styled.img`
  width: 50px;
  height: 50px;
  margin-right: 15px;
  border-radius: 50%;
`;

export const ProfileRight = styled.div`
  height: 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: start;
`;

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

const BackButton = styled(PositiveTextButton)`
`;
