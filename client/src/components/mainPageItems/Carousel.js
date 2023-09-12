import React from 'react';
import { styled } from 'styled-components';
import carousel1 from '../../assets/images/carousels/carousel1.png';
import carousel2 from '../../assets/images/carousels/carousel2.png';
import carousel3 from '../../assets/images/carousels/carousel3.png';
import { AutoPlay } from "@egjs/flicking-plugins";
import Flicking, { ViewportSlot, MoveEvent, WillChangeEvent, Viewport } from "@egjs/react-flicking";
import { Arrow } from "@egjs/flicking-plugins";
import "@egjs/flicking-plugins/dist/arrow.css";

const CarouselItemContainer = styled.div`
`
const CarouselImg = styled.img`
    width: 95%;
    height: 95%;
    object-fit: contain;
`
const CarouselArrowButton = styled.span`
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    
    &.flicking-arrow-prev {
        color: red;
    }
`

const Carousel = () => {
    const plugins = [ new AutoPlay({ 
        duration: 1500, 
        animationDuration: 2000,
        direction: "NEXT", 
        stopOnHover: false }),
        new Arrow({}),
    ];

    return (
            <Flicking
                duration={2000}
                viewportTag='div'
                cameraTag='div'
                cameraClass=""
                renderOnSameKey={false}
                align="center"
                defaultIndex={0}
                onMove={(e) => {}}
                onWillChange={(e) => {}}
                horizontal={true}
                circular= {true}
                moveType='snap'
                threshold={50}
                plugins={plugins}>
                <CarouselItemContainer>
                    <CarouselImg src={carousel1}/>
                </CarouselItemContainer>
                <CarouselItemContainer>
                    <CarouselImg src={carousel2}/>
                </CarouselItemContainer>
                <CarouselItemContainer>
                    <CarouselImg src={carousel3}/>
                </CarouselItemContainer>
                <ViewportSlot>
                    <CarouselArrowButton className="flicking-arrow-prev is-thin"></CarouselArrowButton>
                    <CarouselArrowButton className="flicking-arrow-next is-thin"></CarouselArrowButton>
                </ViewportSlot>
            </Flicking>
    );
};

export default Carousel;