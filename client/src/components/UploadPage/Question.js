import { styled } from "styled-components";
import {
  UploadTitle,
  UploadSubtitle,
  RowBox,
  ColBox,
} from "../../pages/contents/UploadPage";
import UploadModal from "./Modal/UploadModal";
import plus_circle from "../../assets/images/icons/plus_circle.svg";

export const QuestionBox = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: start;

  width: 100%;
  max-width: 800px;
  padding: 20px;

  border: 1px solid blue;
`;

export const AddQuestionBox = styled.div`
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
  border: 1px solid rgb(236, 236, 236);
  border-radius: 8px;

  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
`;

export const AddBox = styled.img``;

export const SubmitQuestion = styled.button``;

const QuestionSection = () => {
  return (
    <QuestionBox>
      <UploadTitle>문제 등록하기</UploadTitle>
      <UploadSubtitle>
        수강 후 성취도를 검사할 문제를 등록합니다.
      </UploadSubtitle>
      <AddQuestionBox>
        <AddQuestion>
          <AddBox src={plus_circle} alt="문제 등록하기" />
          <span>문제를 등록해 주세요.</span>
        </AddQuestion>
        <SubmitQuestion>강의 등록 완료</SubmitQuestion>
        {/* <UploadModal /> */}
      </AddQuestionBox>
    </QuestionBox>
  );
};

export default QuestionSection;
