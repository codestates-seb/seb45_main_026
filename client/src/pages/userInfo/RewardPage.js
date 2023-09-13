import React, { useState } from 'react';
import { MainContainer, PageContainer } from '../../atoms/layouts/PageContainer';
import { useSelector } from 'react-redux';
import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json';
import { BodyTextTypo, Heading5Typo } from '../../atoms/typographys/Typographys';
import RewardCategory from '../../components/rewardPage/RewardCategory';
import ReceiptDropdown from '../../components/receiptPage/ReceiptDropdown';


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
    const [ rewardList, setRewardList ] = useState([]);
    
    return (
        <PageContainer isDark={isDark}>
            <RewardMainContainer isDark={isDark}>
                <RewardTitle isDark={isDark}>나의 활동</RewardTitle>
                <RewardCategory category='reward'/>
                <RewardContentContainer>
                { rewardList.length===0 && 
                    <ContentNothing>적립 내역이 없습니다.</ContentNothing> }
                </RewardContentContainer>
            </RewardMainContainer>
        </PageContainer>
    );
};

export default RewardPage;