import React,{useEffect, useState} from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import { RegularInput } from "../../atoms/inputs/Inputs";
import { useSelector } from "react-redux";
import searchGray from "../../assets/images/icons/search/searchGray.svg"
import searchLightGray from "../../assets/images/icons/search/searchLightGray.svg"
import { useNavigate } from "react-router";
import { useParams } from "react-router";
import SearchDropdown from "../searchDropdown/SearchDropdown";
import { RegularButton } from "../../atoms/buttons/Buttons";


const globalTokens = tokens.global;

const InputContainer = styled.div`
    width: 100%;
    min-width: 600px;
    display: flex;
    flex-direction: row;
    justify-content: start;
    padding: 0 ${globalTokens.Spacing28.value}px;
    margin-bottom: ${globalTokens.Spacing8.value}px;
    position: relative;
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
    width: 25px;
    height: 25px;
    object-fit: contain;
    margin-left: ${globalTokens.Spacing8.value}px;
`
const SearchInput = styled(RegularInput)`
    border: none;
    background-color: rgba(0,0,0,0);
`
const SubmitButton = styled(RegularButton)`
    width: 80px;
    height: 50px;
    margin-left: ${globalTokens.Spacing20.value}px;
`

export default function SearchSubmit() {
    const isDark = useSelector((state) => state.uiSetting.isDark);
    const navigate = useNavigate()
    const { keyword } = useParams();
    const [isFocus,setIsFocus]=useState(false)
    const [searchKeyword, setSearchKeyword] = useState("")
    const onChangeHandler = (e) => {
        setSearchKeyword(e.target.value)
    }
    const navigateHandler = () => {
      if (searchKeyword !== "") {
        navigate(`/result/${searchKeyword}`);
      }
    }
    const enterHandler = (e) => {
        if (e.key === 'Enter') {
            navigateHandler()
        }
    }
    const focusHandler = () => {
        setIsFocus(true);
    };

    const blurHandler = () => {
         setIsFocus(false);
    };
    useEffect(() => {
        setSearchKeyword("")
    },[keyword])
    return (
      <InputContainer onFocus={focusHandler} onBlur={blurHandler}>
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
        <SubmitButton isDark={isDark} onClick={navigateHandler}>
          검색
        </SubmitButton>
        {searchKeyword !== "" && isFocus ? (
          <SearchDropdown searchKeyword={searchKeyword} />
        ) : (
          <></>
        )}
      </InputContainer>
    );
}