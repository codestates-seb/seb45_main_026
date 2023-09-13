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
    flex-wrap: wrap;
    align-items: center;
    border-bottom: 1px solid ${props=>props.isDark?globalTokens.Gray.value:globalTokens.LightGray.value};
`
export const RewardGrayTypo = styled(BodyTextTypo)`
    color: ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
`
export const RewardTitleTypo = styled(BodyTextTypo)`
    font-weight: ${globalTokens.Bold.value};
    flex-grow: 1;
    padding-left: ${globalTokens.Spacing40.value}px;
`
const ReceiptItem = ({item, idx}) => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    let createDate = item.createdDate;
    let createDay = createDate.split('T')[0];
    let createTime = createDate.split('T')[1];
    const titleName = `${item.orderVideos[0].videoName} 외 ${item.orderVideos.length-1}개`;
    return (
        <ReceiptItemContainer>
            {idx}
            <RewardGrayTypo isDark={isDark}>
                {`${createDay} ${createTime}`}
            </RewardGrayTypo>
            <RewardTitleTypo isDark={isDark}>{titleName}</RewardTitleTypo>
        </ReceiptItemContainer>
    );
};

export default ReceiptItem;