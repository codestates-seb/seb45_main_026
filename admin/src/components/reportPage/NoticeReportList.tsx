import { useEffect, useState } from "react";
import styled from "styled-components";
import { useSelector } from "react-redux";
import { RootState } from "../../redux/Store";
import { useQuery } from "@tanstack/react-query";
import { getNoticeReportList } from "../../services/reprotService";
import { DarkMode, NoticeReportListType } from "../../types/reportDataType";
import Pagination from "../../atoms/pagination/Pagination";
import Loading from "../loading/Loading";
import { useToken } from "../../hooks/useToken";
import tokens from "../../styles/tokens.json";

interface OwnProps {
  announcementId: number;
}

const NoticeReportList: React.FC<OwnProps> = ({ announcementId }) => {
  const refreshToken = useToken();
  const isDark = useSelector((state: RootState) => state.uiSetting.isDark);
  const accessToken = useSelector(
    (state: RootState) => state.loginInfo.accessToken
  );
  const [currentPage, setCurrentPage] = useState<number>(1);
  const [maxPage, setMaxPage] = useState<number>(10);
  const [isSize, setSize] = useState<number>(5);

  const { isLoading, error, data, isFetching, isPreviousData } = useQuery({
    queryKey: ["videoreportlist"],
    queryFn: async () => {
      const response = await getNoticeReportList(
        accessToken.authorization,
        announcementId,
        currentPage,
        isSize
      );

      if (response.response?.data.message === "만료된 토큰입니다.") {
        refreshToken();
      } else {
        return response;
      }
    },
  });

  useEffect(() => {
    setMaxPage(data?.pageInfo.totalPage);
  }, []);

  return (
    <ReportLists>
      <ReportList isDark={isDark} colSpan={7}>
        {isLoading ? (
          <Loading />
        ) : (
          <Typotable>
            <thead>
              <TableTr isDark={isDark}>
                <HeaderReportId isDark={isDark}>신고 ID</HeaderReportId>
                <HeaderReportName isDark={isDark}>신고자</HeaderReportName>
                <HeaderReportContent isDark={isDark}>
                  신고 내용
                </HeaderReportContent>
                <HeaderReportDate isDark={isDark}>신고일</HeaderReportDate>
                <HeaderReportTime isDark={isDark}>신고시간</HeaderReportTime>
              </TableTr>
            </thead>
            <tbody>
              {data.data?.map((el: NoticeReportListType) => (
                <TableTr isDark={isDark}>
                  <BodyReportId isDark={isDark}>{el.reportId}</BodyReportId>
                  <BodyReportnName isDark={isDark}>
                    {el.nickname}
                  </BodyReportnName>
                  <BodyReportContent isDark={isDark}>
                    {el.reportContent}
                  </BodyReportContent>
                  <BodyReportDate isDark={isDark}>
                    {el.createdDate.split("T")[0]}
                  </BodyReportDate>
                  <BodyReportTime isDark={isDark}>
                    {el.createdDate.split("T")[1]}
                  </BodyReportTime>
                </TableTr>
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
      </ReportList>
    </ReportLists>
  );
};

export default NoticeReportList;

const globalTokens = tokens.global;

export const ReportLists = styled.tr`
  width: 100%;
`;
export const ReportList = styled.td<DarkMode>`
  width: 100%;
  padding: 30px 50px;
  background-color: ${(props) =>
    props.isDark ? globalTokens.Gray.value : "rgb(230, 230, 230, 0.5)"};
`;

export const Typotable = styled.table`
  margin: 10px 0px 30px 0px;
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
  background-color: white;
  color: ${(props) =>
    props.isDark ? globalTokens.Black.value : globalTokens.Black.value};
  padding: 15px 0px;
  text-align: center;
`;

export const HeaderReportId = styled(Typoth)`
  width: 70px;
`;
export const HeaderReportName = styled(Typoth)`
  width: 170px;
`;
export const HeaderReportContent = styled(Typoth)`
  width: 400px;
`;
export const HeaderReportDate = styled(Typoth)`
  width: 105px;
`;
export const HeaderReportTime = styled(Typoth)`
  width: 105px;
`;

export const BodyReportId = styled(Typotd)`
  width: 70px;
`;
export const BodyReportnName = styled(Typotd)`
  width: 170px;
`;
export const BodyReportContent = styled(Typotd)`
  width: 400px;
`;
export const BodyReportDate = styled(Typotd)`
  width: 105px;
`;
export const BodyReportTime = styled(Typotd)`
  width: 105px;
`;
