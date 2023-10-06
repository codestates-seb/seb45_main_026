import { useEffect, useState } from "react";
import styled from "styled-components";
import { useSelector } from "react-redux";
import { RootState } from "../../redux/Store";
import { useLocation, useNavigate } from "react-router-dom";
import { useToken } from "../../hooks/useToken";
import { useQuery } from "@tanstack/react-query";
import { PageTitle } from "../../styles/PageTitle";
import NavBar from "../../components/navBar/NavBar";
import Loading from "../../components/loading/Loading";
import Pagination from "../../atoms/pagination/Pagination";
import { DarkMode, ReportChannelDataType } from "../../types/reportDataType";
import { getReportChannelList } from "../../services/reprotService";
import {
  MainContainer,
  PageContainer,
} from "../../atoms/layouts/PageContainer";
import ChannelReportList from "../../components/reportPage/ChannelReportList";
import tokens from "../../styles/tokens.json";
import { HideListArrow, ShowListArrow } from "./ReportVideoPage";
import { RegularButton } from "../../atoms/buttons/Buttons";

const ReportChannelPage = () => {
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
      const response = await getReportChannelList(
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
        <NavBar NavType="채널" />
        {isLoading ? (
          <Loading />
        ) : (
          <Typotable>
            <thead>
              <TableTr isDark={isDark}>
                <TypothId isDark={isDark}>채널 ID</TypothId>
                <TypothVideoName isDark={isDark}>채널 이름</TypothVideoName>
                <TypothVideoStatus isDark={isDark}>채널 상태</TypothVideoStatus>
                <TypothReportCount isDark={isDark}>신고 횟수</TypothReportCount>
                <TypothLastDate isDark={isDark}>최근 신고 날짜</TypothLastDate>
                <TypothReportBlock isDark={isDark}>비고</TypothReportBlock>
                <TypothReportDetail isDark={isDark}></TypothReportDetail>
              </TableTr>
            </thead>
            <tbody>
              {data.data?.map((el: ReportChannelDataType) => (
                <>
                  <TableTr isDark={isDark} key={el.memberId}>
                    <TypotdId isDark={isDark}>{el.memberId}</TypotdId>
                    <TypotdVideoName isDark={isDark}>
                      {el.channelName}
                    </TypotdVideoName>
                    <TypotdVideoStatus isDark={isDark}>
                      {el.memberStatus === "ACTIVE"
                        ? "활동중"
                        : el.memberStatus === "BLOCKED"
                        ? "차단됨"
                        : null}
                    </TypotdVideoStatus>
                    <TypotdReportCount isDark={isDark}>
                      {el.reportCount}회
                    </TypotdReportCount>
                    <TypotdLastDate isDark={isDark}>
                      {el.lastReportedDate.split("T")[0]}
                    </TypotdLastDate>
                    <TypotdReportBlock isDark={isDark}>
                      <RegularButton isDark={isDark}>차단</RegularButton>
                    </TypotdReportBlock>
                    <TypotdReportDetail isDark={isDark}>
                      {isOpened === el.memberId ? (
                        <HideListArrow
                          onClick={() => {
                            if (isOpened !== el.memberId) {
                              setOpened(el.memberId);
                            } else {
                              setOpened(0);
                            }
                          }}
                        />
                      ) : (
                        <ShowListArrow
                          onClick={() => {
                            if (isOpened !== el.memberId) {
                              setOpened(el.memberId);
                            } else {
                              setOpened(0);
                            }
                          }}
                        />
                      )}
                    </TypotdReportDetail>
                  </TableTr>

                  {isOpened === el.memberId && (
                    <ChannelReportList memberId={el.memberId} />
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

export default ReportChannelPage;

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
  width: 80px;
`;
export const TypothVideoName = styled(Typoth)`
  width: 330px;
`;
export const TypothVideoStatus = styled(Typoth)`
  width: 145px;
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
export const TypothReportBlock = styled(Typoth)`
  width: 80px;
`;

export const TypotdId = styled(Typotd)`
  width: 80px;
`;
export const TypotdVideoName = styled(Typotd)`
  width: 330px;
`;
export const TypotdVideoStatus = styled(Typotd)`
  width: 145px;
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
export const TypotdReportBlock = styled(Typotd)`
  width: 80px;
`;
