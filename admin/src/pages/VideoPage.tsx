import { useSelector } from 'react-redux';
import { RootState } from '../redux/Store';
import { MainContainer, PageContainer } from '../atoms/layouts/PageContainer';
import { useNavigate } from 'react-router-dom';
import { useEffect } from 'react';

const VideoPage = () => {
    const navigate = useNavigate();
    const isLogin = useSelector((state:RootState)=>state.loginInfo.isLogin);
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);

    useEffect(()=>{
        if(!isLogin) { 
            navigate('/login'); 
            return;
        }
    },[]);

    return (
        <PageContainer isDark={isDark}>
            <MainContainer isDark={isDark}></MainContainer>
        </PageContainer> 
    );
};

export default VideoPage;