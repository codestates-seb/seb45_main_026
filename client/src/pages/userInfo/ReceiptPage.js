import React, { useEffect, useState } from 'react';
import { PageContainer } from '../../atoms/layouts/PageContainer';
import { useSelector } from 'react-redux';
import { 
    ContentNothing, 
    RewardContentContainer, 
    RewardMainContainer, 
    RewardTitle } from './RewardPage';
import RewardCategory from '../../components/rewardPage/RewardCategory';
import ReceiptItem from '../../components/receiptPage/ReceiptItem';
import { getReceiptService } from '../../services/receiptServices';
import { useToken } from '../../hooks/useToken';
import { useInView } from 'react-intersection-observer';
import { BottomDiv } from '../../pages/contents/LectureListPage';
import ReceiptDropdown from '../../components/receiptPage/ReceiptDropdown';
import ReceiptListHeader from '../../components/receiptPage/ReceiptListHeader';

const ReceiptPage = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const accessToken = useSelector(state=>state.loginInfo.accessToken);
    const refreshToken = useToken();
    const [ receiptList, setReceiptList ] = useState([]);
    const [ page, setPage ] = useState(1);
    const [ maxPage, setMaxPage ] = useState(10);
    const [ month, setMonth ] = useState(1);
    const [ loading, setLoading ] = useState(true);
    const [ ref, inView ] = useInView();

    //첫 페이지 데이터를 불러옴
    useEffect(()=>{
        if(page>1) return;
        getReceiptService(
            accessToken.authorization, page, 20, month
        ).then((res)=> {
            if(res.status==='success') {
                setReceiptList(res.data.data);
                setMaxPage(res.data.pageInfo.totalPage);
                setLoading(false);
            } else if(res.data==='만료된 토큰입니다.') {
                refreshToken();
            } else {
                console.log(res);
            }
        })
    },[month, accessToken]);

    //페이지값이 증가하면 새로운 데이터를 불러옴
    useEffect(()=>{
        if(page>1) {
            getReceiptService(
                accessToken.authorization, page, 20, month
            ).then((res)=>{
                if(res.status==='success') {
                    setReceiptList([
                        ...receiptList,
                        ...res.data.data
                    ]);
                    setLoading(false);
                } else {
                    console.log(res.data);
                }
            });
        }
    },[page]);

    //바닥 요소가 보이면 현재 페이지 값을 1 증가
    useEffect(()=>{
        if(inView && maxPage>page) {
            setLoading(true);
            setPage(page+1);
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
                <RewardCategory category='receipt'/>
                <RewardContentContainer>
                { receiptList.length===0 &&
                    <ContentNothing>결제 내역이 없습니다.</ContentNothing> }
                { receiptList.length>0 && 
                    <ReceiptDropdown category={month} setCategory={setMonth}/> }
                { receiptList.length>0 && <ReceiptListHeader/> }
                { receiptList.length>0 && 
                    receiptList.map((e, idx)=>{ 
                        return <ReceiptItem key={e.orderId} item={e} idx={idx}/>})
                }
                </RewardContentContainer>
                { !loading && <BottomDiv ref={ref}/> }
            </RewardMainContainer>
        </PageContainer>
    );
};

export default ReceiptPage;