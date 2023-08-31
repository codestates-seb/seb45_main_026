import { styled } from "styled-components";
import { PageContainer } from "../../atoms/layouts/PageContainer";
import CourseSection from "../../components/UploadPage/Course";
import QuestionSection from "../../components/UploadPage/Question";

export const UploadContainer = styled.section`
  width: 100%;
  max-width: 1000px;

  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: center;

  background-color: white;
  border: 1px solid black;
`;

export const UploadType = styled.div`
  display: flex;
  align-items: center;
`;

export const UploadTypeBtn = styled.button`
  border: 1px solid black;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  margin: 10px;
`;

export const UploadTitle = styled.h2``;

export const UploadSubtitle = styled.span`
  color: gray;
  font-size: 14px;
`;

export const Subtitle = styled.span``

const UploadPage = () => {
  return (
    <PageContainer>
      <UploadContainer>
        <UploadType>
          <UploadTypeBtn>1</UploadTypeBtn>
          <UploadTypeBtn>2</UploadTypeBtn>
        </UploadType>
        <CourseSection />
        <QuestionSection />
      </UploadContainer>
    </PageContainer>
  );
};

export default UploadPage;
