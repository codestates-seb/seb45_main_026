import React, { useEffect } from 'react';
import { PageContainer } from '../../atoms/layouts/PageContainer';
import { useSelector } from 'react-redux';
import { RootState } from '../../redux/Store';
import { useNavigate } from 'react-router-dom';

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
            
        </PageContainer>
    );
};

export default ReportVideoPage;