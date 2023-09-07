import { styled } from "styled-components";
import { useState } from "react";
import {
  UploadTitle,
  UploadSubtitle,
} from "../../pages/contents/CourseUploadPage";
import UploadModal from "./Modal/UploadModal";
import plus_circle from "../../assets/images/icons/plus_circle.svg";

const ProblemUpload = () => {
  const [isModal, setModal] = useState(false);
  const [isProblemList, setProblemList] = useState([]);
  const [isProblem, setProblem] = useState({
    position: isProblemList.length + 1,
    content: "",
    questionAnswer: "",
    description: "",
    selections: [],
  });

  const handleChangeContent = (e) => {
    switch (e.target.id) {
      case "ProblemTitle":
        setProblem({ ...isProblem, content: e.target.value });
        return;

      case "questionAnswer":
        setProblem({ ...isProblem, questionAnswer: e.target.value });
        return;

      case "ProblemDiscribe":
        setProblem({ ...isProblem, description: e.target.value });
        return;

      case "selections":
        setProblem({ ...isProblem, selections: e.target.value });
        return;

      default:
        return;
    }
  };

  const handleSubmitProblem = () => {
    setProblemList([...isProblemList, isProblem]);
  };

  // console.log(isProblem)

  return (
    <QuestionBox>
      <UploadTitle>문제 등록하기</UploadTitle>
      <UploadSubtitle>
        수강 후 성취도를 검사할 문제를 등록합니다.
      </UploadSubtitle>
      <AddQuestionBox>
        {isProblemList.map((el, idx) => (
          <li key={idx}>{el.content}</li>
        ))}
      </AddQuestionBox>
      <AddQuestionBox>
        <AddQuestion onClick={() => setModal(!isModal)}>
          <AddImg src={plus_circle} alt="문제 등록하기" />
          문제를 등록해 주세요.
        </AddQuestion>
        <SubmitProblem>강의 등록 완료</SubmitProblem>
      </AddQuestionBox>
      {isModal && (
        <UploadModal
          setModal={setModal}
          isProblem={isProblem}
          setProblem={setProblem}
          handleChangeContent={handleChangeContent}
          handleSubmitProblem={handleSubmitProblem}
        />
      )}
    </QuestionBox>
  );
};

export default ProblemUpload;

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
