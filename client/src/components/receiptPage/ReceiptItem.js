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
export const ReceiptGrayTypo = styled(BodyTextTypo)`
    width: 150px;
    color: ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
`
export const ReceiptTitleTypo = styled(BodyTextTypo)`
    width: 350px;
    font-weight: ${globalTokens.Bold.value};
    padding-left: ${globalTokens.Spacing20.value}px;
`
export const ReceiptAmountTypo = styled(BodyTextTypo)`
    width: 100px;
    text-align: center;
`
export const ReceiptStatusTypo = styled(BodyTextTypo)`
    width: 150px;
    color: ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
    text-align: center;
`
const ReceiptItem = ({item}) => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    let createDate = item.createdDate;
    let createDay = createDate.split('T')[0];
    let createTime = createDate.split('T')[1];
    const titleName = `${item.orderVideos[0].videoName} 외 ${item.orderCount-1}개`;
    const amount = item.amount;

    return (
        <ReceiptItemContainer>
            <ReceiptGrayTypo isDark={isDark}>
                {`${createDay} ${createTime}`}
            </ReceiptGrayTypo>
            <ReceiptTitleTypo isDark={isDark}>{titleName}</ReceiptTitleTypo>
            <ReceiptAmountTypo isDark={isDark}>{`${amount}원`}</ReceiptAmountTypo>
            <ReceiptStatusTypo isDark={isDark}>
            {
                item.orderStatus==='COMPLETED'? '결제 완료'
                : item.orderStatus==='CANCELED'? '결제 취소'
                : item.orderStatus==='ORDERED'? '결제 대기'
                : null
            }
            </ReceiptStatusTypo>
        </ReceiptItemContainer>
    );
};

export default ReceiptItem;