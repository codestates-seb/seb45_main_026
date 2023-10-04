import React, { useEffect, useState } from "react";
import {
  MainContainer,
  PageContainer,
} from "../../atoms/layouts/PageContainer";
import { useSelector } from "react-redux";
import { RootState } from "../../redux/Store";
import { useNavigate } from "react-router-dom";
import { PageTitle } from "../../styles/PageTitle";
import { useQuery } from "@tanstack/react-query";
import { getReportVideoList } from "../../services/reprotService";
import Pagination from "../../atoms/pagination/Pagination";
import { reportVideoDataType } from "../../types/reportDataType";
import Loading from "../../components/loading/Loading";
import styled from "styled-components";
import NavBar from "../../components/navBar/NavBar";

const ReportVideoPage = () => {
  const navigate = useNavigate();
  const isDark = useSelector((state: RootState) => state.uiSetting.isDark);
  const isLogin = useSelector((state: RootState) => state.loginInfo.isLogin);
  const accessToken = useSelector(
    (state: RootState) => state.loginInfo.accessToken
  );

  const [currentPage, setCurrentPage] = useState<number>(1);
  const [maxPage, setMaxPage] = useState<number>(10);
  const [isSize, setSize] = useState<number>(10);
  const [isSort, setSort] = useState<string>("last-reported-date");

  const { isLoading, error, data, isFetching } = useQuery({
    queryKey: ["videos"],
    queryFn: async () => {
      const response = await getReportVideoList(
        accessToken.authorization,
        currentPage,
        isSize,
        isSort
      );
      return response;
    },
  });

  console.log(data);

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
        {isLoading ? (
          <Loading />
        ) : (
          <table>
            <thead>
              <tr>
                <TypothId>강의 ID</TypothId>
                <TypothVideoName>
                  <div>강의 제목</div>
                  {/* <div>(생성 날짜)</div> */}
                </TypothVideoName>
                <TypothVideoStatus>강의 상태</TypothVideoStatus>
                <TypothReportCount>신고 횟수</TypothReportCount>
                <TypothLastDate>최근 신고 날짜</TypothLastDate>
              </tr>
            </thead>
            <tbody>
              {data.data.map((el: reportVideoDataType) => (
                <tr key={el.videoId}>
                  <TypotdId>{el.videoId}</TypotdId>
                  <TypotdVideoName>{el.videoName}</TypotdVideoName>
                  <TypotdVideoStatus>{el.videoStatus}</TypotdVideoStatus>
                  <TypotdReportCount>{el.reportCount}회</TypotdReportCount>
                  <TypotdLastDate>{el.lastReportedDate}</TypotdLastDate>
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
      </MainContainer>
    </PageContainer>
  );
};

export default ReportVideoPage;

const TypothId = styled.th`
  width: 100px;
  border: 1px solid black;
`;
const TypothVideoName = styled.th`
  width: 200px;
  border: 1px solid black;
`;
const TypothVideoStatus = styled.th`
  width: 100px;
  border: 1px solid black;
`;
const TypothReportCount = styled.th`
  width: 80px;
  border: 1px solid black;
`;
const TypothLastDate = styled.th`
  width: 200px;
  border: 1px solid black;
`;
const TypotdId = styled.td`
  width: 100px;
  text-align: center;
  border: 1px solid black;
`;
const TypotdVideoName = styled.td`
  width: 200px;
  text-align: center;
  border: 1px solid black;
`;
const TypotdVideoStatus = styled.td`
  width: 100px;
  text-align: center;
  border: 1px solid black;
`;
const TypotdReportCount = styled.td`
  width: 80px;
  text-align: center;
  border: 1px solid black;
`;
const TypotdLastDate = styled.td`
  width: 200px;
  text-align: center;
  border: 1px solid black;
`;
