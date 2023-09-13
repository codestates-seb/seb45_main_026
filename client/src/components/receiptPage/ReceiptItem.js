import React from 'react';
import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json';
import { BodyTextTypo } from '../../atoms/typographys/Typographys';
import { useSelector } from 'react-redux';

const globalTokens = tokens.global;

export const ReceiptItemContainer = styled.section`
    padding: ${globalTokens.Spacing16.value}px 0px;
    width: 90%;
    display: flex;
    flex-direction: row;
    align-items: center;
    border-bottom: 1px solid ${props=>props.isDark?globalTokens.Gray.value:globalTokens.LightGray.value};
`
export const RewardGrayTypo = styled(BodyTextTypo)`
    color: ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
`
export const RewardTitleTypo = styled(BodyTextTypo)`
    width: 350px;
    font-weight: ${globalTokens.Bold.value};
    padding-left: ${globalTokens.Spacing20.value}px;
`
export const RewardAmountTypo = styled(BodyTextTypo)`
    width: 100px;
    padding-left: ${globalTokens.Spacing20.value}px;
`
const ReceiptItem = ({item}) => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    let createDate = item.createdDate;
    let createDay = createDate.split('T')[0];
    let createTime = createDate.split('T')[1];
    const titleName = `${item.orderVideos[0].videoName} 외 ${item.orderCount-1}개`;
    const amount = item.amount;
    console.log(item)
    return (
        <ReceiptItemContainer>
            <RewardGrayTypo isDark={isDark}>
                {`${createDay} ${createTime}`}
            </RewardGrayTypo>
            <RewardTitleTypo isDark={isDark}>{titleName}</RewardTitleTypo>
            <RewardAmountTypo isDark={isDark}>{`${amount}원`}</RewardAmountTypo>
            <RewardGrayTypo isDark={isDark}>{item.orderStatus}</RewardGrayTypo>
        </ReceiptItemContainer>
    );
};

export default ReceiptItem;