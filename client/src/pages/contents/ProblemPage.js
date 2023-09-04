import { styled } from "styled-components";
import { useState } from "react";
import { PageContainer } from "../../atoms/layouts/PageContainer";
import { Link } from "react-router-dom";

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

export const UploadType = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
`;

export const UploadTypeBtn = styled.button`
  width: 40px;
  height: 40px;
  margin: 10px;
  border: 2px solid rgb(255, 100, 100);
  border-radius: 50%;
  background-color: ${(props) =>
    props.active ? "rgb(255, 100, 100);" : "white"};
  color: ${(props) => (props.active ? "white" : "rgb(255, 100, 100)")};
  font-weight: bold;
  font-size: 16px;
`;

export const ProblemTitle = styled.div`
  width: 100%;
  margin: 20px 0px;
  padding: 60px 20px;
  border: 2px solid rgb(236, 236, 236);
  border-radius: 8px;
`;

export const ProblemContent = styled.span``;

export const ProblemLists = styled.ul`
  width: 100%;
  margin: 30px 0px;
`;

export const ProblemList = styled.li`
  width: 100%;
  margin: 15px 0px;
  padding: 10px 20px;
  border: 2px solid rgb(236, 236, 236);
  border-radius: 8px;
  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: center;
`;

export const ContentNum = styled.input`
  width: 20px;
  height: 20px;
`;
export const ListContent = styled.label`
  width: 100%;
  height: 30px;
  margin-left: 30px;
  flex-wrap: wrap;
`;

export const BtnBox = styled.div`
  margin-top: 30px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
`;

export const RegularBtn = styled.button`
  padding: 0px 20px;
  height: 40px;
  border: 1px solid rgb(220, 220, 220);
  border-radius: 20px;
`;

export const PrevBtn = styled(RegularBtn)`
  position: absolute;
  top: 0;
  left: 3%;
`;
export const ConfirmBtn = styled(RegularBtn)`
  background-color: ${(props) =>
    props.opened ? "rgb(255, 100, 100)" : "white"};
  color: ${(props) => (props.opened ? "white" : "black")};
`;

export const NextBtn = styled(RegularBtn)`
  position: absolute;
  top: 0;
  right: 3%;
`;

export const DiscBox = styled.div`
  width: 100%;
  margin: 30px 10px 10px 10px;
  display: flex;
  flex-direction: column;
`;

export const DiscName = styled.span`
  color: gray;
  padding-left: 10px;
`;

export const DiscContent = styled.div`
  border: 2px solid rgb(236, 236, 236);
  border-radius: 8px;
  margin-top: 10px;
  padding: 20px;
  flex-wrap: wrap;
`;

const ProblemPage = () => {
  const [isPage, setPage] = useState({ page: 1 });
  const [isDetail, setDetail] = useState(false);

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
          <ProblemTitle>
            <ProblemContent>
              다음 중 자바스크립트 호이스팅에 대한 설명으로 올바른 것은?
            </ProblemContent>
          </ProblemTitle>

          <ProblemLists>
            <ProblemList>
              <ContentNum type="checkbox" />
              <ListContent>1. 문제 선택사항 미리보기 입니다.</ListContent>
            </ProblemList>
            <ProblemList>
              <ContentNum type="checkbox" />
              <ListContent>2. 문제 선택사항 미리보기 입니다.</ListContent>
            </ProblemList>
            <ProblemList>
              <ContentNum type="checkbox" />
              <ListContent>3. 문제 선택사항 미리보기 입니다.</ListContent>
            </ProblemList>
            <ProblemList>
              <ContentNum type="checkbox" />
              <ListContent>4. 문제 선택사항 미리보기 입니다.</ListContent>
            </ProblemList>
          </ProblemLists>

          <BtnBox>
            <PrevBtn>이전</PrevBtn>
            <ConfirmBtn opened={isDetail} onClick={() => setDetail(!isDetail)}>
              정답 확인
            </ConfirmBtn>
            <NextBtn>다음</NextBtn>
          </BtnBox>

          {isDetail && (
            <DiscBox>
              <DiscName>해설</DiscName>
              <DiscContent>자바스크립트 호이스팅은 ~ 입니다.</DiscContent>
            </DiscBox>
          )}
        </BodyBox>
      </ProblemContainer>
    </PageContainer>
  );
};

export default ProblemPage;
