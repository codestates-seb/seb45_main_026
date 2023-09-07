import { useEffect } from "react";
import { useParams } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import axios from "axios";
import { styled } from "styled-components";
import { PageContainer } from "../../../atoms/layouts/PageContainer";
import DetailVideo from "./DetailVideo";
import DetailReview from "./DetailReview";
import DetailContent from "./DetailContent";
import { setVideoInfo } from "../../../redux/createSlice/VideoInfoSlice";

const DetailPage = () => {
  const dispatch = useDispatch();
  const isDark = useSelector((state) => state.uiSetting.isDark); // 나중에 리펙토링으로 삭제
  const token = useSelector((state) => state.loginInfo.accessToken);
  const { videoId } = useParams();
  // const { videoId } = useParams(); // 강의 번호를 알아야함.
  // console.log(videoId)

  useEffect(() => {
    axios
      .get(`https://api.itprometheus.net/videos/${videoId}`, {
        headers: { Authorization: token.authorization },
      })
      .then((res) => {
        dispatch(setVideoInfo(res.data.data));
      })
      .catch((err) => console.log(err));
  }, []);

  return (
    <PageContainer isDark={isDark}>
      <DetailContainer>
        <DetailVideo />
        <DetailContent />
        <DetailReview />
      </DetailContainer>
    </PageContainer>
  );
};

export default DetailPage;

export const DetailContainer = styled.div`
  width: 100%;
  max-width: 1170px;
  display: flex;
  flex-direction: start;
  justify-content: start;
  align-items: center;
  flex-wrap: wrap;
`;
