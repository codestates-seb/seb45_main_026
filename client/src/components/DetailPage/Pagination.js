import styled from "styled-components";
import { TextButton } from "../../atoms/buttons/Buttons";
import tokens from "../../styles/tokens.json";

const globalTokens = tokens.global;

// isPage : axios 응답으로 받은 => 현재 페이지와 totalPage 정보
// setParams : axios 요청을 위한 => 페이지 값
const Pagination = ({ isPage, isParams, setParams }) => {
  const pageList = [];
  for (let i = isPage.page - 2; i <= isPage.page + 2; i++) {
    pageList.push(i);
  }

  return (
    <PageBox>
      {pageList.map((el, idx) => {
        if (el >= 1 && el <= isPage.totalPage) {
          return (
            <PageBtn
              key={idx}
              onClick={() => setParams({ ...isParams, page: el })}
            >
              {el}
            </PageBtn>
          );
        } else {
          return <NoneBtn key={idx} />;
        }
      })}
    </PageBox>
  );
};

export default Pagination;

export const PageBox = styled.div`
  display: flex;
  justify-content: start;
  margin-bottom: ${globalTokens.Spacing20.value}px;
`;

export const PageBtn = styled(TextButton)`
  width: 25px;
  height: 25px;
`;

export const NoneBtn = styled.div`
  width: 25px;
  height: 25px;
`;
