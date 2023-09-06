import React from 'react';
import { styled } from 'styled-components';
import loadingDark from '../../assets/images/loading/loadingDark.gif';
import loadingLight from '../../assets/images/loading/loadingLight.gif';
import { useSelector } from 'react-redux';


const LoadingContainer = styled.div`
    width: ${props=>props.width};
    height: ${props=>props.height};
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
`
const LoadingImg = styled.img`
    width: 65px;
`
const Loading = ({ width, height }) => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <LoadingContainer width={width} height={height}>
            <LoadingImg src={isDark?loadingDark:loadingLight}/>
        </LoadingContainer>
    );
};

export default Loading;