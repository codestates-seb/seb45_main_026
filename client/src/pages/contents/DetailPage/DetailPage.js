import { styled } from "styled-components";
import axios from "axios";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { PageContainer } from "../../../atoms/layouts/PageContainer";
import DetailVideo from "./DetailVideo";
import DetailReview from "./DetailReview";
import DetailContent from "./DetailContent";
import { setVideoInfo } from "../../../redux/createSlice/VideoInfoSlice";
import { useToken } from "../../../hooks/useToken";
import tokens from "../../../styles/tokens.json";

const globalTokens = tokens.global;

const DetailPage = () => {
  const dispatch = useDispatch();
  const refreshToken = useToken();
  const { videoId } = useParams();
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const token = useSelector((state) => state.loginInfo.accessToken);
  const [videoDatas, setVideoDatas] = useState({});

  const getVideoInfo = () => {
    return axios
      .get(`https://api.itprometheus.net/videos/${videoId}`, {
        headers: { Authorization: token.authorization },
      })
      .then((res) => {
        dispatch(setVideoInfo(res.data.data));
        setVideoDatas(res.data.data); // ?
        console.log(res.data.data);
      })
      .catch((err) => {
        if (err.response.data.message === "만료된 토큰입니다.") {
          refreshToken();
        } else {
          console.log(err);
        }
      });
  };

  useEffect(() => {
    getVideoInfo();
  }, [token]);

  return (
    <PageContainer isDark={isDark}>
      <DetailContainer>
        <DetailVideo videoDatas={videoDatas} />
        <DetailContent videoDatas={videoDatas} getVideoInfo={getVideoInfo} />
        <DetailReview videoDatas={videoDatas} />
      </DetailContainer>
    </PageContainer>
  );
};

export default DetailPage;

export const DetailContainer = styled.div`
  width: 100%;
  max-width: 1170px;
  margin: ${globalTokens.Spacing40.value}px 0;
  display: flex;
  flex-direction: start;
  justify-content: start;
  align-items: center;
  flex-wrap: wrap;
`;
