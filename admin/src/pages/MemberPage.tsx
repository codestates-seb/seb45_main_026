import React, { useEffect, useState } from 'react';
import { MainContainer, PageContainer, TableContainer } from '../atoms/layouts/PageContainer';
import { useSelector } from 'react-redux';
import { RootState } from '../redux/Store';
import { useQuery } from '@tanstack/react-query';
import { getMemberList } from '../services/memberService';
import { PageTitle } from '../styles/PageTitle';
import Loading from '../components/loading/Loading';
import { useNavigate } from 'react-router-dom';
import { queryClient } from '..';
import { memberDataType } from '../types/memberDataType';
import Pagination from '../atoms/pagination/Pagination';
import MemberListItem from '../components/memberListPage/MemberListItem';

const MemberPage = () => {
    const navigate = useNavigate();
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);
    const isLogin = useSelector((state:RootState)=>state.loginInfo.isLogin);
    const accessToken = useSelector((state:RootState)=>state.loginInfo.accessToken);

    const [ currentPage, setCurrentPage ] = useState(1);
    const [ maxPage, setMaxPage ] = useState(10);
    const { isLoading, error, data, isPreviousData } = useQuery({ 
        queryKey: ['members', currentPage], 
        queryFn: async ()=>{
            const res = await getMemberList(accessToken.authorization,'',currentPage,10);
            return res;
        },
        keepPreviousData: true,
        staleTime: 1000*60*5,
        cacheTime: 1000*60*30,
    });

    useEffect(()=>{
        if(!isLogin) {
            navigate('/login');
        }
    },[]);


    useEffect(()=>{
        if(data) {
            setMaxPage(data.pageInfo.totalPage);
        } 
        if( !isPreviousData && data?.hasMore ) {
            queryClient.prefetchQuery({
                queryKey: ['members', currentPage+1], 
                queryFn: async ()=>{
                    const res = await getMemberList(accessToken.authorization,'',currentPage+1,10);
                    return res;
                },
            })
        }
    },[ data, isPreviousData, currentPage, queryClient ]);

    console.log(data);
    
    return (
        <PageContainer isDark={isDark}>
            <MainContainer isDark={isDark}>
                <PageTitle isDark={isDark}>회원 관리</PageTitle>
                { isLoading ? <Loading/> 
                  : error ? <>error</>
                  : <TableContainer>
                    { data.data.map((e:memberDataType)=><MemberListItem item={e}/>) }
                    </TableContainer> }
                <Pagination
                    isDark={isDark}
                    maxPage={maxPage}
                    currentPage={currentPage}
                    setCurrentPage={setCurrentPage}/>
            </MainContainer>
        </PageContainer>
    );
};

export default MemberPage;