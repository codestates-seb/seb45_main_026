import React, { useState } from 'react';
import styled from 'styled-components';
import tokens from '../../styles/tokens.json';
import { ItemContainer } from '../videoListPage/VideoListItem';
import { useSelector } from 'react-redux';
import { RootState } from '../../redux/Store';
import { memberDataType } from '../../types/memberDataType';
import { TableTd } from '../../atoms/table/Tabel';
import { RegularButton } from '../../atoms/buttons/Buttons';
import { useMutation } from '@tanstack/react-query';
import { queryClient } from '../..';
import { axiosErrorType } from '../../types/axiosErrorType';
import { useToken } from '../../hooks/useToken';
import { updateMemberStatusService } from '../../services/memberService';
import MemberBlockModal from './MemberBlockModal';
import { useMemberStatusUpdate } from '../../hooks/useMemberStateUpdate';

type memberListItemType = {
    item : memberDataType;
}

const MemberListItem = ({ item } : memberListItemType) => {
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);
    const accessToken = useSelector((state:RootState)=>state.loginInfo.accessToken);
    const [ is차단선택팝업open, setIs차단선택팝업open ] = useState(false);

    const { 
        mutate, 
    } = useMemberStatusUpdate(()=>{});

    //차단 버튼 누르면 실행됨
    const handleBlockButtonClick = async () => {
        setIs차단선택팝업open(true);
    }
    
    //차단 해제 버튼 누르면 실행됨
    const handleActiveButtonClick = async () => {
        mutate({
            authorization : accessToken.authorization,
            memberId : Number(item.memberId),
            days : 7,
            blockReason : 'test'
        });
    }

    return (
    <>
        <MemberBlockModal
            memberId={item.memberId}
            isModalOpen={is차단선택팝업open}
            setIsModalOpen={setIs차단선택팝업open}
            content={`${item.nickname} 계정을 비활성화합니다.`}/>
        <ItemContainer isDark={isDark}>
            <MemberIdTypo isDark={isDark}>{item.memberId}</MemberIdTypo>
            <MemberEmailTypo isDark={isDark}>{item.email}</MemberEmailTypo>
            <MemberNicknameTypo isDark={isDark}>{item.nickname}</MemberNicknameTypo>
            <MemberChannelTypo isDark={isDark}>{item.channelName}</MemberChannelTypo>
            <MemberStatusTypo isDark={isDark}>
            { item.memberStatus==='ACTIVE'? '정상'
             : item.memberStatus==='BLOCKED'? '차단됨'
             : '' }
            </MemberStatusTypo>
            <MemberButtonTd isDark={isDark}>
            { item.memberStatus==='ACTIVE' &&
                <RegularButton 
                    isDark={isDark}
                    onClick={handleBlockButtonClick}>차단</RegularButton> }
            { item.memberStatus==='BLOCKED' &&
                <RegularButton 
                    isDark={isDark}
                    onClick={handleActiveButtonClick}>차단해제</RegularButton> }
            </MemberButtonTd>
        </ItemContainer>
    </>
    );
};
export const MemberIdTypo = styled(TableTd)`
    width: 5%;
    text-align: center;
`
export const MemberEmailTypo = styled(TableTd)`
    width: 25%;
    text-align: center;
`
export const MemberNicknameTypo = styled(TableTd)`
    width: 20%;
    text-align: center;
`
export const MemberChannelTypo = styled(TableTd)`
    width: 20%;
    text-align: center;
`
export const MemberStatusTypo = styled(TableTd)`
    width: 15%;
    text-align: center;
`
export const MemberButtonTd = styled(TableTd)`
    width: 15%;
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
`

export default MemberListItem;