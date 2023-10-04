import { useSelector } from 'react-redux';
import { RootState } from '../redux/Store';
import { MainContainer, PageContainer, TableContainer } from '../atoms/layouts/PageContainer';
import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { getVideoList } from '../services/videoService';
import { PageTitle } from '../styles/PageTitle';
import Loading from '../components/loading/Loading';
import { videoDataType } from '../types/videoDataType';
import VideoListItem from '../components/videoListPage/VideoListItem';
import Pagination from '../atoms/pagination/Pagination';
import { queryClient } from '..';

const VideoPage = () => {
    const navigate = useNavigate();
    const isLogin = useSelector((state:RootState)=>state.loginInfo.isLogin);
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);
    const accessToken = useSelector((state:RootState)=>state.loginInfo.accessToken);
    const [ currentPage, setCurrentPage ] = useState(1);
    const [ maxPage, setMaxPage ] = useState(10);

    const { isLoading, error, data, isPreviousData } = useQuery({
        queryKey: ['videos', currentPage ],
        queryFn: async ()=>{
            const response = await getVideoList(accessToken.authorization,'test@gmail.com','',currentPage,10);
            return response;
         },
         keepPreviousData: true,
         staleTime: 1000*60*5,
         cacheTime: 1000*60*30,
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
        if( !isPreviousData && data?.hasMore ) {
            queryClient.prefetchQuery({
                queryKey: ['videos', currentPage+1],
                queryFn:  async () => {
                    const response = await getVideoList(accessToken.authorization,'','',currentPage+1,10);
                    return response;
                 },
            })
        }
    },[ data, isPreviousData, currentPage, queryClient ]);

    return (
        <PageContainer isDark={isDark}>
            <MainContainer isDark={isDark}>
                <PageTitle isDark={isDark}>강의 관리</PageTitle>
                { isLoading? <Loading/>
                : error? <>error</>
                : <TableContainer>
                    { data.data.map((e:videoDataType)=>
                    <VideoListItem key={e.videoId} item={e}/>) }
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

export default VideoPage;