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
import {
  ReceiptAmountTypo,
  ReceiptItemContainer,
  ReceiptTitleTypo,
} from "../../components/receiptPage/ReceiptItem.style";
import {
  IncomeHeadContainer,
  IncomeAmountHeadTypo,
  IncomeIndexHeadTypo,
} from "../../components/incomePage/IncomeHeader";
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
  PieChart,
  Pie,
  Sector,
  Cell,
} from "recharts";
import axios from "axios";
import styled from "styled-components";
import { SmallTextTypo } from "../../atoms/typographys/Typographys";
import tokens from "../../styles/tokens.json";

const globalTokens = tokens.global;

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
  const [month, setMonth] = useState(null); // 초기값 원래 currentMonth였음.
  const [year, setYear] = useState(currentYear);
  const [sort, setSort] = useState("total-sale-amount");
  const [loading, setLoading] = useState(true);
  const [ref, inView] = useInView();

  // 차트 데이터 변환 (화면 랜더링)
  const tickFormatX = (tickItem) => (tickItem ? tickItem + "월" : "");
  const tickFormatY = (tickItem) => tickItem.toLocaleString();
  const formatTooltip = (tickItem) => tickItem.toLocaleString() + "원";

  // 차트 데이터 값
  const [isYearData, setYearData] = useState([
    { year: 0, month: 1, amount: 0 },
    { year: 0, month: 2, amount: 0 },
    { year: 0, month: 3, amount: 0 },
    { year: 0, month: 4, amount: 0 },
    { year: 0, month: 5, amount: 0 },
    { year: 0, month: 6, amount: 0 },
    { year: 0, month: 7, amount: 0 },
    { year: 0, month: 8, amount: 0 },
    { year: 0, month: 9, amount: 0 },
    { year: 0, month: 10, amount: 0 },
    { year: 0, month: 11, amount: 0 },
    { year: 0, month: 12, amount: 0 },
  ]);
  const getAdjustmentYear = () => {
    return axios
      .get(
        `https://api.itprometheus.net/adjustments/total-adjustment?year=${year}`
      )
      .then((res) => {
        // console.log(`${year}년 정산 상태`, res.data.data.adjustmentStatus);
        // console.log(`${year}년 정산 금액`, res.data.data.amount);

        const monthData = res.data.data.monthData;
        const newYearData = isYearData.map((el) => {
          const yearData = monthData.find((num) => num.month === el.month);
          if (yearData) {
            return yearData;
          } else {
            return el;
          }
        });
        setYearData(newYearData);
      })
      .catch((err) => {
        console.log(err);
      });
  };

  const [isMonthData, setMonthData] = useState([{ videoName: "", profit: 0 }]);
  const getAdjustmentMonth = () => {
    return axios
      .get(
        `https://api.itprometheus.net/adjustments/total-adjustment?month=${month}&year=${year}`
      )
      .then((res) => {
        // console.log(`${month}월 정산 내역`, res.data.data);
        // console.log(`${month}월 정산된 금액`, res.data.data.amount);
        // console.log(`${month}월 정산 상태`, res.data.data.adjustmentStatus);
        // console.log("이유", res.data.data.reason);
      })
      .catch((err) => {
        console.log(err);
      });
  };

  const getMonthVideoData = () => {
    return axios
      .get(
        `https://api.itprometheus.net/adjustments/videos?year=${year}&month=${month}`
      )
      .then((res) => {
        // console.log("videos", res.data);
      })
      .catch((err) => {
        console.log(err);
      });
  };

  // 연도 or 월별 정산 내역
  useEffect(() => {
    if (year === null) {
    } else if (month === null) {
      getAdjustmentYear();
    } else {
      getAdjustmentMonth();
      getMonthVideoData();
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
            portion:
              (el.totalSaleAmount - el.refundAmount) / el.totalSaleAmount,
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

  const [isPointer, setPointer] = useState(0);
  const COLORS = ["#FF8042", "#FFBB28", "#00C49F", "#0088FE"];
  const renderActiveShape = (props) => {
    const RADIAN = Math.PI / 180;
    const {
      cx,
      cy,
      midAngle,
      innerRadius,
      outerRadius,
      startAngle,
      endAngle,
      fill,
      payload,
      percent,
      value,
    } = props;
    const sin = Math.sin(-RADIAN * midAngle);
    const cos = Math.cos(-RADIAN * midAngle);
    const sx = cx + (outerRadius + 10) * cos;
    const sy = cy + (outerRadius + 10) * sin;
    const mx = cx + (outerRadius + 30) * cos;
    const my = cy + (outerRadius + 30) * sin;
    const ex = mx + (cos >= 0 ? 1 : -1) * 22;
    const ey = my;
    const textAnchor = cos >= 0 ? "start" : "end";

    return (
      <g>
        <text
          x={cx}
          y={cy}
          dy={8}
          textAnchor="middle"
          fill={COLORS[isPointer % COLORS.length]}
          style={{
            fontSize: "18px",
            fontWeight: "600",
          }}
        >
          {payload.videoName}
        </text>
        <Sector
          cx={cx}
          cy={cy}
          innerRadius={innerRadius}
          outerRadius={outerRadius}
          startAngle={startAngle}
          endAngle={endAngle}
          fill={COLORS[isPointer % COLORS.length]}
        />
        <Sector
          cx={cx}
          cy={cy}
          startAngle={startAngle}
          endAngle={endAngle}
          innerRadius={outerRadius + 6}
          outerRadius={outerRadius + 10}
          fill={COLORS[isPointer % COLORS.length]}
        />
        <path
          d={`M${sx},${sy}L${mx},${my}L${ex},${ey}`}
          stroke={COLORS[isPointer % COLORS.length]}
          fill="none"
        />
        <circle
          cx={ex}
          cy={ey}
          r={2}
          fill={COLORS[isPointer % COLORS.length]}
          stroke="none"
        />
        <text
          x={ex + (cos >= 0 ? 1 : -1) * 12}
          y={ey}
          textAnchor={textAnchor}
          fill={isDark ? globalTokens.White.value : globalTokens.Black.value}
        >{`총 수익 : ${value.toLocaleString()}원`}</text>
        <text
          x={ex + (cos >= 0 ? 1 : -1) * 12}
          y={ey}
          dy={18}
          textAnchor={textAnchor}
          fill="#999"
        >
          {`(판매 비율 ${(percent * 100).toFixed(2)}%)`}
        </text>
      </g>
    );
  };

  return (
    <PageContainer isDark={isDark}>
      <RewardMainContainer isDark={isDark}>
        <RewardTitle isDark={isDark}>나의 활동</RewardTitle>
        <RewardCategory category="income" />
        <RewardContentContainer>
          <IncomeCategory
            year={year}
            setYear={setYear}
            month={month}
            setMonth={setMonth}
          />
          {year !== null &&
            (month === null ? (
              <ChartBox>
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart
                    width={1000}
                    height={500}
                    data={[{}, ...isYearData, {}]}
                    margin={{ top: 35, right: 20, bottom: 5, left: 0 }}
                  >
                    <Bar
                      dataKey="amount"
                      fill={isDark ? "#8884d8" : globalTokens.Negative.value}
                      onClick={(el) => setMonth(el.month)}
                    />
                    <CartesianGrid
                      stroke="#ccc"
                      strokeDasharray="3 3"
                      vertical={false}
                    />
                    <XAxis
                      dataKey="month"
                      tickFormatter={tickFormatX}
                      tickLine={false}
                      // axisLine={false}
                      tick={{
                        fill: `${
                          isDark
                            ? globalTokens.White.value
                            : globalTokens.Black.value
                        }`,
                      }}
                    />
                    <YAxis
                      dataKey="amount"
                      tickFormatter={tickFormatY}
                      tickLine={false}
                      // axisLine={false}
                      tick={{
                        fill: `${
                          isDark
                            ? globalTokens.White.value
                            : globalTokens.Black.value
                        }`,
                      }}
                      label={{
                        value: "(원)",
                        angle: 0,
                        offset: 20,
                        position: "top",
                        fill: `${
                          isDark
                            ? globalTokens.White.value
                            : globalTokens.Black.value
                        }`,
                      }}
                    ></YAxis>
                    <Tooltip
                      cursor={{
                        strokeDasharray: "3 3",
                        fill: isDark
                          ? "rgb(136,132,216, 0.3)"
                          : "rgb(255,204,204, 0.3)",
                      }}
                      formatter={formatTooltip}
                      labelFormatter={tickFormatX}
                      // content={} // tooltip을 커스텀해서 랜더링 가능
                    />
                    <Legend
                      formatter={(el) => {
                        return "정산 금액";
                      }}
                      // content={} // Legend를 커스텀해서 랜더링 가능
                    />
                  </BarChart>
                </ResponsiveContainer>
              </ChartBox>
            ) : (
              <ChartBox>
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart width={1000} height={500}>
                    <Pie
                      activeIndex={isPointer}
                      activeShape={renderActiveShape}
                      data={isMonthData}
                      dataKey="profit"
                      nameKey="videoName"
                      cx="50%"
                      cy="50%"
                      innerRadius={135}
                      outerRadius={180}
                      fill={
                        isDark
                          ? globalTokens.Gray.value
                          : globalTokens.LightGray.value
                      }
                      onMouseEnter={(e, idx) => {
                        setPointer(idx);
                      }}
                      onClick={(event) => console.log(event)}
                    />
                  </PieChart>
                </ResponsiveContainer>
              </ChartBox>
            ))}

          {month === null ? (
            <ChartGridBox>
              <HeadContainer isDark={isDark}>
                <MonthHeadTypo isDark={isDark}>월</MonthHeadTypo>
                <AmountHeadTypo isDark={isDark}>정산 금액(원)</AmountHeadTypo>
                <StatusHeadContainer isDark={isDark}>
                  정산 상태
                </StatusHeadContainer>
                <ReasonHeadTypo isDark={isDark}>비고</ReasonHeadTypo>
              </HeadContainer>
              <IncomeItemContainer isDark={isDark}>
                <IncomeMonthTypo isDark={isDark}>1월</IncomeMonthTypo>
                <IncomeAmountTypo isDark={isDark}>10000</IncomeAmountTypo>
                <IncomeStatusTypo isDark={isDark}>정산 중</IncomeStatusTypo>
                <IncomeReasonTypo isDark={isDark}>
                  정산 처리 중 입니다.
                </IncomeReasonTypo>
              </IncomeItemContainer>
            </ChartGridBox>
          ) : incomeList.length > 0 ? (
            <ChartGridBox>
              <IncomeHeader />
              {incomeList.map((e) => (
                <IncomeItem key={e.videoId} item={e} />
              ))}
            </ChartGridBox>
          ) : (
            <ContentNothing isDark={isDark}>
              정산 내역이 없습니다.
            </ContentNothing>
          )}
        </RewardContentContainer>
        {!loading && <BottomDiv ref={ref} />}
      </RewardMainContainer>
    </PageContainer>
  );
};

export default IncomePage;

export const ChartGridBox = styled.div`
  width: 100%;
  min-height: 500px;
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: center;
`;

export const ChartBox = styled.div`
  width: 100%;
  max-width: 800px;
  aspect-ratio: 1.8/1;
  margin: 20px 0px;
`;

export const Chartgrid = styled.div`
  min-height: 500px;
`;
export const HeadContainer = styled(IncomeHeadContainer)``;
export const MonthHeadTypo = styled(IncomeIndexHeadTypo)`
  text-align: center;
  width: 70px;
`;
export const AmountHeadTypo = styled(IncomeAmountHeadTypo)`
  width: 200px;
`;
export const StatusHeadContainer = styled(IncomeAmountHeadTypo)`
  width: 180px;
`;
export const ReasonHeadTypo = styled(IncomeAmountHeadTypo)`
  width: 100%;
  max-width: 500px;
`;

export const IncomeItemContainer = styled(ReceiptItemContainer)``;
export const IncomeMonthTypo = styled(ReceiptTitleTypo)`
  text-align: center;
  width: 70px;
`;
export const IncomeAmountTypo = styled(ReceiptAmountTypo)`
  width: 200px;
`;
export const IncomeStatusTypo = styled(ReceiptAmountTypo)`
  width: 180px;
`;
export const IncomeReasonTypo = styled(ReceiptAmountTypo)`
  width: 100%;
  max-width: 500px;
`;
