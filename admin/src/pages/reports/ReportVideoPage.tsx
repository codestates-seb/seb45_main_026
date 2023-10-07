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
import {
  DarkMode,
  UseLocateType,
  reportVideoDataType,
} from "../../types/reportDataType";
import { getReportVideoList } from "../../services/reprotService";
import {
  MainContainer,
  PageContainer,
} from "../../atoms/layouts/PageContainer";
import VideoReportList from "../../components/reportPage/VideoReportList";
import { useLocate } from "../../hooks/useLocation";
import tokens from "../../styles/tokens.json";
import { RegularButton } from "../../atoms/buttons/Buttons";
import { ReactComponent as arrowPrev } from "../../assets/images/icons/arrowPrev.svg";
import { ReactComponent as arrowPrevDark } from "../../assets/images/icons/arrowPrevDark.svg";
import { ReactComponent as arrowNext } from "../../assets/images/icons/arrowNext.svg";
import { ReactComponent as arrowNextDark } from "../../assets/images/icons/arrowNextDark.svg";

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
    setMaxPage(data?.pageInfo.totalPage);
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
              <TableTr isDark={isDark}>
                <TypothId isDark={isDark}>강의 ID</TypothId>
                <TypothVideoName isDark={isDark}>강의 제목</TypothVideoName>
                <TypothVideoStatus isDark={isDark}>강의 상태</TypothVideoStatus>
                <TypothReportCount isDark={isDark}>신고 횟수</TypothReportCount>
                <TypothLastDate isDark={isDark}>최근 신고 날짜</TypothLastDate>
                <TypothReportBlock isDark={isDark}>비고</TypothReportBlock>
                <TypothReportDetail isDark={isDark}></TypothReportDetail>
              </TableTr>
            </thead>
            <tbody>
              {data.data?.map((el: reportVideoDataType) => (
                <>
                  <TableTr isDark={isDark} key={el.videoId}>
                    <TypotdId isDark={isDark}>{el.videoId}</TypotdId>
                    <TypotdVideoName
                      isDark={isDark}
                      onClick={() => locateVideo(el.videoId)}
                    >
                      {el.videoName}
                    </TypotdVideoName>
                    <TypotdVideoStatus isDark={isDark}>
                      {el.videoStatus === "CREATED"
                        ? "활동중"
                        : el.videoStatus === "CLOSED"
                        ? "폐쇄됨"
                        : el.videoStatus === "ADMIN_CLOSED"
                        ? "관리자에 의해 폐쇄됨"
                        : null}
                    </TypotdVideoStatus>
                    <TypotdReportCount isDark={isDark}>
                      {el.reportCount} 회
                    </TypotdReportCount>
                    <TypotdLastDate isDark={isDark}>
                      {el.lastReportedDate.split("T")[0]}
                    </TypotdLastDate>
                    <TypotdReportBlock isDark={isDark}>
                      {(el.videoStatus === "CLOSED" ||
                        el.videoStatus === "ADMIN_CLOSED") && (
                        <RegularButton isDark={isDark} onClick={() => {}}>
                          활성화
                        </RegularButton>
                      )}
                      {el.videoStatus === "CREATED" && (
                        <RegularButton isDark={isDark} onClick={() => {}}>
                          비활성화
                        </RegularButton>
                      )}
                    </TypotdReportBlock>
                    <TypotdReportDetail isDark={isDark}>
                      {isOpened === el.videoId ? (
                        <HideListArrow
                          onClick={() => {
                            if (isOpened !== el.videoId) {
                              setOpened(el.videoId);
                            } else {
                              setOpened(0);
                            }
                          }}
                        />
                      ) : (
                        <ShowListArrow
                          onClick={() => {
                            if (isOpened !== el.videoId) {
                              setOpened(el.videoId);
                            } else {
                              setOpened(0);
                            }
                          }}
                        />
                      )}
                    </TypotdReportDetail>
                  </TableTr>
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
export const TypothVideoName = styled(Typoth)`
  width: 330px;
`;
export const TypothVideoStatus = styled(Typoth)`
  width: 155px;
`;
export const TypothReportCount = styled(Typoth)`
  width: 85px;
`;
export const TypothLastDate = styled(Typoth)`
  width: 140px;
`;
export const TypothReportDetail = styled(Typoth)`
  width: 80px;
`;
export const TypothReportBlock = styled(Typoth)`
  width: 90px;
`;

export const TypotdId = styled(Typotd)`
  width: 70px;
`;
export const TypotdVideoName = styled(Typotd)`
  width: 330px;
  cursor: pointer;
`;
export const TypotdVideoStatus = styled(Typotd)`
  width: 155px;
`;
export const TypotdReportCount = styled(Typotd)`
  width: 85px;
`;
export const TypotdLastDate = styled(Typotd)`
  width: 140px;
`;
export const TypotdReportDetail = styled(Typotd)`
  width: 80px;
`;
export const TypotdReportBlock = styled(Typotd)`
  width: 90px;
`;

export const ShowListArrow = styled(arrowNext)`
  width: 18px;
  height: 18px;
  transform: rotate(90deg);
  cursor: pointer;
`;
export const HideListArrow = styled(arrowPrev)`
  width: 18px;
  height: 18px;
  transform: rotate(90deg);
  cursor: pointer;
`;
