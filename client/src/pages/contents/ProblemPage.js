import { styled } from "styled-components";
import { useEffect } from "react";
import { PageContainer } from "../../atoms/layouts/PageContainer";
import { Link } from "react-router-dom";
import axios from "axios";
import { setProblems } from "../../redux/createSlice/ProblemSlice";
import SelectNum from "../../components/CartPage/ProblemPage/SelectNum";
import ProblemBox from "../../components/CartPage/ProblemPage/ProblemBox";
import { useDispatch, useSelector } from "react-redux";

const ProblemPage = () => {
  const dispatch = useDispatch();
  const token = useSelector((state) => state.loginInfo.accessToken);
  const setting = useSelector((state) => state.problemSlice.setting);
  const problemsData = useSelector((state) => state.problemSlice.data);
  const filtered = problemsData.filter((el, idx) => idx + 1 === setting.isPage);

  useEffect(() => {
    axios
      .get("https://api.itprometheus.net/videos/2/questions", {
        headers: { Authorization: token.authorization },
      })
      .then((res) => {
        console.log(res.data.data);
        dispatch(setProblems(res.data.data));
      })
      .catch((err) => console.log(err));
  }, []);

  return (
    <PageContainer>
      <ProblemContainer>
        <HeaderBox>
          <Link to="/videos/1">
            <LectureBtn>← 강의로 돌아가기</LectureBtn>
          </Link>
          <ProblemHeader>문제</ProblemHeader>
        </HeaderBox>
        <BodyBox>
          <SelectNum />
          {filtered.map((el) => (
            <ProblemBox key={el.questionId} el={el} />
          ))}
        </BodyBox>
      </ProblemContainer>
    </PageContainer>
  );
};

export default ProblemPage;

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
