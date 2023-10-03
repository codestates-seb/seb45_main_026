import { useSelector } from 'react-redux';
import { RootState } from '../redux/Store';
import { MainContainer, PageContainer } from '../atoms/layouts/PageContainer';
import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { getVideoList } from '../services/videoService';
import NavBar from '../components/navBar/NavBar';
import { PageTitle } from '../styles/PageTitle';
import Loading from '../components/loading/Loading';
import { videoDataType } from '../types/videoDataType';
import VideoListItem from '../components/videoListPage/VideoListItem';
import Pagination from '../atoms/pagination/Pagination';

const VideoPage = () => {
    const navigate = useNavigate();
    const isLogin = useSelector((state:RootState)=>state.loginInfo.isLogin);
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);
    const accessToken = useSelector((state:RootState)=>state.loginInfo.accessToken);
    const [ currentPage, setCurrentPage ] = useState(1);
    const [ maxPage, setMaxPage ] = useState(10);

    const { isLoading, error, data, isFetching } = useQuery({
        queryKey: ['videos'],
        queryFn: async ()=>{
            const response = await getVideoList(accessToken.authorization,'test@gmail.com','',currentPage,10);
            return response;
         },
    });

    useEffect(()=>{
        if(!isLogin) {
            navigate('/login'); 
            return;
        }
    },[]);

    useEffect(()=>{
        if(data) {
            setMaxPage(data.pageInfo.totalPage);
        }
    },[])

    console.log(data)

    return (
        <PageContainer isDark={isDark}>
            <MainContainer isDark={isDark}>
                <PageTitle isDark={isDark}>강의 관리</PageTitle>
            { isLoading? <Loading/>
             : error? <>error</>
             : data.data.map((e:videoDataType)=>
                <VideoListItem key={e.videoId} item={e}/>) }
            <Pagination 
                isDark={isDark} 
                maxPage={maxPage}
                currentPage={currentPage}
                setCurrentPage={setCurrentPage}/>
            </MainContainer>
        </PageContainer>
    );
};

export default VideoPage;