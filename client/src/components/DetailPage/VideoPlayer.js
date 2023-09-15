import axios from "axios";
import { useEffect, useMemo, useRef, useState } from "react";
import ReactPlayer from "react-player";
import styled from "styled-components";

const VideoPlayer = ({ videoId, thumbnailUrl, handleVideo }) => {
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

  return (
    <VideoBox>
      <ReactPlayer
        ref={videoRef}
        url={isUrl}
        width={"100%"}
        height={"100%"}
        playing={nowPlaying} // 자동 재생 on
        muted={false} // 자동 재생 on
        controls={false}
        poster={thumbnailUrl} // 플레이어 초기 포스터 사진
        onEnded={handleVideo} // 플레이어 끝났을 때 이벤트
      />
      <VideoCover
        onMouseOver={() => setShowControl(true)}
        onMouseOut={() => setShowControl(false)}
        onClick={(e) => {
          handlePause();
          e.stopPropagation();
        }}
      >
        {showControl && (
          <VideoBar
            type="range"
            min={0}
            max={totalTime}
            step={0.01}
            value={currentTime}
            onChange={(e) => handleAdjust(e)}
          />
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

export const VideoBar = styled.input`
  position: absolute;
  bottom: 5%;
  left: 5%;
  width: 80%;
  overflow: hidden;
  background: none;
  /* appearance: none; */
  transition: 100ms;

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
