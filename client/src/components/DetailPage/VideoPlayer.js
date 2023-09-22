import axios from "axios";
import { useEffect, useMemo, useRef, useState } from "react";
import screenfull from "screenfull";
import ReactPlayer from "react-player";
import styled from "styled-components";
import { ReactComponent as Play } from "../../assets/images/icons/Play.svg";
import { ReactComponent as Pause } from "../../assets/images/icons/Pause.svg";
import { ReactComponent as Volume } from "../../assets/images/icons/Volume.svg";
import { ReactComponent as FullScreen } from "../../assets/images/icons/FullScreen.svg";

const VideoPlayer = ({
  videoId,
  thumbnailUrl,
  Playing = false,
  isPrevMode,
  controlBar,
  muted = false,
  onMouseOut = () => {},
}) => {
  const [isUrl, setUrl] = useState("");

  const getVideoUrl = () => {
    return axios
      .get(`https://api.itprometheus.net/videos/${videoId}/url`)
      .then((res) => {
        if (isPrevMode) {
          setUrl(res.data.data.videoUrl + "#t=0,60");
        } else {
          setUrl(res.data.data.videoUrl);
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };

  useEffect(() => {
    getVideoUrl();
  }, []);

  const videoRef = useRef(null);
  const [nowPlaying, setNowPlaying] = useState(Playing); // 재생 여부
  const [showControl, setShowControl] = useState(false); // control bar 보이는지?
  const [isPrevVolumeBtn, setPrevVolumeBtn] = useState(false); // Prev 볼륨 조절버튼
  const [isVolumeOpen, setVolumeOpen] = useState(false); // Prev 볼륨 온/오프
  const [isVolume, setVolume] = useState(0.5); // 볼륨 조절
  const [isMuted, setMuted] = useState(muted); // 음소거 온/오프
  const [isTime, setIsTime] = useState(0);

  // 컨트롤러 게이지 업로드.
  const infiniteLoop = () => {
    if (!videoRef.current) {
      return;
    }
    setIsTime(videoRef.current.getCurrentTime());
    setTimeout(() => {
      infiniteLoop();
    }, 1000);
  };

  // 영상 전체 시간
  const totalTime =
    videoRef && videoRef.current && videoRef.current.getDuration();

  // 영상 현재 시간
  const currentTime =
    videoRef && videoRef.current && videoRef.current.getCurrentTime();

  // 영상 재생/일시정지
  const handlePause = () => {
    return setNowPlaying(!nowPlaying);
  };

  // 영상 시간 조절
  const handleAdjust = (e) => {
    videoRef.current.seekTo(e.target.value);
    setIsTime(e.target.value);
  };

  const [speedRate, setSpeedRate] = useState(1);
  // 영상 배속 조절
  const handleSpeedRate = (rate) => {
    setSpeedRate(rate);
  };

  // 10초 전 이동
  const rewindHandler = () => {
    videoRef.current.seekTo(videoRef.current.getCurrentTime() - 10);
  };

  // 10초 후 이동
  const forwardHandler = () => {
    videoRef.current.seekTo(videoRef.current.getCurrentTime() + 10);
  };

  // 1분 미리보기
  const PreviewMode = () => {
    if (currentTime >= 60) {
      videoRef.current.seekTo(0);
      setNowPlaying(false);
    }
  };

  useMemo(() => {
    if (isPrevMode) {
      PreviewMode();
    }
  }, [currentTime]);

  // 전체화면
  const handleFullScreen = () => {
    if (screenfull.isEnabled) {
      screenfull.toggle(videoRef.current.wrapper);
    }
  };

  const handlePrevVolume = () => {
    if (isPrevVolumeBtn) {
      setPrevVolumeBtn(false);
      setMuted(true);
      setVolume(0);
    } else {
      setPrevVolumeBtn(true);
      setMuted(false);
      setVolume(0.5);
    }
  };

  return (
    <VideoBox>
      {!controlBar && (
        <PrevVolumeBox
          onClick={(e) => {
            e.stopPropagation();
          }}
        >
          <PrevVolumeBtn
            isPrevVolumeBtn={isPrevVolumeBtn}
            onClick={handlePrevVolume}
          />
        </PrevVolumeBox>
      )}
      <ReactPlayer
        ref={videoRef}
        url={isUrl}
        width="100%"
        height="100%"
        playing={nowPlaying} // 자동 재생 on
        muted={isMuted} // 자동 재생 on
        volume={isVolume}
        poster={thumbnailUrl} // 플레이어 초기 포스터 사진
        startTime={0}
        endTime={isPrevMode ? 60 : totalTime}
        playbackRate={speedRate}
        // onEnded={handleVideo} // 플레이어 끝났을 때 이벤트
      />
      <VideoCover
        onMouseOver={() => {
          setShowControl(true);
          infiniteLoop();
        }}
        onMouseOut={() => {
          setShowControl(false);
          onMouseOut();
        }}
        onClick={() => {
          handlePause();
        }}
      >
        {controlBar && showControl && (
          <>
            <SpeedRateBox
              onClick={(e) => {
                e.stopPropagation();
              }}
            >
              <SpeedRate onClick={() => handleSpeedRate(1)}>
                &times; 1
              </SpeedRate>
              <SpeedRate onClick={() => handleSpeedRate(1.2)}>
                &times; 1.2
              </SpeedRate>
              <SpeedRate onClick={() => handleSpeedRate(1.5)}>
                &times; 1.5
              </SpeedRate>
              <SpeedRate onClick={() => handleSpeedRate(3)}>
                &times; 2
              </SpeedRate>
            </SpeedRateBox>
            <ControlBox
              onClick={(e) => {
                e.stopPropagation();
              }}
            >
              {!nowPlaying ? (
                <PlayBtn
                  onClick={() => {
                    handlePause();
                  }}
                />
              ) : (
                <PauseBtn
                  onClick={() => {
                    handlePause();
                  }}
                />
              )}
              <VideoBar
                type="range"
                min={0}
                max={totalTime}
                step={0.01}
                value={isTime}
                onChange={(e) => handleAdjust(e)}
                disabled={isPrevMode}
              />
              {screenfull.isEnabled && (
                // screenfull이 지원되는 경우에만 전체 화면 버튼 표시
                <FullScreenBtn onClick={handleFullScreen} />
              )}

              <VolumeBox>
                {isVolumeOpen && (
                  <VolumeBar
                    type="range"
                    min={0}
                    max={1}
                    step={0.01}
                    value={isVolume}
                    onChange={(e) => setVolume(e.target.value)}
                  />
                )}
                <VolumeBtn onClick={() => setVolumeOpen(!isVolumeOpen)} />
              </VolumeBox>
            </ControlBox>
          </>
        )}
      </VideoCover>
    </VideoBox>
  );
};

export default VideoPlayer;

export const VideoBox = styled.div`
  position: relative;
  width: 100%;
  height: 100%;
  background-color: black;
`;

export const VideoCover = styled.div`
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgb(255, 255, 255, 0%);
  z-index: 10;
`;

export const ControlBox = styled.div`
  position: absolute;
  width: 100%;
  height: 7%;
  bottom: 0%;
`;

export const SpeedRateBox = styled.div`
  position: absolute;
  top: 1%;
  right: 1%;
  display: flex;
  justify-content: start;
  align-items: center;
`;

export const SpeedRate = styled.button`
  margin: 0px 2px;
  padding: 2px 5px;
  background-color: rgb(220, 220, 220, 0.1);
  color: white;
  font-size: 12px;
`;

export const VideoBar = styled.input`
  position: absolute;
  bottom: 45%;
  left: 7%;
  width: 80%;
  overflow: hidden;
  background: none;
  /* appearance: none; */
  transition: 100ms;
  z-index: 9999;

  &::-webkit-slider-runnable-track {
    width: 100%;
    overflow: hidden;
    cursor: pointer;
  }

  &::-webkit-slider-thumb {
    -webkit-appearance: none;
    width: 20px;
    height: 20px;
  }
`;

export const PlayBtn = styled(Play)`
  position: absolute;
  width: 18px;
  height: 18px;
  bottom: 45%;
  left: 3%;
  cursor: pointer;
  path {
    fill: white;
    stroke: black;
    stroke-width: 5px;
  }
`;

export const PauseBtn = styled(Pause)`
  position: absolute;
  width: 18px;
  height: 18px;
  bottom: 45%;
  left: 3%;
  cursor: pointer;
  path {
    fill: white;
    stroke: black;
    stroke-width: 5px;
  }
`;

export const VolumeBox = styled.div`
  position: absolute;
  bottom: 40%;
  right: 4%;
  width: 100px;
  display: flex;
  flex-direction: column;
  justify-content: end;
  align-items: center;
`;

export const VolumeBtn = styled(Volume)`
  width: 20px;
  height: 20px;
  margin-top: 10px;
  cursor: pointer;
  path {
    fill: white;
    stroke: black;
    stroke-width: 2px;
  }
`;

export const VolumeBar = styled.input`
  width: 100px;
  height: 20px;
  overflow: hidden;
  background: none;
  /* appearance: none; */
  transition: 100ms;
  z-index: 9999;

  &::-webkit-slider-runnable-track {
    width: 100%;
    overflow: hidden;
    cursor: pointer;
  }

  &::-webkit-slider-thumb {
    -webkit-appearance: none;
    width: 20px;
    height: 20px;
  }
`;

export const PrevVolumeBox = styled.div`
  position: absolute;
  top: 4%;
  right: 2%;
  width: 20px;
  height: 20px;
  background-color: rgb(200, 200, 200, 20%);
  z-index: 20;
`;

export const PrevVolumeBtn = styled(Volume)`
  width: 20px;
  height: 20px;
  cursor: pointer;
  path {
    fill: ${(props) => (props.isPrevVolumeBtn ? "white" : "gray")};
  }
`;

export const FullScreenBtn = styled(FullScreen)`
  width: 18px;
  height: 18px;
  position: absolute;
  bottom: 45%;
  right: 3%;
  cursor: pointer;
  path {
    fill: white;
    stroke: black;
    stroke-width: 2px;
  }
`;
