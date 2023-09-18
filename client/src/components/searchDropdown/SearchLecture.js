import React, { useState } from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { BodyTextTypo } from "../../atoms/typographys/Typographys";

const globalTokens = tokens.global;

const LectureBody = styled.li`
    width: 47%;
    display: flex;
    align-items: center;
    gap: ${globalTokens.Spacing12.value}px;
    &:hover{
        cursor: pointer;
    }
`
const ImgContainer = styled.div`
    width: 150px;
    height: 100px;
    min-width: 150px;
    border-radius: ${globalTokens.RegularRadius.value}px;
    overflow: hidden;
`
const LecturelImg = styled.img`
    width: 100%;
    height: 100%;
    object-fit: cover;
`
const LectureName = styled(BodyTextTypo)`
    font-weight: ${globalTokens.Bold.value};
`

export default function SearchLecture({lecture}) {
    const isDark = useSelector((state) => state.uiSetting.isDark);
    const navigate=useNavigate()
    return (
        <LectureBody onMouseDown={()=>navigate(`/videos/${lecture.videoId}`)}>
            <ImgContainer>
                <LecturelImg src={lecture.thumbnailUrl}/>
            </ImgContainer>
            <LectureName isDark={isDark}>{lecture.videoName}</LectureName>
        </LectureBody>
    )
}