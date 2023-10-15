import { useEffect, useState } from "react";
import styled from "styled-components";
import { useSelector } from "react-redux";
import { RootState } from "../../redux/Store";
import { useNavigate } from "react-router-dom";
import { useToken } from "../../hooks/useToken";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { PageTitle } from "../../styles/PageTitle";
import NavBar from "../../components/navBar/NavBar";
import Loading from "../../components/loading/Loading";
import Pagination from "../../atoms/pagination/Pagination";
import { DarkMode, ReportReviewDataType } from "../../types/reportDataType";
import { getReportReviewList } from "../../services/reprotService";
import {
  MainContainer,
  PageContainer,
} from "../../atoms/layouts/PageContainer";
import tokens from "../../styles/tokens.json";
import ReportedReviewItems from "../../components/ReportedItems/ReportedReviewItems";
import axios from "axios";
import { errorResponseDataType } from "../../types/axiosErrorType";
import {
  FilterBox,
  FilterBtn,
  FilterContainer,
  FilterDropdown,
  DropdownItem,
} from "./ReportVideoPage";

const ReportReviewPage = () => {
  const navigate = useNavigate();
  const refreshToken = useToken();
  const isDark = useSelector((state: RootState) => state.uiSetting.isDark);
  const isLogin = useSelector((state: RootState) => state.loginInfo.isLogin);
  const accessToken = useSelector(
    (state: RootState) => state.loginInfo.accessToken
  );

  const [currentPage, setCurrentPage] = useState<number>(1);
  const [maxPage, setMaxPage] = useState<number>(1);
  const [isSize, setSize] = useState<number>(10);
  const [isSort, setSort] = useState<string>("last-reported-date");
  const [isFilter, setFilter] = useState<boolean>(false);

  const { isLoading, data, isFetching, isPreviousData } = useQuery({
    queryKey: ["ReportReview", currentPage, isSort, accessToken],
    queryFn: async () => {
      try {
        const response = await getReportReviewList(
          accessToken.authorization,
          currentPage,
          isSize,
          isSort
        );
        setMaxPage(response.pageInfo.totalPage);
        return response;
      } catch (err) {
        if (axios.isAxiosError<errorResponseDataType, any>(err)) {
          if (err.response?.data.message === "만료된 토큰입니다.") {
            refreshToken();
          } else {
            console.log(err);
          }
        }
      }
    },
    keepPreviousData: true,
    staleTime: 1000 * 60 * 5,
    cacheTime: 1000 * 60 * 30,
    retry: 3, //error를 표시하기 전에 실패한 요청을 다시 시도하는 횟수
    retryDelay: 1000,
  });

  const queryClient = useQueryClient();
  useEffect(() => {
    if (!isLogin) {
      navigate("/login");
      return;
    }
    if (currentPage < maxPage) {
      queryClient.prefetchQuery({
        queryKey: ["ReportReview"],
        queryFn: async () => {
          const response = await getReportReviewList(
            accessToken.authorization,
            currentPage + 1,
            isSize,
            isSort
          );
          return response;
        },
      });
    }
  }, []);

  return (
    <PageContainer isDark={isDark}>
      <MainContainer isDark={isDark}>
        <PageTitle isDark={isDark}>신고 내역 관리</PageTitle>
        <NavBar NavType="댓글" />
        <FilterContainer>
          <FilterBox onClick={() => setFilter(!isFilter)}>
            <FilterBtn isDark={isDark}>
              {isSort === "last-reported-date"
                ? "최신순"
                : isSort === "report-count"
                ? "신고순"
                : isSort === "created-date"
                ? "생성순"
                : ""}
            </FilterBtn>
            {isFilter && (
              <FilterDropdown isDark={isDark}>
                <DropdownItem
                  isDark={isDark}
                  onClick={() => setSort("last-reported-date")}
                >
                  최신순
                </DropdownItem>
                <DropdownItem
                  isDark={isDark}
                  onClick={() => setSort("report-count")}
                >
                  신고순
                </DropdownItem>
                <DropdownItem
                  isDark={isDark}
                  onClick={() => setSort("created-date")}
                >
                  생성순
                </DropdownItem>
              </FilterDropdown>
            )}
          </FilterBox>
        </FilterContainer>
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
              {data.data?.map((el: ReportReviewDataType) => (
                <ReportedReviewItems key={el.replyId} item={el} />
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
