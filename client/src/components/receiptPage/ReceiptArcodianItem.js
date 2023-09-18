import React, { useState } from 'react';
import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json';
import { BodyTextTypo } from '../../atoms/typographys/Typographys';
import { NegativeTextButton } from '../../atoms/buttons/Buttons';
import { cancelOnePurchaseService } from '../../services/receiptServices';
import { useSelector } from 'react-redux';
import { 
    ReceiptArcodianHeaderContainer,
    ReceiptArcodianItemContainer,
    ReceiptArcodianItemTitleTypo,
    ReceiptArcodianItemChannelTypo,
    ReceiptArcodianItemPriceTypo,
    ReceiptArcodianItemStatusTypo,
    ReceiptArcodianCancelButton,
    ReceiptArcodianHeaderinTitleTypo,
    ReceiptArcodianHeaderChannelTypo,
    ReceiptArcodianHeaderPriceTypo,
    ReceiptArcodianHeaderStatusTypo
 } from './ReceiptArcodianItem.style';
import { AlertModal } from '../../atoms/modal/Modal'



export const ReceiptArcodianItem = ({item, orderId}) => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const accessTokens = useSelector(state=>state.loginInfo.accessToken);
    const [ is결제취소성공팝업, setIs결제취소성공팝업 ] = useState(false);
    const [ is결제취소실패팝업, setIs결제취소실패팝업 ] = useState(false); 
    const [ 결제취소실패content, set결제취소실패content ] = useState('');
    
    //결제 취소 버튼 선택시 동작
    const handleCancelButtonClick = async () => {
        const response = await cancelOnePurchaseService(
            accessTokens.authorization,
            orderId,
            item.videoId,
        );
        if(response.status==='success') {
            setIs결제취소성공팝업(true);
        } else {
            set결제취소실패content(response.data);
            setIs결제취소실패팝업(true);
        }
    }

    return (
        <>
            <AlertModal 
                isModalOpen={is결제취소실패팝업}
                setIsModalOpen={setIs결제취소실패팝업}
                isBackdropClickClose={true}
                content={결제취소실패content}
                buttonTitle='확인'
                handleButtonClick={()=>{ setIs결제취소실패팝업(false) }}/>
            <AlertModal 
                isModalOpen={is결제취소성공팝업}
                setIsModalOpen={(setIsModalOpen) => {
                    setIsModalOpen(false);
                    window.location.reload();
                }}
                isBackdropClickClose={true}
                content='결제 취소되었습니다.'
                buttonTitle='확인'
                handleButtonClick={()=>{
                    setIs결제취소성공팝업(false);
                    window.location.reload();
                }}/>
            <ReceiptArcodianItemContainer isDark={isDark}>
                    <ReceiptArcodianItemTitleTypo isDark={isDark}>{ item.videoName }</ReceiptArcodianItemTitleTypo>
                    <ReceiptArcodianItemChannelTypo isDark={isDark}>{ item.channelName }</ReceiptArcodianItemChannelTypo>
                    <ReceiptArcodianItemPriceTypo isDark={isDark}>{ item.price }원</ReceiptArcodianItemPriceTypo>
                    <ReceiptArcodianItemStatusTypo isDark={isDark}>
                    {
                        item.orderStatus==='COMPLETED'? '결제 완료'
                        : item.orderStatus==='CANCELED'? '결제 취소'
                        : item.orderStatus==='ORDERED'? '결제 대기'
                        : null
                    }
                    </ReceiptArcodianItemStatusTypo>
                    { item.orderStatus==='COMPLETED' &&
                        <ReceiptArcodianCancelButton 
                            isDark={isDark}
                            onClick={handleCancelButtonClick}>결제 취소</ReceiptArcodianCancelButton>}
                </ReceiptArcodianItemContainer>
        </>
    );
};

export const ReceiptArcodianHeader = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <ReceiptArcodianHeaderContainer isDark={isDark}>
                <ReceiptArcodianHeaderinTitleTypo isDark={isDark}>강의명</ReceiptArcodianHeaderinTitleTypo>
                <ReceiptArcodianHeaderChannelTypo isDark={isDark}>채널명</ReceiptArcodianHeaderChannelTypo>
                <ReceiptArcodianHeaderPriceTypo isDark={isDark}>금액</ReceiptArcodianHeaderPriceTypo>
                <ReceiptArcodianHeaderStatusTypo isDark={isDark}>결제상태</ReceiptArcodianHeaderStatusTypo>
            </ReceiptArcodianHeaderContainer>
    );
}

export default ReceiptArcodianItem;
