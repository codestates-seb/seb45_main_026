import React, { useState,useEffect } from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { BodyTextTypo } from "../../atoms/typographys/Typographys";
import SearchChannel from "./SearchChannel";
import SearchLecture from "./SearchLecture";
import axios from "axios";

const globalTokens = tokens.global;

const DropdownBody = styled.div`
    width: 95%;
    min-height: 200px;
    position: absolute;
    top: 110%;
    left: 2.5%;
    border:  1px solid black;
    background-color: ${props=>props.isDark?globalTokens.Black.value:globalTokens.White.value};
    border: 1px solid ${props=>props.isDark?globalTokens.Gray.value:globalTokens.LightGray.value};
    border-radius: ${globalTokens.RegularRadius.value}px;
    z-index: 2;
    display: flex;
    flex-direction: column;
    padding: ${globalTokens.Spacing20.value}px;
    gap: ${globalTokens.Spacing12.value}px;
`
const ChannelContainer = styled.ul`
    width: 100%;
    min-height: 120px;
    display: flex;
    flex-direction: row;
    gap: ${globalTokens.Spacing16.value}px;
    flex-wrap: wrap;
`
const LectureContainer = styled.ul`
    width: 100%;
    min-height: 200px;
    gap: ${globalTokens.Spacing16.value}px;
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
`
const ChannelBlank = styled(BodyTextTypo)`
    width: 100%;
    margin-top: 60px;
    text-align: center;
`;
const LectureBlank = styled(BodyTextTypo)`
    width: 100%;
    margin-top: 100px;
    text-align: center;
`

export default function SearchDropdown({searchKeyword}) {
    const isDark = useSelector((state) => state.uiSetting.isDark);
    const [dropdownItems,setDropdownItems]=useState({'videos':[],'channels':[]})
    useEffect(() => {
        axios.get(`https://api.itprometheus.net/search?keyword=${searchKeyword}&limit=6`)
            .then(res => {
                setDropdownItems(res.data.data)
            })
            .catch(err=>console.log(err))
    },[searchKeyword])
    return (
        <DropdownBody isDark={isDark}>
            <BodyTextTypo isDark={isDark}>채널</BodyTextTypo>
            <ChannelContainer>
                {dropdownItems.channels.length===0?<ChannelBlank isDark={isDark}>일치하는 채널이 없습니다.</ChannelBlank>:dropdownItems.channels.map(el => <SearchChannel channel={el} />)}
            </ChannelContainer>
            <BodyTextTypo isDark={isDark}>강의</BodyTextTypo>
            <LectureContainer>
                {dropdownItems.videos.length===0?<LectureBlank isDark={isDark}>일치하는 강의가 없습니다.</LectureBlank>:dropdownItems.videos.map(el=><SearchLecture lecture={el}/>)}
            </LectureContainer>
        </DropdownBody>
    )
}