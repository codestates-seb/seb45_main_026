import styled from "styled-components";

// isPage : axios 응답으로 받은 => 현재 페이지와 totalPage 정보
// setParams : axios 요청을 위한 => 페이지 값
const Pagination = ({ isPage, isParams, setParams }) => {
  const pageList = [];
  for (let i = isPage.page - 2; i <= isPage.page + 2; i++) {
    pageList.push(i);
  }

  return (
    <PageBox>
      {pageList.map((el) => {
        if (el >= 1 && el <= isPage.totalPage) {
          return (
            <PageBtn onClick={() => setParams({ ...isParams, page: el })}>
              {el}
            </PageBtn>
          );
        } else {
          return <NoneBtn />;
        }
      })}
    </PageBox>
  );
};

export default Pagination;

export const PageBox = styled.div`
  border: 1px solid red;
  display: flex;
  justify-content: start;
`;

export const PageBtn = styled.button`
  width: 25px;
  height: 25px;
  border: 1px solid blue;
`;

export const NoneBtn = styled.div`
  width: 25px;
  height: 25px;
  border: 1px solid blue;
`;
