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

const ProblemUpload = () => {
  const { videoId } = useParams();
  const navigate = useNavigate();
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
        setProblem({ ...isProblem, questionAnswer: "answer" + num });
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
          alert(res.data.message);
          navigate(`/videos/${videoId}/problems`);
          isProblemList([]);
        }
      })
      .catch((err) => console.log(err));
  };

  return (
    <QuestionBox>
      <UploadTitle>문제 등록하기</UploadTitle>
      <UploadSubtitle>
        수강 후 성취도를 검사할 문제를 등록합니다.
      </UploadSubtitle>
      <AddQuestionBox>
        {isProblemList.map((el, idx) => (
          <QuestionList key={idx}>
            {/* <QuestionNumber>{idx + 1}번 문제</QuestionNumber> */}
            <QuestionTitle>{el.content}</QuestionTitle>
            <QuestionDelete onClick={() => handleDeleteList(idx + 1)}>
              &times;
            </QuestionDelete>
          </QuestionList>
        ))}
      </AddQuestionBox>
      <AddQuestionBox>
        <AddQuestion onClick={() => setModal(!isModal)}>
          <AddImg src={plus_circle} alt="문제 등록하기" />
          문제를 등록해 주세요.
        </AddQuestion>
        <SubmitProblem onClick={() => handleSubmitProblem()}>
          강의 등록 완료
        </SubmitProblem>
      </AddQuestionBox>
      {isModal && (
        <UploadModal
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
  width: 100%;
  max-width: 500px;
  height: 200px;
  border: 2px solid rgb(236, 236, 236);
  border-radius: 8px;
  color: rgb(200, 200, 200);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  &:hover {
    border: 2px solid rgb(220, 220, 220);
    background-color: rgb(236, 236, 236);
  }
`;

export const AddImg = styled.img`
  width: 100%;
  max-width: 50px;
  margin-bottom: 10px;
`;

export const SubmitProblem = styled.button`
  width: 200px;
  height: 40px;
  margin-top: 50px;
  color: white;
  font-weight: 600;
  border-radius: 20px;
  background-color: rgb(255, 100, 100);
  &:hover {
    background-color: rgb(255, 150, 150);
  }
`;
