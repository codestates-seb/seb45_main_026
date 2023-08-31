import { styled } from "styled-components";
import { UploadTitle, UploadSubtitle } from "../../pages/contents/UploadPage";
import UploadModal from "./Modal/UploadModal";

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

const QuestionSection = () => {
  return (
    <QuestionBox>
      <UploadTitle>문제 등록하기</UploadTitle>
      <UploadSubtitle>
        수강 후 성취도를 검사할 문제를 등록합니다.
      </UploadSubtitle>
      <div>
        <div>
          <img src="" alt="" />
          <span>문제를 등록해 주세요.</span>
        </div>
        <button>강의 등록 완료</button>
        <UploadModal />
      </div>
    </QuestionBox>
  );
};

export default QuestionSection;
