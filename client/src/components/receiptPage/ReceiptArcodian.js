import React from 'react';
import { useSelector } from 'react-redux';
import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json';
import { BodyTextTypo } from '../../atoms/typographys/Typographys';

const globalTokens = tokens.global;

const ReceiptArcodianContainer = styled.div`
    display: flex;
    flex-direction: column;
    border: 1px solid  ${props=>props.isDark?globalTokens.Gray.value:globalTokens.LightGray.value};
    margin: ${globalTokens.Spacing8.value}px;
    padding: ${globalTokens.Spacing20.value}px;
    width: 90%;
    border-radius: ${globalTokens.RegularRadius.value}px;
`
const ReceiptArcodianHeaderContainer = styled.div`
    padding: ${globalTokens.Spacing4.value}px ${globalTokens.Spacing8.value}px;
    background-color: ${props=>props.isDark?globalTokens.Black.value:globalTokens.Background.value};
    display: flex;
    flex-direction: row;
    align-items: center;
`
const ReceiptArcodianItemContainer = styled.div`
    padding: ${globalTokens.Spacing8.value}px;  
    border-bottom: 1px solid ${props=>props.isDark?globalTokens.Gray.value:globalTokens.LightGray.value};
    display: flex;
    flex-direction: row;
`
const ReceiptArcodianItemTitleTypo = styled(BodyTextTypo)`
    width: 40%;
`
const ReceiptArcodianItemChannelTypo = styled(BodyTextTypo)`
    width: 20%;
    text-align: center;
`
const ReceiptArcodianItemPriceTypo = styled(BodyTextTypo)`
    width: 20%;
    text-align: center;
`
const ReceiptArcodianHeaderinTitleTypo = styled(ReceiptArcodianItemTitleTypo)`
    text-align: center;
    color: ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
`
const ReceiptArcodianHeaderChannelTypo = styled(ReceiptArcodianItemChannelTypo)`
    color: ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
`
const ReceiptArcodianHeaderPriceTypo = styled(ReceiptArcodianItemPriceTypo)`
    color: ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
`

const ReceiptArcodian = ({videos}) => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <ReceiptArcodianContainer isDark={isDark}>
            <ReceiptArcodianHeaderContainer isDark={isDark}>
                <ReceiptArcodianHeaderinTitleTypo isDark={isDark}>강의명</ReceiptArcodianHeaderinTitleTypo>
                <ReceiptArcodianHeaderChannelTypo isDark={isDark}>채널명</ReceiptArcodianHeaderChannelTypo>
                <ReceiptArcodianHeaderPriceTypo isDark={isDark}>금액</ReceiptArcodianHeaderPriceTypo>
            </ReceiptArcodianHeaderContainer>
        { videos.map((e)=>
            <ReceiptArcodianItemContainer isDark={isDark}>
                <ReceiptArcodianItemTitleTypo isDark={isDark}>{ e.videoName }</ReceiptArcodianItemTitleTypo>
                <ReceiptArcodianItemChannelTypo isDark={isDark}>{ e.channelName }</ReceiptArcodianItemChannelTypo>
                <ReceiptArcodianItemPriceTypo isDark={isDark}>{ e.price }원</ReceiptArcodianItemPriceTypo>
            </ReceiptArcodianItemContainer>) }
        </ReceiptArcodianContainer>
    );
};

export default ReceiptArcodian;