import { useSelector } from 'react-redux';
import { RootState } from '../redux/Store';
import { PageContainer } from '../atoms/layouts/PageContainer';

const VideoPage = () => {
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);

    return (
        <PageContainer isDark={isDark}>
            
        </PageContainer> 
    );
};

export default VideoPage;