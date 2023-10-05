import React from 'react';
import { videoDataType } from '../../types/videoDataType';
import styled from 'styled-components';
import { useSelector } from 'react-redux';
import { RootState } from '../../redux/Store';
import tokens from '../../styles/tokens.json';
import { BodyTextTypo } from '../../atoms/typographys/Typographys';
import { TableTd, TableTr } from '../../atoms/table/Tabel';
import { RegularButton } from '../../atoms/buttons/Buttons';
import { useMutation } from '@tanstack/react-query';
import axios from 'axios';
import { ROOT_URL } from '../../services';
import { queryClient } from '../..';

const globalTokens = tokens.global;

type videoListItemType = {
    item : videoDataType;
}

type mutationType = {
    authorization : string;
    videoId : number;
}

const VideoListItem = ({item} : videoListItemType) => {
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);
    const accessToken = useSelector((state:RootState)=>state.loginInfo.accessToken);

    let createDate = item.createdDate;
    let createDay = createDate.split("T")[0];
    let createTime = createDate.split("T")[1];

    const { mutate, isLoading, isError, error, isSuccess }
        = useMutation({
            mutationFn : ({
                authorization,
                videoId,
            } : mutationType )=>{
                return axios.patch(
                    `${ROOT_URL}/videos/${videoId}/status`,
                    {},
                    {
                        headers: {
                            Authorization : authorization,
                        }
                    }
                );
            },
            onSuccess: ()=>{
                queryClient.invalidateQueries({ queryKey : ['videos'] })
            }
        })

    //강의 활성화, 비활성화 처리
    const updateVideoStatus = () => {
        mutate({
            authorization : accessToken.authorization,
            videoId : item.videoId,
        })
    }

    return (
        <ItemContainer isDark={isDark}>
            <VideoTitleTypo isDark={isDark}>{item.videoName}</VideoTitleTypo>
            <VideoAuthorEmailTypo isDark={isDark}>{item.email}</VideoAuthorEmailTypo>
            <VideoStatusTypo isDark={isDark}>{
                item.videoStatus==='CREATED'? '정상'
                : item.videoStatus=== 'CLOSED'? '폐쇄됨'
                : item.videoStatus==='ADMIN_CLOSED'? '관리자에 의해 폐쇄됨' 
                : null
            }
            </VideoStatusTypo>
            <VideoCreatedDateTypo isDark={isDark}>
            {`${createDay} ${createTime}`}
            </VideoCreatedDateTypo>
            <VideoBlockButtonTd isDark={isDark}>
            { (item.videoStatus=== 'CLOSED' || item.videoStatus==='ADMIN_CLOSED') &&
                <RegularButton isDark={isDark} onClick={updateVideoStatus}>활성화</RegularButton>}
            { item.videoStatus==='CREATED' &&
                <RegularButton isDark={isDark} onClick={updateVideoStatus}>비활성화</RegularButton> }
            </VideoBlockButtonTd>
        </ItemContainer>
    );
};

export const ItemContainer = styled(TableTr)`
`
export const VideoTitleTypo = styled(TableTd)`
    width: 30%;
`
export const VideoAuthorEmailTypo = styled(TableTd)`
    width: 20%;
    text-align: center;
`
export const VideoStatusTypo = styled(TableTd)`
    width: 20%;
    text-align: center;
`
export const VideoCreatedDateTypo = styled(TableTd)`
    width: 20%;
    text-align: center;
`
export const VideoBlockButtonTd = styled(TableTd)`
    width: 10%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
`

export default VideoListItem;