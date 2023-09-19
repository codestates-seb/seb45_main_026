import { useSelector } from "react-redux";
import { PageContainer } from "../atoms/layouts/PageContainer";
import styled from "styled-components";
import { Heading5Typo, Heading1Typo } from "../atoms/typographys/Typographys";

const NotFound = () => {
  const isDark = useSelector((state) => state.uiSetting.isDark);

  return (
    <PageContainer isDark={isDark}>
      <NotFoundBox>
        <NotFoundContent>현재 페이지를 찾을 수 없습니다.</NotFoundContent>
        <NotFoundSubContent>404 Not Found</NotFoundSubContent>
      </NotFoundBox>
    </PageContainer>
  );
};

export default NotFound;

export const NotFoundBox = styled.div`
  width: 100%;
  height: 90vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
`;

export const NotFoundContent = styled(Heading1Typo)`
  margin-bottom: 30px;
`;

export const NotFoundSubContent = styled(Heading5Typo)``;
