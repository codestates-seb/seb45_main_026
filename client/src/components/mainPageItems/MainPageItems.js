import React, { useEffect, useRef, useState } from 'react';
import { useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { 
    MainPageFirstItemContainer, 
    MainPageTitleTypo, 
    MainPageSubTitleTypo,
    MainPageStartButton 
} from './MainPageItems.style';

export const MainPageFirstItem = () => {
    const [ isInTitleViewport, setIsInTitleViewport ] = useState(false);
    const [ isInSubTitleVieport, setIsInSubTitleVieport ] = useState(false);
    const [ isInButtonVieport, setIsInButtonViewport ] = useState(false);
    const ref = useRef(null);
    const navigate = useNavigate();
    const isDark = useSelector(state=>state.uiSetting.isDark);

    useEffect(()=>{
        if(!ref.current) return;
        const callback = (entries) => {
            entries.forEach((entry)=>{
                if(entry.isIntersecting) {
                    setIsInTitleViewport(true);
                    setTimeout(()=>{
                        setIsInSubTitleVieport(true);
                        setTimeout(()=>{
                            setIsInButtonViewport(true);
                        },1000);
                    },1000);
                } else {
                    setIsInTitleViewport(false);
                    setIsInSubTitleVieport(false);
                    setIsInButtonViewport(false);
                }
            });
        }
        const options = { root: null, rootMargin: "0px", threshold: 0 };
        const observer = new IntersectionObserver(callback, options);
        observer.observe(ref.current); // 요소 관찰 시작

        return () => {
            observer.disconnect(); // 컴포넌트 언마운트 시 관찰 중단
        }
    },[])

    const handleStartButtonClick = () => {
        navigate('/lecture');
    }

    return (
        <MainPageFirstItemContainer>
            <MainPageTitleTypo 
                isDark={isDark} 
                className={isInTitleViewport?'frame-in':'frame-out'}
                ref={ref}>Teaching Someone,<br/>Grow Your Skills</MainPageTitleTypo>
            <MainPageSubTitleTypo 
                isDark={isDark} 
                className={
                    isInSubTitleVieport?'frame-in' : 'frame-out'}>
                직접 코딩 강의 영상을 만들어 올려보세요!<br/>
                누군가를 가르칠 때 많이 성장합니다.
            </MainPageSubTitleTypo>
            <MainPageStartButton 
                isDark={isDark} 
                className={isInButtonVieport?'frame-in' : 'frame-out'}
                onClick={handleStartButtonClick}>시작하기</MainPageStartButton>
        </MainPageFirstItemContainer>
    );
};

export default MainPageFirstItem;