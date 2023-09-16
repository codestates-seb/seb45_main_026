import React,{useEffect, useState} from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import { RegularInput } from "../../atoms/inputs/Inputs";
import { useSelector } from "react-redux";
import searchGray from "../../assets/images/icons/search/searchGray.svg"
import searchLightGray from "../../assets/images/icons/search/searchLightGray.svg"
import { useNavigate } from "react-router";
import { useParams } from "react-router";

const globalTokens = tokens.global;

const InputContainer = styled.div`
    width: 100%;
    display: flex;
    flex-direction: row;
    justify-content: start;
    padding: 0 ${globalTokens.Spacing28.value}px;
`
const SearchBox = styled.div`
    width: 90%;
    height: 50px;
    display: flex;
    flex-direction: row;
    justify-content: start;
    align-items: center;
    border: ${globalTokens.ThinHeight.value}px solid ${globalTokens.LightGray.value};
    padding: ${globalTokens.Spacing4.value}px;
    border-radius: ${globalTokens.RegularRadius.value}px;
    gap: ${globalTokens.Spacing8.value}px;
`
const SearchIcon = styled.img`
    width: 40px;
    height: 40px;
`
const SearchInput = styled(RegularInput)`
    border: none;
`
const SubmitButton = styled.button`
    width: 80px;
    height: 50px;
    border: ${globalTokens.ThinHeight.value}px solid ${globalTokens.LightGray.value};
    border-radius: ${globalTokens.RegularRadius.value}px;
    margin-left: ${globalTokens.Spacing20.value}px;
    font-size: ${globalTokens.BodyText.value}px;
    color: ${(props)=>props.isDark ? globalTokens.White.value : globalTokens.Black.value};
`

export default function SearchSubmit() {
    const isDark = useSelector((state) => state.uiSetting.isDark);
    const navigate = useNavigate()
    const { keyword } = useParams();
    
    const [searchKeyword, setSearchKeyword] = useState("")
    const onChangeHandler = (e) => {
        setSearchKeyword(e.target.value)
    }
    const navigateHandler = () => {
        navigate(`/result/${searchKeyword}`)
    }
    const enterHandler = (e) => {
        if (e.key === 'Enter') {
            navigateHandler()
        }
    }
    useEffect(() => {
        setSearchKeyword("")
    },[keyword])
    return (
      <InputContainer>
        <SearchBox>
          <SearchIcon src={isDark ? searchLightGray : searchGray} />
          <SearchInput
            value={searchKeyword}
            onChange={onChangeHandler}
            width="100%"
            isDark={isDark}
            placeholder="검색..."
            onKeyDown={enterHandler}
          />
        </SearchBox>
        <SubmitButton isDark={isDark} onClick={navigateHandler}>검색</SubmitButton>
      </InputContainer>
    );
}