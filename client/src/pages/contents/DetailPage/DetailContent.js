import { styled } from "styled-components";

export const ContentInfo = styled.div`
  width: 100%;
  border-radius: 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: start;
  padding: 5px 20px;
  background-color: white;
`;

export const ContentTitle = styled.div`
  width: 100%;
  border-bottom: 2px solid rgb(236, 236, 236);
  font-weight: bold;
  font-size: 18px;
  background-color: white;
  padding: 5px;
`;

export const ContentSub = styled.div`
  margin: 5px 0px;
  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: center;
  font-size: 14px;
  color: gray;
`;

export const Views = styled.div`
  padding-right: 10px;
`;

export const Createdate = styled(Views)``;

export const Content = styled.div``;

export const Category = styled.ul`
  margin: 5px 0px;
  font-size: small;
  color: gray;
  font-size: 16px;
`;

export const CategoryLists = styled.li``;

export const ContentBtn = styled.button`
  background: none;
  font-size: small;
  color: gray;
  font-size: 16px;
`;

const DetailContent = () => {
  return (
    <>
      <ContentInfo>
        <ContentTitle>강의 소개</ContentTitle>

        <ContentSub>
          <Views>조회수 1.8만회</Views>
          <Createdate>2023.08.26</Createdate>
        </ContentSub>

        <Content>
          Create React App 덕분에 React 개발환경 구축이 쉬워진 것처럼 Redux
          toolkit 을 사용하면 Redux 개발환경 구축을 쉽게 할 수 있습니다.
          <Category>#JavaScript #React #Web</Category>
        </Content>

        <ContentBtn>...더보기</ContentBtn>
      </ContentInfo>
    </>
  );
};

export default DetailContent;
