import React from 'react';
import { PageContainer } from '../../atoms/layouts/PageContainer';
import { useSelector } from 'react-redux';
import { ContentNothing, RewardContentContainer, RewardMainContainer, RewardTitle } from './RewardPage';
import RewardCategory from '../../components/rewardPage/RewardCategory';

const ReceiptPage = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <PageContainer isDark={isDark}>
            <RewardMainContainer isDark={isDark}>
                <RewardTitle isDark={isDark}>나의 활동</RewardTitle>
                <RewardCategory category='receipt'/>
                <RewardContentContainer>
                    <ContentNothing>결제 내역이 없습니다.</ContentNothing>
                </RewardContentContainer>
            </RewardMainContainer>
        </PageContainer>
    );
};

export default ReceiptPage;