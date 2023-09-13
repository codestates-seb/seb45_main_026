import React, { useEffect, useMemo, useRef, useState } from 'react';
import { PageContainer } from '../../atoms/layouts/PageContainer';
import { useSelector } from 'react-redux';
import { ContentNothing, RewardContentContainer, RewardMainContainer, RewardTitle } from './RewardPage';
import RewardCategory from '../../components/rewardPage/RewardCategory';
import ReceiptItem from '../../components/receiptPage/ReceiptItem';
import { getReceiptService } from '../../services/receiptServices';
import { useToken } from '../../hooks/useToken';
import { useInView } from 'react-intersection-observer';
import { BottomDiv } from '../../pages/contents/LectureListPage';

const ReceiptPage = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const accessToken = useSelector(state=>state.loginInfo.accessToken);
    const refreshToken = useToken();
    const [ receiptList, setReceiptList ] = useState([]);
    let [ page, setPage ] = useState(1);
    const [ month, setMonth ] = useState(1);
    const [ loading, setLoading ] = useState(true);
    const [ ref, inView ] = useInView();

    //첫 페이지 데이터를 불러옴
    useEffect(()=>{
        console.log('첫 페이지 불러옴 ')
        if(page>1) return;
        getReceiptService(
            accessToken.authorization, page, 20, month
        ).then((res)=> {
            if(res.status==='success') {
                setReceiptList(res.data.data);
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
            console.log(page)
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
                    console.log(res);
                }
            });
        }
    },[page]);

    //바닥 요소가 보이면 현재 페이지 값을 1 증가
    useEffect(()=>{
        console.log(`inview 변경됨 : ${inView}`)
        if(inView) {
            setLoading(true);
            setPage(page+1);
        }
    },[inView]);

    return (
        <PageContainer isDark={isDark}>
            <RewardMainContainer isDark={isDark}>
                <RewardTitle isDark={isDark}>나의 활동</RewardTitle>
                <RewardCategory category='receipt'/>
                <RewardContentContainer>
                { receiptList.length===0 &&
                    <ContentNothing>결제 내역이 없습니다.</ContentNothing> }
                { receiptList.length>0 && 
                    receiptList.map((e, idx)=>{ 
                        return <ReceiptItem key={e.orderId} item={e} idx={idx}/>})
                }
                </RewardContentContainer>
                { !loading && <BottomDiv ref={ref}>바닥 요소</BottomDiv> }
            </RewardMainContainer>
        </PageContainer>
    );
};

export default ReceiptPage;