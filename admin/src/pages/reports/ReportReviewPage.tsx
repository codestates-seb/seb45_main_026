import React from 'react';
import { PageContainer } from '../../atoms/layouts/PageContainer';
import { useSelector } from 'react-redux';
import { RootState } from '../../redux/Store';

const ReportReviewPage = () => {
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);

    return (
        <PageContainer isDark={isDark}></PageContainer>
    );
};

export default ReportReviewPage;