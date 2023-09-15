import axios from "axios";
import { useEffect, useRef, useState } from "react";
import ReactPlayer from "react-player";
import styled from "styled-components";
import { ReactComponent as Play } from "../../assets/images/icons/Play.svg";
import { ReactComponent as Pause } from "../../assets/images/icons/Pause.svg";
import { ReactComponent as Volume } from "../../assets/images/icons/Volume.svg";
import { ReactComponent as FullScreen } from "../../assets/images/icons/FullScreen.svg";
import screenfull from "screenfull";

const VideoPlayer = ({ videoId, thumbnailUrl }) => {
  const [isUrl, setUrl] = useState("");

  const getVideoUrl = () => {
    return axios
      .get(`https://api.itprometheus.net/videos/${videoId}/url`)
      .then((res) => {
        console.log(res.data);
        setUrl(res.data.data.videoUrl);
      })
      .catch((err) => {
        console.log(err);
      });
  };

  useEffect(() => {
    getVideoUrl();
  }, []);

  //   const strcitTime = true && `#t=0, 60`;
  const videoRef = useRef(null);
  const [nowPlaying, setNowPlaying] = useState(false); // 재생 여부
  const [showControl, setShowControl] = useState(false); // control bar 보이는지?

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
    }
  };

  // 전체화면
  const handleFullScreen = () => {
    if (screenfull.isEnabled) {
      screenfull.toggle(videoRef.current.wrapper);
    }
  };

  const [isVolumeOpen, setVolumeOpen] = useState(false);
  const [isVolume, setVolume] = useState(0.5);

  return (
    <VideoBox>
      <ReactPlayer
        ref={videoRef}
        url={isUrl}
        width={"100%"}
        height={"100%"}
        playing={nowPlaying} // 자동 재생 on
        muted={false} // 자동 재생 on
        volume={isVolume}
        controls={false}
        poster={thumbnailUrl} // 플레이어 초기 포스터 사진
        // onEnded={handleVideo} // 플레이어 끝났을 때 이벤트
      />
      <VideoCover
        onMouseOver={() => setShowControl(true)}
        onMouseOut={() => setShowControl(false)}
        onClick={() => {
          handlePause();
        }}
      >
        {showControl && (
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
              value={currentTime}
              onChange={(e) => handleAdjust(e)}
            />
            <VolumeBox>
              <VolumeBtn onClick={() => setVolumeOpen(!isVolumeOpen)} />
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
            </VolumeBox>
            {screenfull.isEnabled && (
              // screenfull이 지원되는 경우에만 전체 화면 버튼 표시
              <FullScreenBtn onClick={handleFullScreen} />
            )}
          </ControlBox>
        )}
      </VideoCover>
    </VideoBox>
  );
};

export default VideoPlayer;

export const VideoBox = styled.div`
  position: relative;
  width: 100%;
  aspect-ratio: 1.8/1;
  margin-top: 5px;
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
  height: 45px;
  bottom: 0%;
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
  }
`;

export const VolumeBox = styled.div`
  position: absolute;
  bottom: 150%;
  right: -2.5%;
  width: 130px;
  height: 30px;
  transform: rotate(270deg);
  display: flex;
  justify-content: start;
  align-items: start;
`;

export const VolumeBtn = styled(Volume)`
  width: 20px;
  height: 20px;
  margin-right: 10px;
  transform: rotate(90deg);
  cursor: pointer;
  path {
    fill: white;
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

export const FullScreenBtn = styled(FullScreen)`
  width: 18px;
  height: 18px;
  position: absolute;
  bottom: 45%;
  right: 8%;
  cursor: pointer;
  path {
    fill: white;
  }
`;
