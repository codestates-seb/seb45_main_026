import { useEffect, useState } from "react";
import styled from "styled-components";
import { useSelector } from "react-redux";
import { RootState } from "../../redux/Store";
import { useNavigate } from "react-router-dom";
import { useToken } from "../../hooks/useToken";
import { useQuery } from "@tanstack/react-query";
import { PageTitle } from "../../styles/PageTitle";
import NavBar from "../../components/navBar/NavBar";
import Loading from "../../components/loading/Loading";
import Pagination from "../../atoms/pagination/Pagination";
import { DarkMode, reportReviewDataType } from "../../types/reportDataType";
import { getReportReviewList } from "../../services/reprotService";
import {
  MainContainer,
  PageContainer,
} from "../../atoms/layouts/PageContainer";
import ReviewReportList from "../../components/reportPage/ReviewReportList";
import tokens from "../../styles/tokens.json";
import { RegularButton } from "../../atoms/buttons/Buttons";
import { HideListArrow, ShowListArrow } from "./ReportVideoPage";

const ReportReviewPage = () => {
  const navigate = useNavigate();
  const refreshToken = useToken();
  const isDark = useSelector((state: RootState) => state.uiSetting.isDark);
  const isLogin = useSelector((state: RootState) => state.loginInfo.isLogin);
  const accessToken = useSelector(
    (state: RootState) => state.loginInfo.accessToken
  );

  const [currentPage, setCurrentPage] = useState<number>(1);
  const [maxPage, setMaxPage] = useState<number>(10);
  const [isSize, setSize] = useState<number>(10);
  const [isSort, setSort] = useState<string>("last-reported-date");
  const [isOpened, setOpened] = useState<number>(0);

  const { isLoading, error, data, isFetching, isPreviousData } = useQuery({
    queryKey: ["reportvideos"],
    queryFn: async () => {
      const response = await getReportReviewList(
        accessToken.authorization,
        currentPage,
        isSize,
        isSort
      );

      console.log(response);

      if (response.response?.data.message === "만료된 토큰입니다.") {
        refreshToken();
      } else {
        return response;
      }
    },
  });

  // console.log(data);

  useEffect(() => {
    if (!isLogin) {
      navigate("/login");
      return;
    }
    setMaxPage(data?.pageInfo.totalPage);
  }, []);
  return (
    <PageContainer isDark={isDark}>
      <MainContainer isDark={isDark}>
        <PageTitle isDark={isDark}>신고 내역 관리</PageTitle>
        <NavBar NavType="댓글" />
        {isLoading ? (
          <Loading />
        ) : (
          <Typotable>
            <thead>
              <TableTr isDark={isDark}>
                <TypothId isDark={isDark}>댓글 ID</TypothId>
                <TypothReplyName isDark={isDark}>
                  신고된 댓글 내용
                </TypothReplyName>
                <TypothReportCount isDark={isDark}>신고 횟수</TypothReportCount>
                <TypothLastDate isDark={isDark}>최근 신고 날짜</TypothLastDate>
                <TypothReportDetail isDark={isDark}>비고</TypothReportDetail>
                <TypothReportBlock isDark={isDark}></TypothReportBlock>
              </TableTr>
            </thead>
            <tbody>
              {data.data?.map((el: reportReviewDataType) => (
                <>
                  <TableTr isDark={isDark} key={el.replyId}>
                    <TypotdId isDark={isDark}>{el.replyId}</TypotdId>
                    <TypotdReplyName isDark={isDark}>
                      {el.content}
                    </TypotdReplyName>
                    <TypotdReportCount isDark={isDark}>
                      {el.reportCount}회
                    </TypotdReportCount>
                    <TypotdLastDate isDark={isDark}>
                      {el.lastReportedDate.split("T")[0]}
                    </TypotdLastDate>
                    <TypotdReportBlock isDark={isDark}>
                      <RegularButton isDark={isDark}>삭제</RegularButton>
                    </TypotdReportBlock>
                    <TypotdReportDetail isDark={isDark}>
                      {isOpened === el.replyId ? (
                        <HideListArrow
                          onClick={() => {
                            if (isOpened !== el.replyId) {
                              setOpened(el.replyId);
                            } else {
                              setOpened(0);
                            }
                          }}
                        />
                      ) : (
                        <ShowListArrow
                          onClick={() => {
                            if (isOpened !== el.replyId) {
                              setOpened(el.replyId);
                            } else {
                              setOpened(0);
                            }
                          }}
                        />
                      )}
                    </TypotdReportDetail>
                  </TableTr>

                  {isOpened === el.replyId && (
                    <ReviewReportList replyId={el.replyId} />
                  )}
                </>
              ))}
            </tbody>
          </Typotable>
        )}
        <Pagination
          isDark={isDark}
          maxPage={maxPage}
          currentPage={currentPage}
          setCurrentPage={setCurrentPage}
        />
      </MainContainer>
    </PageContainer>
  );
};

export default ReportReviewPage;

const globalTokens = tokens.global;

export const Typotable = styled.table`
  margin: 30px 0px 30px 0px;
`;
export const TableTr = styled.tr<DarkMode>`
  border-bottom: 1px solid
    ${(props) =>
      props.isDark ? globalTokens.Gray.value : globalTokens.LightGray.value};
`;
export const Typoth = styled.th<DarkMode>`
  background-color: ${(props) =>
    props.isDark ? globalTokens.Black.value : globalTokens.Background.value};
  color: ${(props) =>
    props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value};
  padding: 15px 0px;
  text-align: center;
`;
export const Typotd = styled.td<DarkMode>`
  color: ${(props) =>
    props.isDark ? globalTokens.White.value : globalTokens.Black.value};
  padding: 15px 0px;
  text-align: center;
`;

export const TypothId = styled(Typoth)`
  width: 70px;
`;
export const TypothReplyName = styled(Typoth)`
  width: 400px;
`;
export const TypothReportCount = styled(Typoth)`
  width: 100px;
`;
export const TypothLastDate = styled(Typoth)`
  width: 150px;
`;
export const TypothReportDetail = styled(Typoth)`
  width: 100px;
`;
export const TypothReportBlock = styled(Typoth)`
  width: 80px;
`;

export const TypotdId = styled(Typotd)`
  width: 80px;
`;
export const TypotdReplyName = styled(Typotd)`
  width: 400px;
`;
export const TypotdReportCount = styled(Typotd)`
  width: 100px;
`;
export const TypotdLastDate = styled(Typotd)`
  width: 150px;
`;
export const TypotdReportDetail = styled(Typotd)`
  width: 100px;
`;
export const TypotdReportBlock = styled(Typotd)`
  width: 80px;
`;
