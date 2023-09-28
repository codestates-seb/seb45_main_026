import React, { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { useToken } from "../../hooks/useToken";
import { useInView } from "react-intersection-observer";
import { PageContainer } from "../../atoms/layouts/PageContainer";
import {
  ContentNothing,
  RewardContentContainer,
  RewardMainContainer,
  RewardTitle,
} from "./RewardPage";
import RewardCategory from "../../components/rewardPage/RewardCategory";
import { getIncomeService } from "../../services/incomeServices";
import { BottomDiv } from "../contents/LectureListPage";
import IncomeItem from "../../components/incomePage/IncomeItem";
import IncomeHeader from "../../components/incomePage/IncomeHeader";
import IncomeCategory from "../../components/incomePage/IncomeCategory";
import {
  LineChart,
  Line,
  CartesianGrid,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  BarChart,
  Legend,
  Bar,
} from "recharts";
import axios from "axios";
import styled from "styled-components";

const IncomePage = () => {
  const date = new Date();
  const currentYear = date.getFullYear();
  const currentMonth = date.getMonth() + 1;
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const accessToken = useSelector((state) => state.loginInfo.accessToken);
  const refreshToken = useToken();
  const [incomeList, setIncomeList] = useState([]);
  const [page, setPage] = useState(1);
  const [maxPage, setMaxPage] = useState(10);
  const [month, setMonth] = useState(currentMonth);
  const [year, setYear] = useState(currentYear);
  const [sort, setSort] = useState("total-sale-amount");
  const [loading, setLoading] = useState(true);
  const [ref, inView] = useInView();

  // 차트 데이터 값
  const yearData = [
    { year: month, month: 1, amount: 0 },
    { year: month, month: 2, amount: 0 },
    { year: month, month: 3, amount: 0 },
    { year: month, month: 4, amount: 0 },
    { year: month, month: 5, amount: 0 },
    { year: month, month: 6, amount: 0 },
    { year: month, month: 7, amount: 0 },
    { year: month, month: 8, amount: 0 },
    { year: month, month: 9, amount: 0 },
    { year: month, month: 10, amount: 0 },
    { year: month, month: 11, amount: 0 },
    { year: month, month: 12, amount: 0 },
  ];
  const monthData = [{ videoName: "", profit: 0 }];
  const [isYearData, setYearData] = useState(yearData);
  const [isMonthData, setMonthData] = useState(monthData);

  const getAdjustmentMonth = () => {
    return axios
      .get(
        `https://api.itprometheus.net/adjustments/total-adjustment?month=${month}&year=${year}`
      )
      .then((res) => {
        console.log("Month", res.data.data.monthData);
        const adjustData = res.data.data.monthData;
        const newData = yearData.map((el) => {
          for (let i = 0; i < adjustData.length; i++) {
            if (el.month === adjustData[i].month) {
              return { ...el, ...adjustData[i] };
            } else {
              return el;
            }
          }
        });
        setYearData(newData);
      })
      .catch((err) => {
        console.log(err);
      });
  };

  const getAdjustmentYear = () => {
    return axios
      .get(
        `https://api.itprometheus.net/adjustments/total-adjustment?year=${year}`
      )
      .then((res) => {
        console.log("Year", res.data.data.monthData);
        const adjustData = res.data.data.monthData;
        const newData = yearData.map((el) => {
          for (let i = 0; i < adjustData.length; i++) {
            if (el.month === adjustData[i].month) {
              return { ...el, ...adjustData[i] };
            } else {
              return el;
            }
          }
        });
        setYearData(newData);
      })
      .catch((err) => {
        console.log(err);
      });
  };

  const getMonth = () => {
    return axios
      .get(
        `https://api.itprometheus.net/adjustments/videos?year=${year}&month=${month}`
      )
      .then((res) => {
        console.log(res.data.data);
      })
      .catch((err) => {
        console.log(err);
      });
  };

  // 연도 or 월별 정산 내역
  useEffect(() => {
    if (!year) {
      return;
    } else if (!month) {
      getAdjustmentYear();
    } else {
      getAdjustmentMonth();
    }
  }, [month, year]);

  //첫 페이지 데이터를 불러옴
  useEffect(() => {
    if (page > 1) return;
    getIncomeService({
      authorization: accessToken.authorization,
      page: page,
      size: 20,
      month: month,
      year: year,
      sort: sort,
    }).then((res) => {
      if (res.status === "success") {
        const adjustData = res.data.data;
        const newMonthData = adjustData.map((el) => {
          return {
            videoName: el.videoName,
            profit: el.totalSaleAmount - el.refundAmount,
          };
        });
        setMonthData(newMonthData);
        setIncomeList(res.data.data);
        setMaxPage(res.data.pageInfo.totalPage);
        setLoading(false);
      } else if (res.data === "만료된 토큰입니다.") {
        refreshToken();
      } else {
        console.log(res.data);
      }
    });
  }, [year, month, accessToken]);

  //페이지값이 증가하면 새로운 데이터를 불러옴
  useEffect(() => {
    if (page > 1) {
      getIncomeService({
        authorization: accessToken.authorization,
        page: page,
        size: 20,
        month: month,
        year: year,
        sort: sort,
      }).then((res) => {
        if (res.status === "success") {
          setIncomeList([...incomeList, ...res.data.data]);
          setLoading(false);
        } else {
          console.log(res.data);
        }
      });
    }
  }, [page]);

  //바닥 요소가 보이면 현재 페이지 값을 1 증가
  useEffect(() => {
    if (inView && maxPage > page) {
      setLoading(true);
      setPage(page + 1);
    }
  }, [inView]);

  useEffect(() => {
    window.scrollTo({
      top: 0,
    });
  }, []);

  return (
    <PageContainer isDark={isDark}>
      <RewardMainContainer isDark={isDark}>
        <RewardTitle isDark={isDark}>나의 활동</RewardTitle>
        <RewardCategory category="income" />
        <RewardContentContainer>
          <ChartBox>
            <ResponsiveContainer width="100%" height="100%">
              <LineChart
                width={800}
                height={300}
                data={isYearData}
                margin={{ top: 5, right: 20, bottom: 5, left: 0 }}
              >
                <Line dataKey="amount" stroke="#8884d8" />
                <CartesianGrid stroke="#ccc" strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis dataKey="amount" />
                <Tooltip />
                <Legend />
              </LineChart>
            </ResponsiveContainer>
          </ChartBox>

          <ChartBox>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart
                width={800}
                height={300}
                data={isMonthData}
                margin={{ top: 5, right: 20, bottom: 5, left: 0 }}
              >
                <Bar dataKey="profit" fill="#8884d8" />
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="videoName" tickSize={10} />
                <YAxis dataKey="profit" />
                <Tooltip />
                <Legend />
              </BarChart>
            </ResponsiveContainer>
          </ChartBox>

          <IncomeCategory
            year={year}
            setYear={setYear}
            month={month}
            setMonth={setMonth}
          />

          {incomeList.length === 0 && (
            <ContentNothing isDark={isDark}>
              정산 내역이 없습니다.
            </ContentNothing>
          )}
          {incomeList.length > 0 && <IncomeHeader />}
          {incomeList.length > 0 &&
            incomeList.map((e) => <IncomeItem key={e.videoId} item={e} />)}
        </RewardContentContainer>
        {!loading && <BottomDiv ref={ref} />}
      </RewardMainContainer>
    </PageContainer>
  );
};

export default IncomePage;

export const ChartBox = styled.div`
  width: 100%;
  max-width: 800px;
  aspect-ratio: 2.2/1;
`;
