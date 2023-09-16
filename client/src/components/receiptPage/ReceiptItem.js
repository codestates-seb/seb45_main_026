import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import { IconButtonContainer, IconButtonImg } from '../../atoms/buttons/IconButtons';
import arrowDown from '../../assets/images/icons/arrow/subscribe_arrow_down.svg';
import arrowUp from '../../assets/images/icons/arrow/subscribe_arrow_up.svg';
import { ReceiptAmountTypo, ReceiptGrayTypo, ReceiptItemContainer, ReceiptStatusTypo, ReceiptTitleTypo } from './ReceiptItem.style';
import ReceiptArcodian from './ReceiptArcodian';

const ReceiptItem = ({item}) => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    let createDate = item.createdDate;
    let createDay = createDate.split('T')[0];
    let createTime = createDate.split('T')[1];
    const titleName = `${item.orderVideos[0].videoName} 외 ${item.orderCount-1}개`;
    const amount = item.amount;
    const [ isAcordianOpen, setIsAcordianOpen ] = useState(false);

    const handleArcodianButtonClick = () => {
        setIsAcordianOpen(!isAcordianOpen);
    }

    return (
        <>
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
            {
                <IconButtonContainer isDark={isDark} onClick={handleArcodianButtonClick}>
                    <IconButtonImg src={isAcordianOpen?arrowUp:arrowDown}/>
                </IconButtonContainer>
            }   
        </ReceiptItemContainer>
        { isAcordianOpen && 
            <ReceiptArcodian videos={item.orderVideos}/>  }
        </>
    );
};

export default ReceiptItem;