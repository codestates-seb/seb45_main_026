import { styled } from "styled-components";
import axios from "axios";
import { useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import { useState } from "react";
import {
  UploadTitle,
  UploadSubtitle,
} from "../../pages/contents/CourseUploadPage";
import UploadModal from "./Modal/UploadModal";
import plus_circle from "../../assets/images/icons/plus_circle.svg";
import { useToken } from "../../hooks/useToken";
import tokens from '../../styles/tokens.json';
import { PositiveTextButton, RoundButton, TextButton } from "../../atoms/buttons/Buttons";

const globalTokens = tokens.global;

const ProblemUpload = () => {
  const isDark = useSelector(state=>state.uiSetting.isDark);
  const { videoId } = useParams();
  const navigate = useNavigate();
  const refreshToken = useToken();
  const token = useSelector((state) => state.loginInfo.accessToken);
  const initialState = {
    content: "",
    questionAnswer: "",
    description: "",
    selections: ["", "", "", ""],
  };
  const [isModal, setModal] = useState(false);
  const [isProblemList, setProblemList] = useState([]);
  const [isProblem, setProblem] = useState(initialState);

  const handleChangeContent = (e, num) => {
    switch (e.target.id) {
      case "ProblemTitle":
        setProblem({ ...isProblem, content: e.target.value });
        return;

      case "questionAnswer":
        setProblem({ ...isProblem, questionAnswer: num });
        return;

      case "ProblemDiscribe":
        setProblem({ ...isProblem, description: e.target.value });
        return;

      case "selections":
        const isSelection = isProblem.selections.map((el, idx) => {
          if (idx === num - 1) {
            return e.target.value;
          } else {
            return el;
          }
        });
        setProblem({ ...isProblem, selections: isSelection });
        return;

      default:
        return;
    }
  };

  const initProblem = () => {
    setProblem(initialState);
  };

  const handleCreateProblem = () => {
    setProblemList([...isProblemList, isProblem]);
  };

  const handleDeleteList = (num) => {
    const filtered = isProblemList.filter((obj, idx) => idx + 1 !== num);
    setProblemList(filtered);
  };

  const handleSubmitProblem = () => {
    return axios
      .post(
        `https://api.itprometheus.net/videos/${videoId}/questions`,
        isProblemList,
        {
          headers: { Authorization: token.authorization },
        }
      )
      .then((res) => {
        console.log(res.data);
        if (res.data.code === 201) {
          alert("성공적으로 강의 문제가 등록되었습니다.");
          navigate(`/videos/${videoId}/problems`);
          setProblemList([]);
        }
      })
      .catch((err) => {
        console.log(err);
        if (err.response.data.message === "만료된 토큰입니다.") {
          refreshToken(() => handleSubmitProblem());
        }
      });
  };

  return (
    <QuestionBox isDark={isDark}>
      <UploadTitle isDark={isDark}>문제 등록하기</UploadTitle>
      <UploadSubtitle isDark={isDark}>
        수강 후 성취도를 검사할 문제를 등록합니다.
      </UploadSubtitle>
      <AddQuestionBox isDark={isDark}>
        {isProblemList.map((el, idx) => (
          <QuestionList isDark={isDark} key={idx}>
            {/* <QuestionNumber>{idx + 1}번 문제</QuestionNumber> */}
            <QuestionTitle>{el.content}</QuestionTitle>
            <QuestionDelete onClick={() => handleDeleteList(idx + 1)}>
              &times;
            </QuestionDelete>
          </QuestionList>
        ))}
      </AddQuestionBox>
      <AddQuestionBox isDark={isDark}>
        <AddQuestion isDark={isDark} onClick={() => setModal(!isModal)}>
          <AddImg src={plus_circle} alt="문제 등록하기" />
          문제를 등록해 주세요.
        </AddQuestion>
        <SubmitProblem isDark={isDark} onClick={() => handleSubmitProblem()}>
          강의 등록 완료
        </SubmitProblem>
      </AddQuestionBox>
      {isModal && (
        <UploadModal
          isDark={isDark}
          setModal={setModal}
          isProblem={isProblem}
          setProblem={setProblem}
          handleChangeContent={handleChangeContent}
          handleCreateProblem={handleCreateProblem}
          initProblem={initProblem}
        />
      )}
    </QuestionBox>
  );
};

export default ProblemUpload;

export const QuestionList = styled.li`
  width: 100%;
  max-width: 500px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 2px solid rgb(236, 236, 236);
  border-radius: 8px;
  margin-bottom: 20px;
  padding: 20px;
`;

export const QuestionNumber = styled.div`
  width: 65px;
  font-weight: 600;
`;
export const QuestionDelete = styled.button`
  width: 20px;
`;
export const QuestionTitle = styled.div`
  width: 340px;
  font-weight: bold;
  color: rgb(255, 100, 100);
`;

export const QuestionBox = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: start;
  width: 100%;
  max-width: 800px;
  min-height: 100vh;
  padding: 20px;
`;

export const AddQuestionBox = styled.ul`
  width: 100%;
  max-width: 800px;
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: center;
`;

export const AddQuestion = styled.div`
  margin-top: ${globalTokens.Spacing8.value}px;
  width: 100%;
  max-width: 500px;
  height: 200px;
  border: 1px solid ${props=>props.isDark? globalTokens.Gray.value : globalTokens.LightGray.value};
  border-radius: ${globalTokens.RegularRadius.value}px;
  color: ${props=>props.isDark?globalTokens.White.value:globalTokens.Black.value};
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: 300ms;
  &:hover {
    background-color: ${props=>props.isDark?globalTokens.Black.value:globalTokens.Background.value};
  }
`;

export const AddImg = styled.img`
  width: 100%;
  max-width: 50px;
  margin-bottom: 10px;
`;

export const SubmitProblem = styled(RoundButton)`
  width: 180px;
  height: 45px;
  margin-top: 50px;
  font-weight: ${globalTokens.Bold.value};
`;
