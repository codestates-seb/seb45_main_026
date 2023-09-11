import React from 'react';
import Flicking, { MoveEvent, WillChangeEvent } from "@egjs/react-flicking";
import { styled } from 'styled-components';
import carousel1 from '../../assets/images/carousels/carousel1.png';
import carousel2 from '../../assets/images/carousels/carousel2.png';
import carousel3 from '../../assets/images/carousels/carousel3.png';

const CarouselContainer = styled.div`
    height: 600px;
`
const CarouselItemContainer = styled.div`
`
const CarouselImg = styled.img`
    width: 100%;
    height: 100%;
    object-fit: cover;
`

const Carousel = () => {
    return (
        <CarouselContainer>
            <Flicking
                viewportTag='div'
                cameraTag='div'
                cameraClass=""
                renderOnSameKey={false}
                align="center"
                defaultIndex={0}
                onMove={(e) => {}}
                onWillChange={(e) => {}}
                horizontal={true}
                circular= {true}>
                <CarouselItemContainer>
                    <CarouselImg src={carousel1}/>
                </CarouselItemContainer>
                <CarouselItemContainer>
                    <CarouselImg src={carousel2}/>
                </CarouselItemContainer>
                <CarouselItemContainer>
                    <CarouselImg src={carousel3}/>
                </CarouselItemContainer>
            </Flicking>
        </CarouselContainer>
    );
};

export default Carousel;