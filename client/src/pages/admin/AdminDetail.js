import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { AdminMainContainer } from './AdminList';
import { useToken } from '../../hooks/useToken';
import { PageContainer } from '../../atoms/layouts/PageContainer';
import { HomeTitle } from '../../components/contentListItems/ChannelHome';
import { styled } from 'styled-components';
import { BigButton, NegativeTextButton } from '../../atoms/buttons/Buttons';
import { useNavigate, useParams } from 'react-router-dom';
import { getReportContentService, patchVideoStatus } from '../../services/adminService';
import ReportDetailItem from '../../components/adminPageItems/ReportDetailItem';
import ReportDetailHeader from '../../components/adminPageItems/ReportDetailHeader';
import { useInView } from 'react-intersection-observer';
import { BottomDiv } from '../contents/LectureListPage';
import { BodyTextTypo } from '../../atoms/typographys/Typographys';
import tokens from '../../styles/tokens.json';
import { AlertModal } from '../../atoms/modal/Modal';

const globalTokens = tokens.global;

const AdminBackContainer = styled.div`
    width: 90%;
    display: flex;
    flex-direction: row;
    justify-content: start;
    gap: ${globalTokens.Spacing8.value}px;
`
const AdminDetailVideoTitleTypo = styled(BodyTextTypo)`
    font-weight: ${globalTokens.Bold.value};
    margin: ${globalTokens.Spacing8.value}px 0;
`
const AdminBackButton = styled(NegativeTextButton)`
    padding-left: 0;
`

export const AdminDetail = () => {
    const refreshToken = useToken();
    const { videoId } = useParams();
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const accessToken = useSelector(state=>state.loginInfo.accessToken);
    const userInfo = useSelector(state=>state.loginInfo.loginInfo);
    const navigate = useNavigate();
    const [ contentList, setContentList ] = useState([]);
    const [ page, setPage ] = useState(1);
    const [ maxPage, setMaxPage ] = useState(10);
    const [ ref, inView ] = useInView();
    const [ loading, setLoading ] = useState(true);
    const [ is비활성화성공팝업, setIs비활성화성공팝업 ] = useState(false);
    const [ is비활성화실패팝업, setIs비활성화실패팝업 ] = useState(false);
    let videoName = decodeURI(window.location.search).split('=')[1].split('?')[0];
    let videoStatus = decodeURI(window.location.search).split('=')[2];

    const handleBackButtonClick = () => {
        navigate(-1);
    }
    const handleVideoButtonClick = () => {
        navigate(`/videos/${videoId}`)
    }
    const handleDeleteButtonClick = async () => {
        const response = await patchVideoStatus(accessToken.authorization, videoId);
        if(response.status==='success') {
            setIs비활성화성공팝업(true);
        } else if(response.data==='만료된 토큰입니다.'){
            refreshToken(handleDeleteButtonClick);
        } else {
            console.log(response.data);
            setIs비활성화실패팝업(true);
        }
    }

    //첫 페이지 데이터 불러옴
    useEffect(()=>{
        if(page>1) return;
        if (userInfo.authority!=='ROLE_ADMIN') {
            navigate('/lecture');
            return;
        }
        getReportContentService(accessToken.authorization, page, videoId).then((res)=>{
            if(res.status==='success') {
                setContentList(res.data.data);
            } else if(res.data==='만료된 토큰입니다.') {
                refreshToken();
            } else {
                console.log(res.data);
            }
        })
    },[ accessToken ]);

    //페이지값이 증가하면 새로운 데이터를 불러옴
    useEffect(()=>{
        if(page>1) {
            getReportContentService(accessToken.authorization, page, videoId).then((res)=>{
                if(res.status==='success') {
                    setContentList([
                        ...contentList,
                        ...res.data.data
                    ]);
                    setMaxPage(res.data.pageInfo.totalPage);
                    setLoading(false);
                } else {
                    console.log(res.data);
                }
            })
        }
    }, [page]);

    //바닥 요소가 보이면 현재 페이지 값을 1 증가
    useEffect(()=>{
        if(inView && maxPage>page) {
            setLoading(true);
            setPage(page+1);
        }
    },[inView]);
    
    return (
        <>
            <AlertModal
                isModalOpen={is비활성화성공팝업}
                setIsModalOpen={setIs비활성화성공팝업}
                isBackdropClickClose={true}
                content='강의 비활성화 처리 되었습니다.'
                buttonTitle='확인'
                handleButtonClick={()=>{
                    setIs비활성화성공팝업(false);
                    navigate(-1);
                 }}/>
            <AlertModal
                isModalOpen={is비활성화실패팝업}
                setIsModalOpen={setIs비활성화실패팝업}
                isBackdropClickClose={true}
                content='강의 비활성화 처리 실패했습니다.'
                buttonTitle='확인'
                handleButtonClick={()=>{ setIs비활성화실패팝업(false) }}/>
            <PageContainer isDark={isDark}>
                <AdminMainContainer isDark={isDark}>
                    <HomeTitle isDark={isDark}>신고내역 관리</HomeTitle>
                    <AdminBackContainer isDark={isDark}>
                        <AdminBackButton isDark={isDark} onClick={handleBackButtonClick}>← 뒤로가기</AdminBackButton>
                    </AdminBackContainer>
                    <AdminBackContainer>
                        <AdminDetailVideoTitleTypo isDark={isDark}>강의명 : {videoName}</AdminDetailVideoTitleTypo>
                    </AdminBackContainer>
                    <AdminBackContainer>
                        <BigButton isDark={isDark} onClick={handleVideoButtonClick}>해당 강의 확인하기</BigButton>
                        { videoStatus!=='CLOSED' && videoStatus!=='ADMIN_CLOSED' &&
                            <BigButton isDark={isDark} onClick={handleDeleteButtonClick}>강의 비활성화 하기</BigButton>}
                    </AdminBackContainer>
                    <ReportDetailHeader/>
                    { contentList.map((e)=><ReportDetailItem key={e.reportId} item={e}/>) }
                    { !loading && <BottomDiv ref={ref}/> }
                </AdminMainContainer>
            </PageContainer>
        </>
    );
};

export default AdminDetail;