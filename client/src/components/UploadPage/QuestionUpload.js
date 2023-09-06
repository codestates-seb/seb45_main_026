import { styled } from "styled-components";
import { UploadTitle, UploadSubtitle } from "../../pages/contents/UploadPage";
import UploadModal from "./Modal/UploadModal";
import plus_circle from "../../assets/images/icons/plus_circle.svg";
import { useState } from "react";

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

export const SubmitQuestion = styled.button`
  width: 200px;
  height: 40px;
  margin-top: 50px;
  font-weight: 600;
  color: white;
  border-radius: 20px;
  background-color: rgb(255, 100, 100);
  &:hover {
    background-color: rgb(255, 150, 150);
  }
`;

const QuestionUpload = () => {
  const [isModal, setModal] = useState(false);

  return (
    <QuestionBox>
      <UploadTitle>문제 등록하기</UploadTitle>
      <UploadSubtitle>
        수강 후 성취도를 검사할 문제를 등록합니다.
      </UploadSubtitle>
      <AddQuestionBox>
        <li></li>
      </AddQuestionBox>
      <AddQuestionBox>
        <AddQuestion onClick={() => setModal(!isModal)}>
          <AddImg src={plus_circle} alt="문제 등록하기" />
          문제를 등록해 주세요.
        </AddQuestion>
        <SubmitQuestion>강의 등록 완료</SubmitQuestion>
      </AddQuestionBox>
      {isModal && <UploadModal setModal={setModal} />}
    </QuestionBox>
  );
};

export default QuestionUpload;
