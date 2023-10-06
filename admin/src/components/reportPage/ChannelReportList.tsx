import { useState } from "react";
import styled from "styled-components";
import { useSelector } from "react-redux";
import { RootState } from "../../redux/Store";
import { useQuery } from "@tanstack/react-query";
import { getChannelReportList } from "../../services/reprotService";
import { ChannelReportListType } from "../../types/reportDataType";
import Pagination from "../../atoms/pagination/Pagination";
import Loading from "../loading/Loading";
import { useToken } from "../../hooks/useToken";

interface OwnProps {
  memberId: number;
}

const ChannelReportList: React.FC<OwnProps> = ({ memberId }) => {
  const refreshToken = useToken();
  const isDark = useSelector((state: RootState) => state.uiSetting.isDark);
  const accessToken = useSelector(
    (state: RootState) => state.loginInfo.accessToken
  );
  const [currentPage, setCurrentPage] = useState<number>(1);
  const [maxPage, setMaxPage] = useState<number>(10);
  const [isSize, setSize] = useState<number>(5);

  const { isLoading, error, data, isFetching, isPreviousData } = useQuery({
    queryKey: ["ChannelReportList"],
    queryFn: async () => {
      const response = await getChannelReportList(
        accessToken.authorization,
        memberId,
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

  !isLoading && console.log(data);

  return (
    <ReportLists>
      <ReportList colSpan={6}>
        {isLoading ? (
          <Loading />
        ) : (
          <table>
            <thead>
              <tr>
                <HeaderReportId>신고 ID</HeaderReportId>
                <HeaderReportName>신고자</HeaderReportName>
                <HeaderReportContent>신고 내용</HeaderReportContent>
                <HeaderReportDate>신고일자</HeaderReportDate>
              </tr>
            </thead>
            <tbody>
              {data.data.map((el: ChannelReportListType) => (
                <tr>
                  <BodyReportId>{el.reportId}</BodyReportId>
                  <BodyReportnName>{el.nickname}</BodyReportnName>
                  <BodyReportContent>{el.reportContent}</BodyReportContent>
                  <BodyReportDate>{el.createdDate}</BodyReportDate>
                </tr>
              ))}
            </tbody>
          </table>
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

export default ChannelReportList;

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
export const ReportLists = styled.tr`
  width: 100%;
  border: 1px solid red;
`;
export const ReportList = styled.td`
  width: 100%;
  padding: 30px 40px;
  border: 1px solid blue;
`;
export const HeaderReportId = styled(Typoth)`
  width: 70px;
`;
export const HeaderReportName = styled(Typoth)`
  width: 120px;
`;
export const HeaderReportContent = styled(Typoth)`
  width: 450px;
`;
export const HeaderReportDate = styled(Typoth)`
  width: 180px;
`;
export const BodyReportId = styled(Typotd)`
  width: 70px;
`;
export const BodyReportnName = styled(Typotd)`
  width: 120px;
`;
export const BodyReportContent = styled(Typotd)`
  width: 450px;
`;
export const BodyReportDate = styled(Typotd)`
  width: 180px;
`;
