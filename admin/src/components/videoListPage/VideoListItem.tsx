import React from 'react';
import { videoDataType } from '../../types/videoDataType';
import styled from 'styled-components';
import { useSelector } from 'react-redux';
import { RootState } from '../../redux/Store';
import tokens from '../../styles/tokens.json';
import { BodyTextTypo } from '../../atoms/typographys/Typographys';
import { TableTd, TableTr } from '../../atoms/table/Tabel';

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

export const ItemContainer = styled(TableTr)``
const VideoTitleTypo = styled(TableTd)<{isDark : boolean}>``

export default VideoListItem;