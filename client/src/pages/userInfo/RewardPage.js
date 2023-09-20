import React, { useEffect, useState } from 'react';
import { MainContainer, PageContainer } from '../../atoms/layouts/PageContainer';
import { useSelector } from 'react-redux';
import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json';
import { BodyTextTypo, Heading5Typo } from '../../atoms/typographys/Typographys';
import RewardCategory from '../../components/rewardPage/RewardCategory';
import RewardItem from '../../components/rewardPage/RewardItem';
import { useInView } from 'react-intersection-observer';
import { getRewardListService } from '../../services/rewardService';
import { BottomDiv } from '../contents/LectureListPage';
import { useToken } from '../../hooks/useToken';
import RewardListHeader from '../../components/rewardPage/RewardListHeader';

const globalTokens = tokens.global;

export const RewardMainContainer = styled(MainContainer)`
    background-color: ${props=>props.isDark?'rgba(255,255,255,0.15)':globalTokens.White.value};
    border: none;
    margin-top: ${globalTokens.Spacing40.value}px;
    margin-bottom: ${globalTokens.Spacing40.value}px;
    padding: ${globalTokens.Spacing20.value}px;
    border-radius: ${globalTokens.RegularRadius.value}px;
    display: flex;
`
export const RewardTitle = styled(Heading5Typo)`
  width: 100%;
  padding-left: ${globalTokens.Spacing28.value}px;
  margin-top: ${globalTokens.Spacing20.value}px;
  margin: ${globalTokens.Spacing8.value}px;
`
export const RewardContentContainer = styled.section`
    width: 100%;
    min-height: 75vh;
    display: flex;
    flex-direction: column;
    justify-content: start;
    align-items: center;
`
export const ContentNothing = styled(BodyTextTypo)`
    margin: ${globalTokens.Spacing40.value}px;
    color: ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
`

const RewardPage = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const accessToken = useSelector(state=>state.loginInfo.accessToken);
    const [ rewardList, setRewardList ] = useState([]);
    const [ page, setPage ] = useState(1);
    const [ maxPage, setMaxPage ] =useState(10);
    const [ isLoading, setIsLoading ] = useState(true);
    const [ ref, inView ] = useInView();
    const refreshToken = useToken();

    //첫 페이지 데이터를 불러옴
    useEffect(()=>{
        if(page>1) return;
        getRewardListService(accessToken.authorization, page, 20).then((res)=>{
            if(res.status==='success') {
                setMaxPage(res.data.pageInfo.totalPage);
                setRewardList(res.data.data);
                setIsLoading(false);
            } else if(res.data==='만료된 토큰입니다.'){
                refreshToken();
            } else {
                console.log(res.data)
            }
        })
    },[accessToken]);

    //페이지 값이 증가하면 새로운 데이터를 불러옴
    useEffect(()=>{
        if(page>1) {
            getRewardListService(accessToken.authorization, page, 20).then((res)=>{
                if(res.status==='success') {
                    setRewardList([
                        ...rewardList,
                        ...res.data.data
                    ]);
                    setIsLoading(false);
                } else {
                    console.log(res);
                }
            });
        }
    },[page])

    //바닥 요소가 보이고 더 불러올 페이지가 있으면 현재 페이지 값을 1 증가
    useEffect(()=>{
        if(inView && maxPage>page) {
            setIsLoading(true);
            setPage(page+1);
        }
    },[inView])

    useEffect(() => {
      window.scrollTo({
        top: 0,
      });
    }, []);

    return (
        <PageContainer isDark={isDark}>
            <RewardMainContainer isDark={isDark}>
                <RewardTitle isDark={isDark}>나의 활동</RewardTitle>
                <RewardCategory category='reward'/>
                <RewardContentContainer isDark={isDark}>
                { rewardList.length===0 && 
                    <ContentNothing isDark={isDark}>적립 내역이 없습니다.</ContentNothing> }
                { rewardList.length>0 && <RewardListHeader/> }
                { rewardList.length>0 &&
                    rewardList.map((e)=><RewardItem item={e}/>) }
                </RewardContentContainer>
                { !isLoading && <BottomDiv ref={ref}/> }
            </RewardMainContainer>
        </PageContainer>
    );
};

export default RewardPage;