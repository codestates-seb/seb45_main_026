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
import { UseLocateType, reportVideoDataType } from "../../types/reportDataType";
import { getReportVideoList } from "../../services/reprotService";
import {
  MainContainer,
  PageContainer,
} from "../../atoms/layouts/PageContainer";
import VideoReportList from "../../components/reportPage/VideoReportList";
import { useLocate } from "../../hooks/useLocation";

const ReportVideoPage = () => {
  const navigate = useNavigate();
  const refreshToken = useToken();
  const { locateVideo }: UseLocateType = useLocate();
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
      const response = await getReportVideoList(
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
        <NavBar NavType="비디오" />
        {isLoading ? (
          <Loading />
        ) : (
          <Typotable>
            <thead>
              <tr>
                <TypothId>강의 ID</TypothId>
                <TypothVideoName>강의 제목</TypothVideoName>
                <TypothVideoStatus>강의 상태</TypothVideoStatus>
                <TypothReportCount>신고 횟수</TypothReportCount>
                <TypothLastDate>최근 신고 날짜</TypothLastDate>
                <TypothReportDetail>비고</TypothReportDetail>
              </tr>
            </thead>
            <tbody>
              {data.data?.map((el: reportVideoDataType) => (
                <>
                  <tr key={el.videoId}>
                    <TypotdId>{el.videoId}</TypotdId>
                    <TypotdVideoName onClick={() => locateVideo(el.videoId)}>
                      {el.videoName}
                    </TypotdVideoName>
                    <TypotdVideoStatus>{el.videoStatus}</TypotdVideoStatus>
                    <TypotdReportCount>{el.reportCount}회</TypotdReportCount>
                    <TypotdLastDate>{el.lastReportedDate}</TypotdLastDate>
                    <TypotdReportDetail>
                      <button
                        onClick={() => {
                          if (isOpened !== el.videoId) {
                            setOpened(el.videoId);
                          } else {
                            setOpened(0);
                          }
                        }}
                      >
                        {isOpened === el.videoId ? "축소하기" : "상세보기"}
                      </button>
                    </TypotdReportDetail>
                  </tr>

                  {isOpened === el.videoId && (
                    <VideoReportList videoId={el.videoId} />
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

export default ReportVideoPage;

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
  cursor: pointer;
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
