import { styled } from "styled-components";
import { useEffect } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import axios from "axios";
import { PageContainer } from "../../atoms/layouts/PageContainer";
import { setProblems } from "../../redux/createSlice/ProblemSlice";
import SelectNum from "../../components/ProblemPage/SelectNum";
import ProblemBox from "../../components/ProblemPage/ProblemBox";
import { useToken } from "../../hooks/useToken";

const ProblemPage = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { videoId } = useParams();
  const refreshToken = useToken();
  const myId = useSelector((state) => state.loginInfo.myid);
  const videoDatas = useSelector((state) => state.videoInfo.data);
  const token = useSelector((state) => state.loginInfo.accessToken);
  const setting = useSelector((state) => state.problemSlice.setting);
  const problemsData = useSelector((state) => state.problemSlice.data);
  const filtered = problemsData.filter((el, idx) => idx + 1 === setting.isPage);

  const getProblems = () => {
    return axios
      .get(`https://api.itprometheus.net/videos/${videoId}/questions`, {
        headers: { Authorization: token.authorization },
      })
      .then((res) => {
        console.log(res.data.data);
        dispatch(setProblems(res.data.data));
      })
      .catch((err) => {
        if (err.response.data.message === "만료된 토큰입니다.") {
          refreshToken(() => getProblems());
        } else {
          console.log(err);
        }
      });
  };

  useEffect(() => {
    getProblems();
  }, []);

  return (
    <PageContainer>
      <ProblemContainer>
        <HeaderBox>
          <Link to={`/videos/${videoId}`}>
            <LectureBtn>← 강의로 돌아가기</LectureBtn>
          </Link>
          <ProblemHeader>문제</ProblemHeader>
        </HeaderBox>
        <BodyBox>
          {problemsData.length ? (
            <>
              <SelectNum />
              {filtered.map((el) => (
                <ProblemBox key={el.questionId} el={el} />
              ))}
            </>
          ) : (
            <ListEmptyBox>
              <ListEmptyGuide>현재 등록된 강의 문제가 없습니다.</ListEmptyGuide>
              {myId === videoDatas.channel.memberId && (
                <>
                  <ListEmptyGuide>
                    이용자들을 위해 강의 문제들을 추가해 주세요.
                  </ListEmptyGuide>
                  <UploadNavBtn
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

export const ListEmptyGuide = styled.span`
  width: 100%;
  max-width: 500px;
  margin: 5px 0px;
  text-align: center;
  color: gray;
  font-weight: 600;
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
  max-width: 1000px;
  padding: 50px 0px 100px 0px;
  background-color: white;
  border-radius: 8px;
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
  font-size: small;
  color: red;
  margin: 0px 0px 20px -50px;
`;

export const ProblemHeader = styled.h2``;

export const BodyBox = styled.div`
  width: 100%;
  max-width: 700px;
`;
