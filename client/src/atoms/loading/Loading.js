import React from 'react';
import { styled } from 'styled-components';
import loadingDark from '../../assets/images/loading/loadingDark.gif';
import loadingLight from '../../assets/images/loading/loadingLight.gif';
import { useSelector } from 'react-redux';

const LoadingBackdrop = styled.div`
    position: fixed;
    top: 0;
    left: 0;
    z-index: 1001;
    background-color: rgba(255,255,255,0.25);
    opacity: ${(props) => (props.isLoading ? `1` : `0`)};
    visibility: ${(props) => (props.isLoading ? "visible" : "hidden")};
    width: 100vw;
    height: 100vh;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
`
const LoadingImg = styled.img`
    width: 100px;
`
export const Loading = ({isLoading}) => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <LoadingBackdrop isLoading={isLoading} isDark={isDark}>
            <LoadingImg src={isDark?loadingDark:loadingLight}/>
        </LoadingBackdrop>
    );
};

export default Loading;