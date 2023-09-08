import React,{useState,useRef,useEffect} from "react";
import styled from "styled-components";
import tokens from "../../styles/tokens.json";
import FilterDropdown from "./FilterDropdown";
import { useSelector, useDispatch } from "react-redux";
import { setDropdown} from "../../redux/createSlice/FilterSlice";

const globalTokens = tokens.global;

const ButtonContainer = styled.div`
    width: 100px;
    display: flex;
    flex-direction: column;
    align-items: center;
`
const FilterButton = styled.button`
    width: 100%;
    height: 48px;
    color: ${globalTokens.White.value};
    background-color: ${globalTokens.LightRed.value};
    padding: ${globalTokens.Spacing12.value}px;
    font-size: ${globalTokens.BodyText.value}px;
    font-weight: ${globalTokens.Bold.value};
    border-radius: ${globalTokens.RegularRadius.value}px;
`


export default function CategoryButton({filter}) {
  
  const obj = {}
  obj[`${filter.name}Ref`] = useRef(null);
  const dispatch = useDispatch()
  const filterState = useSelector((state) => state.filterSlice.filter[filter.name]);
  const openDropdown = useSelector((state) => state.filterSlice.dropdown);
   useEffect(() => {
     const dropdownHandler = (e) => {
       if (obj[`${filter.name}Ref`].current !== null && !obj[`${filter.name}Ref`].current.contains(e.target)) {
         dispatch(setDropdown(e.target.name));
       }
     };

     if (openDropdown) {
       window.addEventListener("click", dropdownHandler);
     }

     return () => {
       window.removeEventListener("click", dropdownHandler);
     };
   }, [openDropdown]);
  const clickHandler = () => {
    if (openDropdown === filter.name) {
      dispatch(setDropdown(false));
    } else {
      dispatch(setDropdown(filter.name));
    }
   }
  
    return (
      <ButtonContainer>
        <FilterButton onClick={clickHandler} name={filter.name} ref={obj[`${filter.name}Ref`]}>{filterState.value!==filter.initialValue?filterState.text:filter.initialText}</FilterButton>
        {openDropdown===filter.name ? <FilterDropdown options={filter.options} actionName={filter.actionName} /> : <></>}
      </ButtonContainer>
    );
}
