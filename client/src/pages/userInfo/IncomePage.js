import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { useToken } from '../../hooks/useToken';
import { useInView } from 'react-intersection-observer';
import { PageContainer } from '../../atoms/layouts/PageContainer';
import { ContentNothing, RewardContentContainer, RewardMainContainer, RewardTitle } from './RewardPage';
import RewardCategory from '../../components/rewardPage/RewardCategory';
import { getIncomeService } from '../../services/incomeServices';
import { BottomDiv } from '../contents/LectureListPage';
import IncomeItem from '../../components/incomePage/IncomeItem';
import IncomeHeader from '../../components/incomePage/IncomeHeader';
import IncomeCategory from '../../components/incomePage/IncomeCategory';

const IncomePage = () => {
    const date = new Date();
    const currentYear = date.getFullYear();
    const currentMonth = date.getMonth()+1;
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const accessToken = useSelector(state=>state.loginInfo.accessToken);
    const refreshToken = useToken();
    const [ incomeList, setIncomeList ] = useState([]);
    const [ page, setPage ] = useState(1);
    const [ maxPage, setMaxPage ] = useState(10);
    const [ month, setMonth ] = useState(currentMonth);
    const [ year, setYear ] = useState(currentYear);
    const [ sort, setSort ] = useState('total-sale-amount');
    const [ loading, setLoading ] = useState(true);
    const [ ref, inView ] = useInView();

    //첫 페이지 데이터를 불러옴
    useEffect(()=>{
        if(page>1) return;
        getIncomeService({
            authorization: accessToken.authorization,
            page: page,
            size: 20,
            month: month,
            year: year,
            sort: sort,
        }).then((res)=>{
            if(res.status==='success') {
                console.log(res.data);
                setIncomeList(res.data.data);
                setMaxPage(res.data.pageInfo.totalPage);
                setLoading(false);
            } else if(res.data==='만료된 토큰입니다.') {
                refreshToken();
            } else {
                console.log(res.data);
            }
        })
    },[year, month, accessToken]);

    //페이지값이 증가하면 새로운 데이터를 불러옴
    useEffect(()=>{
        if(page>1) {
            getIncomeService({
                authorization: accessToken.authorization,
                page: page,
                size: 20,
                month: month,
                year: year,
                sort: sort,
            }).then((res)=>{
                if(res.status==='success') {
                    setIncomeList([
                        ...incomeList,
                        ...res.data.data,
                    ]);
                    setLoading(false);
                } else {
                    console.log(res.data)
                }
            })
        }
    },[page]);

    //바닥 요소가 보이면 현재 페이지 값을 1 증가
    useEffect(()=>{
        if(inView && maxPage>page) {
            setLoading(true);
            setPage(page+1);
        }
    },[inView])

    return (
        <PageContainer isDark={isDark}>
            <RewardMainContainer isDark={isDark}>
                <RewardTitle isDark={isDark}>나의 활동</RewardTitle>
                <RewardCategory category='income'/>
                <RewardContentContainer>
                { incomeList.length===0 &&
                    <ContentNothing isDark={isDark}>정산 내역이 없습니다.</ContentNothing> }
                { incomeList.length>0 && 
                    <IncomeCategory year={year} setYear={setYear} month={month} setMonth={setMonth}/>}
                { incomeList.length>0 && 
                    <IncomeHeader/> } 
                { incomeList.length>0 && 
                    incomeList.map((e)=><IncomeItem key={e.videoId} item={e}/>) }
                </RewardContentContainer>
                { !loading && <BottomDiv ref={ref}/> }
            </RewardMainContainer>
        </PageContainer>
    );
};

export default IncomePage;