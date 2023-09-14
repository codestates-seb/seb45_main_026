import React from "react";
import styled from "styled-components";
import tokens from "../../styles/tokens.json";
import { useDispatch, useSelector } from "react-redux";
import { setSort, setIsPurchased, setCategory, setIsFree, setIsSubscribed,setWatchedDate } from "../../redux/createSlice/FilterSlice";

const globalTokens = tokens.global;

const DropdownContainer = styled.ul`
    margin-top: ${globalTokens.Spacing4.value}px;
    width: 100px;
    display: flex;
    flex-direction: column;
    border-radius: ${globalTokens.RegularRadius.value}px;
    z-index: 1;
    background-color: ${props=>props.isDark?globalTokens.Black.value:globalTokens.White.value};
    border: 1px solid ${props=>props.isDark?globalTokens.Gray.value:globalTokens.LightGray.value};
`
const DropdownItem = styled.li`
    width: 100px;
    height: 40px;
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: ${globalTokens.RegularRadius.value}px;
    color: ${props=>props.isDark?globalTokens.White.value : globalTokens.Black.value }; 
    transition: 300ms;
    cursor: pointer;
    &:hover{
        color: ${globalTokens.Gray.value};
    }
`

export default function FilterDropdown({ options, actionName }) {
    const isDark = useSelector(state=>state.uiSetting.isDark);
    const dispatch = useDispatch()
    const actionMap = {
        setSort,
        setIsPurchased,
        setCategory,
        setIsFree,
        setIsSubscribed,
        setWatchedDate
    };
    return (
        <DropdownContainer isDark={isDark}>
            {options.map((el,idx)=>
                <DropdownItem 
                    isDark={isDark}
                    key={idx} 
                    onClick={()=>dispatch(actionMap[actionName](el))}>
                        {el.text}
                </DropdownItem>)}
        </DropdownContainer>
  );
}
