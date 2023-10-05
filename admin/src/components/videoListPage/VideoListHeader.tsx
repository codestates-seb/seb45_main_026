import React from 'react';
import { ItemContainer, VideoAuthorEmailTypo, VideoCreatedDateTypo, VideoStatusTypo, VideoTitleTypo } from './VideoListItem';
import { useSelector } from 'react-redux';
import { RootState } from '../../redux/Store';
import styled from 'styled-components';
import tokens from '../../styles/tokens.json';
import { TableTh } from '../../atoms/table/Tabel';

const globalTokens = tokens.global;

const VideoListHeader = () => {
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);

    return (
        <TableTh isDark={isDark}>
            <VideoHeaderTitleTypo isDark={isDark}>강의명</VideoHeaderTitleTypo>
            <VideoHeaderAuthorEmailTypo isDark={isDark}>작성자이메일</VideoHeaderAuthorEmailTypo>
            <VideoHeaderStatusTypo isDark={isDark}>강의 상태</VideoHeaderStatusTypo>
            <VideoHeaderCreatedDateTypo isDark={isDark}>생성일시</VideoHeaderCreatedDateTypo>
        </TableTh>
    );
};

const VideoHeaderTitleTypo = styled(VideoTitleTypo)`
    color: ${props=>props.isDark? globalTokens.LightGray.value : globalTokens.Gray.value };
`
const VideoHeaderAuthorEmailTypo = styled(VideoAuthorEmailTypo)`
    color: ${props=>props.isDark? globalTokens.LightGray.value : globalTokens.Gray.value };
`
const VideoHeaderStatusTypo = styled(VideoStatusTypo)`
    color: ${props=>props.isDark? globalTokens.LightGray.value : globalTokens.Gray.value };
`
const VideoHeaderCreatedDateTypo = styled(VideoCreatedDateTypo)`
    color: ${props=>props.isDark? globalTokens.LightGray.value : globalTokens.Gray.value };
`
export default VideoListHeader;