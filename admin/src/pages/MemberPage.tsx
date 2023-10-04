import React, { useEffect, useState } from 'react';
import { MainContainer, PageContainer } from '../atoms/layouts/PageContainer';
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
import { TableContainer } from '../atoms/table/Tabel';
import { errorResponseDataType } from '../types/axiosErrorType';
import axios from 'axios';
import { useToken } from '../hooks/useToken';

const MemberPage = () => {
    const navigate = useNavigate();
    const refreshToken = useToken();
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);
    const isLogin = useSelector((state:RootState)=>state.loginInfo.isLogin);
    const accessToken = useSelector((state:RootState)=>state.loginInfo.accessToken);

    const [ currentPage, setCurrentPage ] = useState(1);
    const [ maxPage, setMaxPage ] = useState(10);

    const { isLoading, error, data, isPreviousData } = useQuery({ 
        queryKey: [ 'members', accessToken, currentPage ], 
        queryFn: async ()=>{
            try {
                const res = await getMemberList(accessToken.authorization,'',currentPage,10);
                return res;
            } catch (err) {
                if (axios.isAxiosError<errorResponseDataType, any>(err)) {
                    if(err.response?.data.message==='만료된 토큰입니다.') {
                        refreshToken();
                    }
                } else {
                    console.log(err);
                }
            }
        },
        keepPreviousData: true,
        staleTime: 1000*60*5,
        cacheTime: 1000*60*30,
        retry: 3, //error를 표시하기 전에 실패한 요청을 다시 시도하는 횟수
        retryDelay: 1000,
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
                queryKey: ['members', accessToken, currentPage+1], 
                queryFn: async ()=>{
                    try {
                        const res = await getMemberList(accessToken.authorization,'',currentPage+1,10);
                        return res;
                    } catch (err) {
                        if (axios.isAxiosError<errorResponseDataType, any>(err)) {
                            if(err.response?.data.message==='만료된 토큰입니다.') {
                                refreshToken();
                            }
                        } else {
                            console.log(err);
                        }
                    }
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