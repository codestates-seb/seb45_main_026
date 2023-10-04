import React from 'react';
import styled from 'styled-components';
import tokens from '../../styles/tokens.json';
import { ItemContainer } from '../videoListPage/VideoListItem';
import { useSelector } from 'react-redux';
import { RootState } from '../../redux/Store';
import { memberDataType } from '../../types/memberDataType';
import { BodyTextTypo } from '../../atoms/typographys/Typographys';

const globalTokens = tokens.global;

type memberListItemType = {
    item : memberDataType;
}

const MemberListItem = ({ item } : memberListItemType) => {
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);

    return (
        <ItemContainer isDark={isDark}>
            <MemberEmailTypo isDark={isDark}>{item.email}</MemberEmailTypo>
            <MemberNicknameTypo isDark={isDark}>{item.nickname}</MemberNicknameTypo>
            <MemberChannelTypo isDark={isDark}>{item.channelName}</MemberChannelTypo>
            <MemberStatusTypo isDark={isDark}>
            { item.memberStatus==='ACTIVE'? '정상'
             : item.memberStatus==='BLOCKED'? '차단됨'
             : '' }
            </MemberStatusTypo>
        </ItemContainer>
    );
};

const MemberEmailTypo = styled.td<{isDark : boolean}>`
    width: 300px;
`
const MemberNicknameTypo = styled.td<{isDark : boolean}>`
    width: 150px;
`
const MemberChannelTypo = styled.td<{isDark : boolean}>`
    width: 250px;
`
const MemberStatusTypo = styled.td<{isDark : boolean}>`
    width: 100px;
`

export default MemberListItem;