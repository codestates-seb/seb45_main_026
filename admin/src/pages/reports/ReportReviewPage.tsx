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
import { reportReviewDataType } from "../../types/reportDataType";
import { getReportReviewList } from "../../services/reprotService";
import {
  MainContainer,
  PageContainer,
} from "../../atoms/layouts/PageContainer";
import ReviewReportList from "../../components/reportPage/ReviewReportList";

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
    // setMaxPage(data?.pageInfo.totalPage);
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
              <tr>
                <TypothId>댓글 ID</TypothId>
                <TypothVideoName>신고된 댓글 내용</TypothVideoName>
                <TypothReportCount>신고 횟수</TypothReportCount>
                <TypothLastDate>최근 신고 날짜</TypothLastDate>
                <TypothReportDetail>비고</TypothReportDetail>
              </tr>
            </thead>
            <tbody>
              {data.data?.map((el: reportReviewDataType) => (
                <>
                  <tr key={el.replyId}>
                    <TypotdId>{el.replyId}</TypotdId>
                    <TypotdVideoName>{el.content}</TypotdVideoName>
                    <TypotdReportCount>{el.reportCount}회</TypotdReportCount>
                    <TypotdLastDate>{el.lastReportedDate}</TypotdLastDate>
                    <TypotdReportDetail>
                      <button
                        onClick={() => {
                          if (isOpened !== el.replyId) {
                            setOpened(el.replyId);
                          } else {
                            setOpened(0);
                          }
                        }}
                      >
                        {isOpened === el.replyId ? "축소하기" : "상세보기"}
                      </button>
                    </TypotdReportDetail>
                  </tr>

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

export const Typotable = styled.table`
  margin: 30px 0px 30px 0px;
`;
export const Typoth = styled.th`
  padding: 10px 0px;
  text-align: center;
  border: 1px solid black;
`;
export const Typotd = styled.td`
  padding: 10px 0px;
  text-align: center;
  border: 1px solid black;
`;
export const TypothId = styled(Typoth)`
  width: 70px;
`;
export const TypothVideoName = styled(Typoth)`
  width: 330px;
`;
export const TypothVideoStatus = styled(Typoth)`
  width: 150px;
`;
export const TypothReportCount = styled(Typoth)`
  width: 85px;
`;
export const TypothLastDate = styled(Typoth)`
  width: 190px;
`;
export const TypothReportDetail = styled(Typoth)`
  width: 80px;
`;
export const TypotdId = styled(Typotd)`
  width: 70px;
`;
export const TypotdVideoName = styled(Typotd)`
  width: 330px;
`;
export const TypotdVideoStatus = styled(Typotd)`
  width: 150px;
`;
export const TypotdReportCount = styled(Typotd)`
  width: 85px;
`;
export const TypotdLastDate = styled(Typotd)`
  width: 190px;
`;
export const TypotdReportDetail = styled(Typotd)`
  width: 80px;
`;
