import React from 'react';
import { TableTh } from '../../atoms/table/Tabel';
import { useSelector } from 'react-redux';
import { RootState } from '../../redux/Store';
import styled from 'styled-components';
import { MemberButtonTd, MemberChannelTypo, MemberEmailTypo, MemberIdTypo, MemberNicknameTypo, MemberStatusTypo } from './MemberListItem';
import tokens from '../../styles/tokens.json';

const globalTokens = tokens.global;

const MemberListHeader = () => {
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);

    return (
        <TableTh isDark={isDark}>
            <MemberHeaderIdTypo isDark={isDark}>ID</MemberHeaderIdTypo>
            <MemberHeaderEmailTypo isDark={isDark}>이메일</MemberHeaderEmailTypo>
            <MemberHeaderNicknameTypo isDark={isDark}>닉네임</MemberHeaderNicknameTypo>
            <MemberHeaderChannelTypo isDark={isDark}>채널명</MemberHeaderChannelTypo>
            <MemberHeaderStatusTypo isDark={isDark}>계정상태</MemberHeaderStatusTypo>
            <MemberHeaderButtonTd isDark={isDark}></MemberHeaderButtonTd>
        </TableTh>
    );
};

const MemberHeaderIdTypo = styled(MemberIdTypo)`
    color : ${props=>props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value };
`
const MemberHeaderEmailTypo = styled(MemberEmailTypo)`
    color : ${props=>props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value };
`
const MemberHeaderNicknameTypo = styled(MemberNicknameTypo)`
    color : ${props=>props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value };
`
const MemberHeaderChannelTypo = styled(MemberChannelTypo)`
    color : ${props=>props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value };
`
const MemberHeaderStatusTypo = styled(MemberStatusTypo)`
    color : ${props=>props.isDark ? globalTokens.LightGray.value : globalTokens.Gray.value };
`
const MemberHeaderButtonTd = styled(MemberButtonTd)``

export default MemberListHeader;