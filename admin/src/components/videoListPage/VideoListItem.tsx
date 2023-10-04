import React from 'react';
import { videoDataType } from '../../types/videoDataType';
import styled from 'styled-components';
import { useSelector } from 'react-redux';
import { RootState } from '../../redux/Store';
import tokens from '../../styles/tokens.json';
import { BodyTextTypo } from '../../atoms/typographys/Typographys';

const globalTokens = tokens.global;

type videoListItemType = {
    item : videoDataType;
}

const VideoListItem = ({item} : videoListItemType) => {
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);
    
    return (
        <ItemContainer isDark={isDark}>
            <VideoTitleTypo isDark={isDark}>{item.videoName}</VideoTitleTypo>
        </ItemContainer>
    );
};

export const ItemContainer = styled.tr<{isDark : boolean}>`
    width: 100%;
    padding: ${globalTokens.Spacing8.value}px;
    display: flex;
    flex-direction: row;
    justify-content: start;
    align-items: center;
    border-bottom: 1px solid ${props=>props.isDark?globalTokens.Gray.value:globalTokens.LightGray.value};
`
const VideoTitleTypo = styled.td<{isDark : boolean}>`
    
`

export default VideoListItem;