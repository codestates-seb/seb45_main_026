import React, { useEffect } from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import axios from "axios";
import { useDispatch } from "react-redux";

const globalTokens = tokens.global;

const SubmitBody = styled.div`
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px;
    gap: ${globalTokens.Spacing8.value}px;
`
const NoticeTextarea = styled.textarea`
    width: 100%;
    height: 300px;
    resize: none;
    padding: ${globalTokens.Spacing16.value}px;
    font-size: ${globalTokens.BodyText.value}px;
    border-radius: ${globalTokens.RegularRadius.value}px;
`
const SubmitButton = styled.button`
    width: 60px;
    height: 40px;
    background-color: white;
    border: 1px black solid;
    border-radius: ${globalTokens.RegularRadius.value}px;
`

export default function NoticeSubmit() {
    return (
        <SubmitBody>
            <NoticeTextarea />
            <SubmitButton>확인</SubmitButton>
        </SubmitBody>
    );
}