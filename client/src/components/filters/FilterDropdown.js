import React from "react";
import styled from "styled-components";
import tokens from "../../styles/tokens.json";
import { useDispatch } from "react-redux";
import { setSort, setIsPurchased, setCategory } from "../../redux/createSlice/FilterSlice";

const globalTokens = tokens.global;

const DropdownContainer = styled.ul`
    width: 100px;
    display: flex;
    flex-direction: column;
    border-radius: 8px;
    background-color: lightgray;
    gap: 1px;
`
const ListContainer = styled.li`
    width: 100px;
    height: 40px;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: white;
    &:hover{
        background-color: lightgray;
    }
`

export default function FilterDropdown({ options, actionName }) {
    const dispatch = useDispatch()
    const actionMap = {
        setSort,
        setIsPurchased,
        setCategory
    };
    return (
        <DropdownContainer>
            {options.map((el,idx)=><ListContainer key={idx} onClick={()=>dispatch(actionMap[actionName](el))}>{el.text}</ListContainer>)}
        </DropdownContainer>
  );
}
