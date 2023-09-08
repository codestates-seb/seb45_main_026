import { styled } from "styled-components";
import axios from "axios";
import { useEffect } from "react";
import { useParams } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { PageContainer } from "../../../atoms/layouts/PageContainer";
import DetailVideo from "./DetailVideo";
import DetailReview from "./DetailReview";
import DetailContent from "./DetailContent";
import { setVideoInfo } from "../../../redux/createSlice/VideoInfoSlice";

const DetailPage = () => {
  const { videoId } = useParams();
  const dispatch = useDispatch();
  const isDark = useSelector((state) => state.uiSetting.isDark); // 나중에 리펙토링으로 삭제
  const token = useSelector((state) => state.loginInfo.accessToken);

  useEffect(() => {
    axios
      .get(`https://api.itprometheus.net/videos/${videoId}`, {
        headers: { Authorization: token.authorization },
      })
      .then((res) => {
        // console.log(res.data.data)
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
