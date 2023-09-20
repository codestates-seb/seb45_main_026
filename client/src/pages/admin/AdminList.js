import React, { useEffect, useState } from 'react';
import { MainContainer, PageContainer } from '../../atoms/layouts/PageContainer';
import { useSelector } from 'react-redux';
import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json';
import { useInView } from 'react-intersection-observer';
import { getReportService } from '../../services/adminService';
import { useToken } from '../../hooks/useToken';
import { BottomDiv } from '../contents/LectureListPage';
import { HomeTitle } from '../../components/contentListItems/ChannelHome';
import ReportItem from '../../components/adminPageItems/ReportItem';
import ReportHeader from '../../components/adminPageItems/ReportHeader';

const globalTokens = tokens.global;

export const AdminMainContainer = styled(MainContainer)`
    min-width: 600px;
    min-height: 700px;
    background-color: ${(props) =>
        props.isDark ? "rgba(255,255,255,0.15)" : globalTokens.White.value};
    border: none;
    margin: ${globalTokens.Spacing40.value}px 0;
    padding: ${globalTokens.Spacing28.value}px;
    border-radius: ${globalTokens.Spacing20.value}px;
`

const AdminList = () => {
    const refreshToken = useToken();
    const isDark=useSelector(state=>state.uiSetting.isDark);
    const accessToken = useSelector(state=>state.loginInfo.accessToken);
    const userInfo = useSelector(state=>state.loginInfo.loginInfo);
    const [ reportList, setReportList ] = useState([]);
    const [ page, setPage ] = useState(1);
    const [ maxPage, setMaxPage ] = useState(10);
    const [ ref, inView ] = useInView();
    const [ loading, setLoading ] = useState(true);

    //첫 페이지 데이터를 불러옴
    useEffect(()=>{
        if(page>1) return;
        getReportService(accessToken.authorization,1).then((res)=>{
            if(res.status==='success') {
                setReportList([ ...res.data.data ]);
                setMaxPage(res.data.pageInfo.totalPage);
                setLoading(false);
            } else if(res.data==='만료된 토큰입니다.'){
                refreshToken();
            } else {
                console.log(res.data);
            }
        })
    },[accessToken]);

    //페이지값이 증가하면 새로운 데이터를 불러옴
    useEffect(()=>{
        if (page>1) {
            getReportService(accessToken.authorization,page).then((res)=>{
                if(res.status==='success'){
                    setReportList([
                        ...reportList,
                        ...res.data.data
                    ])
                }else{
                    console.log(res);
                }
            })
        } 
    },[page]);

    //바닥 요소가 보이면 현재 페이지 값을 1 증가
    useEffect(()=>{
        if(inView && maxPage>page){
            setLoading(true);
            setPage(page+1);
        }
    },[inView]);

    return (
        <PageContainer isDark={isDark}>
            <AdminMainContainer isDark={isDark}>
                
                <HomeTitle isDark={isDark}>신고내역 관리</HomeTitle>
                <ReportHeader/>
                {
                    reportList.length>0 && 
                        reportList.map((e,idx)=>{ 
                            return <ReportItem key={e.videoId} item={e}/>})
                }
                { !loading && <BottomDiv ref={ref}/> }
            </AdminMainContainer>
        </PageContainer>
    );
};

export default AdminList;