import React, { useEffect, useRef, useState } from 'react';
import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json';
import { useSelector } from 'react-redux';
import { MainPageSubTitleTypo, MainPageTitleTypo } from './MainPageItems.style';
import { frameInBottomToTopAnimation, frameInTopToBottomAnimation } from './frameAnimation';

const globalTokens = tokens.global;

const MainPageFourthItemContainer = styled.section`
    padding: ${globalTokens.Spacing32.value}px;
    border-radius: ${globalTokens.BigRadius.value}px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
`
const MainFourthPageTitleTypo=styled(MainPageTitleTypo)`
    &.fourth-frame-in {
        animation: ${frameInTopToBottomAnimation} 1.5s;
    }
    &.fourth-frame-out {
        opacity: 0;
    }
`
const MainFourthPageSubTitleTypo = styled(MainPageSubTitleTypo)`
    &.fourth-frame-in {
        animation: ${frameInBottomToTopAnimation} 1.5s;
    }
    &.fourth-frame-out {
        opacity: 0;
    }
`

export const MainFourthPageItems = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const ref = useRef();
    const [ isInTitleViewport, setIsInTitleViewport ] = useState(false); 
    const [ isInSubTitleViewport, setIsInSubTitleViewport ] = useState(false);

    useEffect(()=>{
        if(!ref.current) return;
        const callback = (entries) => {
          entries.forEach((entry)=>{
            if(entry.isIntersecting){
              setIsInTitleViewport(true);
              setTimeout(()=>{
                setIsInSubTitleViewport(true);
              },500)
            } else {
              setIsInTitleViewport(false);
              setIsInSubTitleViewport(false);
            }
          })
        }
        const options = { root: null, rootMargin: "0px", threshold: 0 };
        const observer = new IntersectionObserver(callback, options);
        observer.observe(ref.current); // 요소 관찰 시작
          
        return () => {
          observer.disconnect(); // 컴포넌트 언마운트 시 관찰 중단
        }
    },[])

    return (
        <MainPageFourthItemContainer>
            <MainFourthPageTitleTypo
                ref={ref}
                className={isInTitleViewport?'fourth-frame-in':'fourth-frame-out'}
                isDark={isDark}>
                    Solve a Problem
            </MainFourthPageTitleTypo>
            <MainFourthPageSubTitleTypo 
                isDark={isDark}
                className={isInSubTitleViewport?'fourth-frame-in':'fourth-frame-out'}>
                문제를 풀면서, 포인트도 얻어봐요!
            </MainFourthPageSubTitleTypo>
        </MainPageFourthItemContainer>
    );
};

export default MainFourthPageItems;