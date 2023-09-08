import React,{ useState, useRef, useEffect } from "react";
import styled from "styled-components";
import tokens from "../../styles/tokens.json";
import FilterDropdown from "./FilterDropdown";
import { useSelector, useDispatch } from "react-redux";
import { setDropdown} from "../../redux/createSlice/FilterSlice";
import { RoundButton } from "../../atoms/buttons/Buttons";
const globalTokens = tokens.global;

const ButtonContainer = styled.div`
    width: 100px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: start;
`
const FilterButton = styled(RoundButton)`
    width: 100%;
    padding: ${globalTokens.Spacing8.value}px;
    background-color: rgba(255,255,255,0);
    color: ${props=>props.isDark?globalTokens.White:globalTokens.Black.value};
    &:hover {
      background-color: ${props=>props.isDark?'rgba(255,255,255,0.15)':'rgba(0,0,0,0.15)'};
    }
`

export default function CategoryButton({filter}) {
  const isDark = useSelector(state=>state.uiSetting.isDark);
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
        <FilterButton 
          isDark={isDark} 
          onClick={clickHandler} 
          name={filter.name} 
          ref={obj[`${filter.name}Ref`]}>
            {filterState.text}
        </FilterButton>
        { openDropdown===filter.name
          ? <FilterDropdown 
              options={filter.options} 
              actionName={filter.actionName} />
          : <></>}
      </ButtonContainer>
    );
}
