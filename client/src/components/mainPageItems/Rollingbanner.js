import React from 'react';
import { keyframes, styled } from 'styled-components';
import react from '../../assets/images/rollingbanner/react.png'
import java from '../../assets/images/rollingbanner/java.png'
import spring from '../../assets/images/rollingbanner/spring.png'
import springboot from '../../assets/images/rollingbanner/springboot.png'
import banilajs from '../../assets/images/rollingbanner/banilajs.png'
import typescript from '../../assets/images/rollingbanner/typescript.png'

const infiniteAnimation1 = keyframes`
    0% {
        transform: translateX(0%);
    }
    50% {
        transform: translateX(-100%);
    }
    50.1% {
        transform: translateX(100%);
    }
    100% {
        transform: translateX(0%);
    }
`
const infiniteAnimation2 = keyframes`
    0% {
        transform: translateX(0%);
    }
    100% {
        transform: translateX(-200%);
    }
`
const Wrapper = styled.div`
    width: 1300px;
    display: flex;
    flex-direction: column;
    align-items: center;
`
const SlideContainer = styled.div`
    overflow: hidden;
    width: 1300px;
`
const SlideWrapper = styled.ul`
    width: 1300px;
    display: flex;
    flex-wrap: nowrap;
`
const Slide = styled.div`
    display: flex;
    align-items: center;
    flex-wrap: nowrap;
    position: relative;
    gap: 30px;
    &::before {
        content: "";
        display: block;
        width: 100%;
        height: 1px;
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-100%);
        z-index: 1;
    }
    &.original {
        animation: 20s linear infinite normal none running
        ${infiniteAnimation1};
    }
    &.clone {
        animation: 20s linear infinite ${infiniteAnimation2};
    }
`
const SlideItem = styled.li`
    z-index: 2;
    transition: 0.3s;
`
const SlideItemImg = styled.img`
    width: 200px;
    padding: 10px;
`
export const Rollingbanner = () => { 
    const slides = [ react, java, spring, springboot, banilajs, typescript ]
    return (
        <Wrapper>
            <SlideContainer>
                <SlideWrapper>
                    <Slide className='original'>
                    {
                        slides.map((e,idx)=>
                            <SlideItem key={idx}>
                                <SlideItemImg src={e}/>
                            </SlideItem>)
                    }
                    </Slide>
                    <Slide className='clone'>
                    {
                        slides.map((e,idx)=>
                            <SlideItem key={idx}>
                                <SlideItemImg src={e}/>
                            </SlideItem>)
                    }
                    </Slide>
                </SlideWrapper>
            </SlideContainer>
        </Wrapper>
    );
};

export default Rollingbanner;