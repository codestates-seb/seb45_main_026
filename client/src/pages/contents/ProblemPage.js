import { styled } from "styled-components";
import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import axios from "axios";
import { PageContainer } from "../../atoms/layouts/PageContainer";
import { setProblems } from "../../redux/createSlice/ProblemSlice";
import SelectNum from "../../components/ProblemPage/SelectNum";
import ProblemBox from "../../components/ProblemPage/ProblemBox";
import { useToken } from "../../hooks/useToken";
import tokens from "../../styles/tokens.json";
import { BodyTextTypo } from "../../atoms/typographys/Typographys";
import { AlertModal } from "../../atoms/modal/Modal";

const globalTokens = tokens.global;

const ProblemPage = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { videoId } = useParams();
  const refreshToken = useToken();
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const myId = useSelector((state) => state.loginInfo.myid);
  const videoDatas = useSelector((state) => state.videoInfo.data);
  const token = useSelector((state) => state.loginInfo.accessToken);
  const setting = useSelector((state) => state.problemSlice.setting);
  const problemsData = useSelector((state) => state.problemSlice.data);
  const filtered = problemsData.filter((el, idx) => idx + 1 === setting.isPage);
  const [errAlert, setErrAlert] = useState({
    isModalOpen: false,
    content: "",
  });

  const getProblems = () => {
    return axios
      .get(`https://api.itprometheus.net/videos/${videoId}/questions`, {
        headers: { Authorization: token.authorization },
      })
      .then((res) => {
        dispatch(setProblems(res.data.data));
        console.log(res)
      })
      .catch((err) => {
        if (err.response.data?.code === 401) {
          refreshToken(() => getProblems());
          setErrAlert({
            ...errAlert,
            isModalOpen: true,
            content: "새로고침 후 다시 시도해주세요.",
          });
        } else if (err.response.data?.code === 400) {
          setErrAlert({
            ...errAlert,
            isModalOpen: true,
            content: "잘못된 접근입니다.",
          });
        } else if (err.response.data?.code === 403) {
          setErrAlert({
            ...errAlert,
            isModalOpen: true,
            content: "로그인 후 가능합니다.",
          });
        } else if (err.response.data?.code === 404) {
          navigate("/*");
        } else if (err.response.data?.code === 409) {
          setErrAlert({
            ...errAlert,
            isModalOpen: true,
            content: `${err.response.data.message}`,
          });
        } else if (err.response.data?.code === 500) {
          setErrAlert({
            ...errAlert,
            isModalOpen: true,
            content: "서버로부터 응답이 없습니다.",
          });
        } else {
          setErrAlert({
            ...errAlert,
            isModalOpen: true,
            content: "알 수 없는 오류입니다.",
          });
        }
      });
  };

  useEffect(() => {
    getProblems();
  }, []);

  return (
    <>
      <PageContainer isDark={isDark}>
        <ProblemContainer isDark={isDark}>
          <HeaderBox isDark={isDark}>
            <Link isDark={isDark} to={`/videos/${videoId}`}>
              <LectureBtn isDark={isDark}>← 강의로 돌아가기</LectureBtn>
            </Link>
            <ProblemHeader isDark={isDark}>문제</ProblemHeader>
          </HeaderBox>
          <BodyBox isDark={isDark}>
            {problemsData.length ? (
              <>
                <SelectNum />
                {filtered.map((el) => (
                  <ProblemBox key={el.questionId} el={el} />
                ))}
              </>
            ) : (
              <ListEmptyBox>
                <ListEmptyGuide isDark={isDark}>
                  현재 등록된 강의 문제가 없습니다.
                </ListEmptyGuide>
                {myId === videoDatas.channel.memberId && (
                  <>
                    <ListEmptyGuide isDark={isDark}>
                      이용자들을 위해 강의 문제들을 추가해 주세요.
                    </ListEmptyGuide>
                    <UploadNavBtn
                      isDark={isDark}
                      onClick={() =>
                        navigate(`/videos/${videoId}/problems/upload`)
                      }
                    >
                      강의 문제 추가하기
                    </UploadNavBtn>
                  </>
                )}
              </ListEmptyBox>
            )}
          </BodyBox>
        </ProblemContainer>
      </PageContainer>
      <AlertModal
        isModalOpen={errAlert.isModalOpen}
        setIsModalOpen={setErrAlert}
        isBackdropClickClose={false}
        content={errAlert.content}
        buttonTitle="확인"
        handleButtonClick={() =>
          setErrAlert({ ...errAlert, isModalOpen: false })
        }
      />
    </>
  );
};

export default ProblemPage;

export const ListEmptyBox = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 150px 0px;
  min-height: 600px;
`;

export const ListEmptyGuide = styled(BodyTextTypo)`
  width: 100%;
  max-width: 500px;
  margin: 5px 0px;
  text-align: center;
  color: ${(props) =>
    props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
`;

export const UploadNavBtn = styled.button`
  width: 100%;
  max-width: 400px;
  height: 45px;
  margin: 20px 0px;
  border-radius: 8px;
  background-color: rgb(255, 200, 200);
  font-weight: 600;
  font-size: 16px;
`;

export const ProblemContainer = styled.section`
  width: 100%;
  max-width: 1170px;
  margin-top: ${globalTokens.Spacing40.value}px;
  margin-bottom: ${globalTokens.Spacing40.value}px;
  padding: 50px 0px 100px 0px;
  background-color: ${(props) =>
    props.isDark ? "rgba(255,255,255,0.15)" : globalTokens.White.value};
  border-radius: ${globalTokens.Spacing8.value}px;
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: center;
`;

export const HeaderBox = styled.div`
  width: 100%;
  max-width: 800px;
  padding: 20px;
  /* border:1px solid black; */
`;

export const LectureBtn = styled.button`
  font-size: ${globalTokens.BodyText.value}px;
  color: ${(props) =>
    props.isDark ? globalTokens.LightRed.value : globalTokens.Negative.value};
  margin: 0px 0px ${globalTokens.Spacing16.value}px -50px;
`;

export const ProblemHeader = styled.h2`
  font-size: ${globalTokens.Heading5.value}px;
  color: ${(props) =>
    props.isDark ? globalTokens.White.value : globalTokens.Black.value};
`;

export const BodyBox = styled.div`
  width: 100%;
  max-width: 700px;
`;
