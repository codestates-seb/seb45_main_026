import React, { useEffect, useMemo, useRef, useState } from 'react';
import { PageContainer } from '../../atoms/layouts/PageContainer';
import { useSelector } from 'react-redux';
import { ContentNothing, RewardContentContainer, RewardMainContainer, RewardTitle } from './RewardPage';
import RewardCategory from '../../components/rewardPage/RewardCategory';
import ReceiptItem from '../../components/receiptPage/ReceiptItem';
import { getReceiptService } from '../../services/receiptServices';
import { useToken } from '../../hooks/useToken';

const ReceiptPage = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const accessToken = useSelector(state=>state.loginInfo.accessToken);
    const refreshToken = useToken();
    const [ receiptList, setReceiptList ] = useState([]);
    const [ month, setMonth ] = useState(1);
    let [ page, setPage ] = useState(1);
    const target = useRef(null);

    useEffect(()=>{
        if(page>1) return;
        getReceiptService(
            accessToken.authorization, page, 20, month
        ).then((res)=> {
            if(res.status==='success') {
                setReceiptList(res.data.data);
            } else if(res.data==='만료된 토큰입니다.') {
                refreshToken();
            } else {
                console.log(res);
            }
        })
    },[accessToken]);

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
                <div>나는 관측 대상</div>
            </RewardMainContainer>
        </PageContainer>
    );
};

export default ReceiptPage;