import React from 'react';
import { PageContainer } from '../../atoms/layouts/PageContainer';
import { useSelector } from 'react-redux';

const RewardPage = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    
    return (
        <PageContainer isDark={isDark}>
            This is RewardPage
        </PageContainer>
    );
};

export default RewardPage;