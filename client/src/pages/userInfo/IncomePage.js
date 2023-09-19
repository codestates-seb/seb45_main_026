import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import { useToken } from '../../hooks/useToken';
import { useInView } from 'react-intersection-observer';
import { PageContainer } from '../../atoms/layouts/PageContainer';
import { ContentNothing, RewardContentContainer, RewardMainContainer, RewardTitle } from './RewardPage';
import RewardCategory from '../../components/rewardPage/RewardCategory';

const IncomePage = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const accessToken = useSelector(state=>state.loginInfo.accessToken);
    const refreshToken = useToken();
    const [ incomeList, setIncomeList ] = useState([]);
    const [ page, setPage ] = useState(1);
    const [ maxPage, setMaxPage ] = useState(10);
    const [ month, setMonth ] = useState(1);
    const [ loading, setLoading ] = useState(true);
    const [ ref, inView ] = useInView();

    return (
        <PageContainer isDark={isDark}>
            <RewardMainContainer isDark={isDark}>
                <RewardTitle isDark={isDark}>나의 활동</RewardTitle>
                <RewardCategory category='income'/>
                <RewardContentContainer>
                { incomeList.length===0 &&
                    <ContentNothing isDark={isDark}>정산 내역이 없습니다.</ContentNothing> }
                </RewardContentContainer>
            </RewardMainContainer>
        </PageContainer>
    );
};

export default IncomePage;