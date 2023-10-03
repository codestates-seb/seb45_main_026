import React, { useEffect } from 'react';
import { MainContainer, PageContainer } from '../../atoms/layouts/PageContainer';
import { useSelector } from 'react-redux';
import { RootState } from '../../redux/Store';
import { useNavigate } from 'react-router-dom';
import { PageTitle } from '../../styles/PageTitle';

const ReportVideoPage = () => {
    const navigate = useNavigate();
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);
    const isLogin = useSelector((state:RootState)=>state.loginInfo.isLogin);

    useEffect(()=>{
        if(!isLogin) { 
            navigate('/login'); 
            return;
        }
    },[]);

    return (
        <PageContainer isDark={isDark}>
            <MainContainer isDark={isDark}>
                <PageTitle isDark={isDark}>신고 내역 관리</PageTitle>
            </MainContainer>
        </PageContainer>
    );
};

export default ReportVideoPage;