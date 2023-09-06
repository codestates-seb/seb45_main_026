import React from "react";
import styled from "styled-components";
import tokens from "../../styles/tokens.json";
import { useDispatch } from "react-redux";
import { setSort, setIsPurchased, setCategory, setIsFree } from "../../redux/createSlice/FilterSlice";

const globalTokens = tokens.global;

const DropdownContainer = styled.ul`
    width: 100px;
    display: flex;
    flex-direction: column;
    border-radius: ${globalTokens.RegularRadius.value}px;
    gap: 1px;
    z-index: 1;
`
const DropdownItem = styled.li`
    width: 100px;
    height: 40px;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: white;
    border-radius: ${globalTokens.RegularRadius.value}px;
    border: 1px solid lightgray;
    &:hover{
        background-color: lightgray;
    }
`

export default function FilterDropdown({ options, actionName }) {
    const dispatch = useDispatch()
    const actionMap = {
        setSort,
        setIsPurchased,
        setCategory,
        setIsFree
    };
    return (
        <DropdownContainer>
            {options.map((el,idx)=><DropdownItem key={idx} onClick={()=>dispatch(actionMap[actionName](el))}>{el.text}</DropdownItem>)}
        </DropdownContainer>
  );
}
