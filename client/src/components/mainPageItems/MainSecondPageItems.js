import React, { useEffect, useRef, useState } from 'react';
import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json';
import { MainPageSubTitleTypo, MainPageTitleTypo } from './MainPageItems.style';
import { useSelector } from 'react-redux';
import Carousel from './Carousel';
import { frameInBottomToTopAnimation, frameInLeftToRightAnimation, frameInRightToLeftAnimation } from './frameAnimation';

const globalTokens = tokens.global;

const MainPageSecontItemContainer = styled.section`
    padding: ${globalTokens.Spacing32.value}px;
    border-radius: ${globalTokens.BigRadius.value}px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
`
const SecondPageTitleTypo = styled(MainPageTitleTypo)`
  &.second-frame-in {
    animation: ${frameInLeftToRightAnimation} 1.5s;
  }
  &.second-frame-out {
    opacity: 0;
  }
`
const SecondPageSubTitleTypo = styled(MainPageSubTitleTypo)`
  &.second-frame-in {
    animation: ${frameInRightToLeftAnimation} 1.5s;
  }
  &.second-frame-out {
    opacity: 0;
  }
`

const SecondPageCarouselContainer = styled.div`
  max-width: 1000px;
  max-height: 65vh;
  &.second-frame-in {
    animation: ${frameInBottomToTopAnimation} 1.5s;
  }
  &.second-frame-out {
    opacity: 0;
  }
`

export const MainSecondPageItems = () => {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const ref = useRef();
    const [ isInTitleViewport, setIsInTitleViewport ] = useState(false);
    const [ isInSubTitleViewport, setIsInSubTitleViewport ] = useState(false);
    const [ isInCarouselViewport, setIsInCarouselViewport ] = useState(false)
    
    useEffect(()=>{
      if(!ref.current) return;
      const callback = (entries) => {
        entries.forEach((entry)=>{
          if(entry.isIntersecting){
            setIsInTitleViewport(true);
            setTimeout(()=>{
              setIsInSubTitleViewport(true);
              setTimeout(()=>{
                setIsInCarouselViewport(true);
              },500)
            },500);
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
        <MainPageSecontItemContainer>
            <SecondPageTitleTypo 
              isDark={isDark} 
              className={isInTitleViewport?'second-frame-in':'second-frame-out'}
              ref={ref}>
                Growth & Rewards
            </SecondPageTitleTypo>
            <SecondPageSubTitleTypo 
              isDark={isDark}
              className={isInSubTitleViewport?'second-frame-in':'second-frame-out'}>
                성장은 기본, 영상으로 수익 창출까지!
            </SecondPageSubTitleTypo>
            <SecondPageCarouselContainer
              className={isInCarouselViewport?'second-frame-in':'second-frame-out'}>
              <Carousel/>
            </SecondPageCarouselContainer>
        </MainPageSecontItemContainer>
    );
};

export default MainSecondPageItems;