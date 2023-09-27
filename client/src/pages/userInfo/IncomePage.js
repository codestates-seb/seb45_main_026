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
} from "recharts";

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
  const data = [
    { name: "Page A", uv: 400, pv: 2400, amt: 2400 },
    { name: "Page B", uv: 200, pv: 2400, amt: 2400 },
    { name: "Page C", uv: 300, pv: 2400, amt: 2400 },
    { name: "Page D", uv: 100, pv: 2400, amt: 2400 },
    { name: "Page E", uv: 500, pv: 2400, amt: 2400 },
  ];

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
        console.log(res.data);
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
          <IncomeCategory
            year={year}
            setYear={setYear}
            month={month}
            setMonth={setMonth}
          />

          <LineChart
            width={600}
            height={300}
            data={data}
            margin={{ top: 5, right: 20, bottom: 5, left: 0 }}
          >
            <Line type="monotone" dataKey="uv" stroke="#8884d8" />
            <CartesianGrid stroke="#ccc" strokeDasharray="5 5" />
            <XAxis dataKey="name" />
            <YAxis />
            <Tooltip />
          </LineChart>

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
