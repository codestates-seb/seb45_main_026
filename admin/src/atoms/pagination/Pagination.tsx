import React from 'react';
import styled from 'styled-components';
import tokens from '../../styles/tokens.json';
import { TextButton } from '../buttons/Buttons';
import arrowPrev from '../../assets/images/icons/arrowPrev.svg';
import arrowPrevDark from '../../assets/images/icons/arrowPrevDark.svg';
import arrowNext from '../../assets/images/icons/arrowNext.svg';
import arrowNextDark from '../../assets/images/icons/arrowNextDark.svg';

const globalTokens = tokens.global;

export type paginationPropsType = {
    isDark : boolean;
    maxPage : number;
    currentPage : number;
    setCurrentPage : React.Dispatch<React.SetStateAction<number>>;
}

const Pagination = ({ isDark, maxPage, currentPage, setCurrentPage } : paginationPropsType) => {
    if(currentPage<1) setCurrentPage(1);
    if(currentPage>maxPage) setCurrentPage(maxPage);

    let start = 1+5*(Math.ceil(currentPage/5)-1);
    let end = start+4>maxPage?maxPage:start+4;
    let numberArr = [];

    for (let i=start; i<=end; i++) numberArr.push(i);

    return (
        <PaginationContainer>
            <ArrowButton 
                isDark={isDark}
                onClick={()=>{
                    if(start===1) start = 1;
                    if(start>1) start = 1+5*(Math.ceil(currentPage/5)-2);
                    end = start+4>maxPage?maxPage:start+4;
                    setCurrentPage(start);
                 }}>
                    <PaginationArrowImg src={isDark?arrowPrevDark:arrowPrev}/>
            </ArrowButton>
            { numberArr.map((e)=>
                <PaginationNumber 
                    isDark={isDark} 
                    isCurrentNumber={currentPage===e}
                    onClick={()=>{ setCurrentPage(e) }}>{e}</PaginationNumber>) }
            <ArrowButton 
                isDark={isDark}
                onClick={()=>{
                    if(end<maxPage) {
                        start = 1+5*(Math.ceil(currentPage/5));
                        end = start+4>maxPage?maxPage:start+4;
                        setCurrentPage(start);
                    }
                }}>
                <PaginationArrowImg src={isDark?arrowNextDark:arrowNext}/>
            </ArrowButton>
        </PaginationContainer>
    );
};

export const PaginationContainer = styled.div`
    width: 100%;
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: center;
`

export const PaginationNumber = styled(TextButton)<{isCurrentNumber : boolean}>`
    font-weight: ${props=>props.isCurrentNumber ? globalTokens.Bold.value : 400 };
    font-size : ${globalTokens.BodyText.value}px;
    color: ${props=>props.isDark? globalTokens.White.value : globalTokens.Black.value};
`

export const ArrowButton = styled(TextButton)`
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
`

export const PaginationArrowImg = styled.img`
    width: ${globalTokens.BodyText.value}px;
`

export default Pagination;