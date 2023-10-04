import { useSelector } from 'react-redux';
import { RootState } from '../redux/Store';
import { MainContainer, PageContainer } from '../atoms/layouts/PageContainer';
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
import { TableContainer } from '../atoms/table/Tabel';
import axios from 'axios';
import { errorResponseDataType } from '../types/axiosErrorType';
import { useToken } from '../hooks/useToken';

const VideoPage = () => {
    const navigate = useNavigate();
    const refreshToken = useToken();
    const isLogin = useSelector((state:RootState)=>state.loginInfo.isLogin);
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);
    const accessToken = useSelector((state:RootState)=>state.loginInfo.accessToken);
    const [ currentPage, setCurrentPage ] = useState(1);
    const [ maxPage, setMaxPage ] = useState(10);

    const { isLoading, error, data, isPreviousData } = useQuery({
        queryKey: ['videos', currentPage ],
        queryFn: async ()=>{
            try {
                const response = await getVideoList(accessToken.authorization,'test@gmail.com','',currentPage,10);
                return response;
            } catch (err) {
                if (axios.isAxiosError<errorResponseDataType, any>(err)) {
                    if(err.response?.data.message === '만료된 토큰입니다.') {
                        refreshToken();
                    } else {
                        console.log(err);
                    }
                }
            }
         },
         keepPreviousData: true,
         staleTime: 1000*60*5,
         cacheTime: 1000*60*30,
    });

  useEffect(() => {
    if (!isLogin) {
      navigate("/login");
      return;
    }
  }, []);

    useEffect(()=>{
        if(data) {
            setMaxPage(data.pageInfo.totalPage);
        }
        if( !isPreviousData && data?.hasMore ) {
            queryClient.prefetchQuery({
                queryKey: ['videos', currentPage+1],
                queryFn:  async () => {
                    try {
                        const response = await getVideoList(accessToken.authorization,'','',currentPage+1,10);
                        return response;
                    } catch (err) {
                        if (axios.isAxiosError<errorResponseDataType, any>(err)) {
                            if(err.response?.data.message === '만료된 토큰입니다.') {
                                refreshToken();
                            }
                        } else {
                            console.log(err);
                        }
                    }
                }
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
