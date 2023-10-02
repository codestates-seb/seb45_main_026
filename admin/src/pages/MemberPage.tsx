import React from 'react';
import { MainContainer, PageContainer } from '../atoms/layouts/PageContainer';
import { useSelector } from 'react-redux';
import { RootState } from '../redux/Store';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { getMemberList } from '../services/memberService';
import axios from 'axios';
import { ROOT_URL } from '../services';
import NavBar from '../components/navBar/NavBar';

const MemberPage = () => {
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);
    const accessToken = useSelector((state:RootState)=>state.loginInfo.accessToken);

    const { isLoading, error, data, isFetching } = useQuery({ 
        queryKey: ['members'], 
        queryFn: async ()=>{
            const res = await getMemberList(accessToken.authorization,'',1,10);
            return res.data;
        }});

    if (isLoading ) return <>Loading...</>

    if (error) return <>{`An error has occured : ${error}`}</>

    console.log(data)

    return (
        <PageContainer isDark={isDark}>
            <MainContainer isDark={isDark}>
            </MainContainer>
        </PageContainer>
    );
};

export default MemberPage;