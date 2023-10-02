import { useSelector } from 'react-redux';
import { RootState } from '../redux/Store';
import { MainContainer, PageContainer } from '../atoms/layouts/PageContainer';
import { useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { getVideoList } from '../services/videoService';
import NavBar from '../components/navBar/NavBar';

type videoDataType = {
    videoId: number;
    videoName: string;
    videoStatus: string;
    memberId: number;
    email: string;
    createdDate: string;
    channelName: string;
}

const VideoPage = () => {
    const navigate = useNavigate();
    const isLogin = useSelector((state:RootState)=>state.loginInfo.isLogin);
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);
    const accessToken = useSelector((state:RootState)=>state.loginInfo.accessToken);
    const { isLoading, error, data, isFetching } = useQuery({
        queryKey: ['videos',accessToken.authorization,],
        queryFn: async ()=>{ 
            const response = await getVideoList(accessToken.authorization,'test@gmail.com','',1,10);
            return response.data;
         },
    });

    console.log(data);

    useEffect(()=>{
        if(!isLogin) {
            navigate('/login'); 
            return;
        }
    },[]);

    return (
        <PageContainer isDark={isDark}>
            <MainContainer isDark={isDark}>
            { isLoading? <>Loading...</>
             : error? <>error</>
             : data.map((e:videoDataType)=><div key={e.videoId}>{e.videoName}</div>) }
            </MainContainer>
        </PageContainer> 
    );
};

export default VideoPage;