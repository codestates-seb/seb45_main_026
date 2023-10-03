import React from 'react';
import { MainContainer, PageContainer } from '../atoms/layouts/PageContainer';
import { useSelector } from 'react-redux';
import { RootState } from '../redux/Store';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { getMemberList } from '../services/memberService';
import axios from 'axios';
import { ROOT_URL } from '../services';
import NavBar from '../components/navBar/NavBar';
import { PageTitle } from '../styles/PageTitle';
import Loading from '../components/loading/Loading';

const MemberPage = () => {
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);
    const accessToken = useSelector((state:RootState)=>state.loginInfo.accessToken);

    const { isLoading, error, data, isFetching } = useQuery({ 
        queryKey: ['members'], 
        queryFn: async ()=>{
            const res = await getMemberList(accessToken.authorization,'',1,10);
            return res.data;
        }});

    console.log(data)

    return (
        <PageContainer isDark={isDark}>
            <MainContainer isDark={isDark}>
                <PageTitle isDark={isDark}>회원 관리</PageTitle>
                { isLoading ? <Loading/> 
                  : error ? <>error</>
                  : null }
            </MainContainer>
        </PageContainer>
    );
};

export default MemberPage;