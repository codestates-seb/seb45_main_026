import React from 'react';
import { useSelector } from 'react-redux';
import styled from 'styled-components';
import { RootState } from '../../redux/Store';
import loadingDark from '../../assets/images/loadings/loadingDark.gif';
import loadingLight from '../../assets/images/loadings/loadingLight.gif';

const Loading = () => {
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);

    return (
        <LoadingContainer>
            <LoadingImg src={isDark ? loadingDark : loadingLight} />
        </LoadingContainer>
    );
};

export const LoadingContainer = styled.div`
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
`
export const LoadingImg = styled.img`
    width: 100px;
`

export default Loading;