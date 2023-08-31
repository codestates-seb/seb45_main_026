import { styled } from "styled-components";
import { PageContainer } from "../../atoms/layouts/PageContainer";
import CourseSection from "../../components/UploadPage/Course";
import QuestionSection from "../../components/UploadPage/Question";
import { useState } from "react";

export const UploadContainer = styled.section`
  width: 100%;
  max-width: 1000px;
  padding: 50px 0px 100px 0px;
  background-color: white;
  border: 1px solid black;

  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: center;
  position: relative;
`;

export const UploadType = styled.div`
  display: flex;
  align-items: center;
`;

export const UploadTypeBtn = styled.button`
  width: 40px;
  height: 40px;
  margin: 10px;
  border: 2px solid
    ${(props) => (props.active ? "rgb(255, 200, 200)" : "rgb(255, 200, 200)")};
  border-radius: 50%;
  background-color: ${(props) =>
    props.active ? "rgb(255, 200, 200)" : "white"};
  color: ${(props) => (props.active ? "white" : "rgb(255, 200, 200)")};
  font-weight: bold;
  font-size: 16px;
`;

export const UploadTitle = styled.h2``;

export const UploadSubtitle = styled.span`
  margin: 5px 0px 50px 5px;
  color: gray;
  font-size: 14px;
`;

export const SubDescribe = styled.span`
  color: red;
  font-size: 12px;
  margin-left: 15px;
`;

export const RowBox = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: start;
  width: 100%;
  margin: 10px 0px;
`;

export const ColBox = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: start;
  width: 100%;
`;

export const NextBtn = styled.button`
  position: absolute;
  bottom: 4%;
  right: 6%;
  width: 100px;
  height: 40px;
  border-radius: 8px;
  background-color: rgb(255, 200, 200);
  font-weight: 600;
`;

const UploadPage = () => {
  const [isPage, setPage] = useState({ page: 1 });

  return (
    <PageContainer>
      <UploadContainer>
        <UploadType>
          <UploadTypeBtn
            active={isPage.page === 1}
            onClick={() => setPage({ ...isPage, page: 1 })}
          >
            1
          </UploadTypeBtn>
          <UploadTypeBtn
            active={isPage.page === 2}
            onClick={() => setPage({ ...isPage, page: 2 })}
          >
            2
          </UploadTypeBtn>
        </UploadType>
        {isPage.page === 1 && <CourseSection />}
        {isPage.page === 2 && <QuestionSection />}
        {isPage.page !== 2 && (
          <NextBtn
            onClick={() => setPage({ ...isPage, page: isPage.page + 1 })}
          >
            다음
          </NextBtn>
        )}
      </UploadContainer>
    </PageContainer>
  );
};

export default UploadPage;
