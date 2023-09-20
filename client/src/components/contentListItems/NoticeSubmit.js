import React, { useEffect,useRef,useState } from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import axios from "axios";
import { useDispatch, useSelector } from "react-redux";
import { useToken } from "../../hooks/useToken";
import { RegularTextArea } from '../../atoms/inputs/TextAreas';
import { BigButton } from "../../atoms/buttons/Buttons";

const globalTokens = tokens.global;

const SubmitBody = styled.div`
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding-top: 20px;
    gap: ${globalTokens.Spacing8.value}px;
`
const NoticeTextarea = styled(RegularTextArea)`
    background-color: rgba(0,0,0,0);
    width: 100%;
    max-width: 1170px;
    height: auto;
    min-height: 100px;
    resize: none;
    padding: ${globalTokens.Spacing16.value}px;
    font-size: ${globalTokens.BodyText.value}px;
`
const SubmitButton = styled(BigButton)`
    width: 100px;
    &:hover{
        cursor: pointer;
    }
`

export default function NoticeSubmit({
  fixValue,
  userId,
  accessToken,
  todo,
  announcementId,
  getHandler,
  setOpenEdit,
}) {
  const isDark = useSelector(state=>state.uiSetting.isDark);
  const textareaRef = useRef(null);
  const refreshToken = useToken();
  const [noticeContent, setNoticeContent] = useState(
    `${fixValue ? fixValue : ""}`
  );
  const handleTextareaResize = () => {
    textareaRef.current.style.height = 'auto';
    textareaRef.current.style.height = textareaRef.current.scrollHeight+'px';
  }
  const handleNoticeContent = (event) => {
    setNoticeContent(event.target.value);
    handleTextareaResize();
  };
  const submitHandler = (userId) => {
    if (todo === "post" && noticeContent !== "") {
      axios
        .post(
          `https://api.itprometheus.net/channels/${userId}/announcements`,
          { content: noticeContent },
          { headers: { Authorization: accessToken.authorization } }
        )
        .then((res) => {
          getHandler(userId);
          setNoticeContent("");
        })
        .catch((err) => {
          if (err.response.data.message === "만료된 토큰입니다.") {
            refreshToken();
          } else {
            console.log(err);
          }
        });
    } else if (todo === "patch" && noticeContent !== "") {
      axios
        .patch(
          `https://api.itprometheus.net/announcements/${announcementId}`,
          { content: noticeContent },
          { headers: { Authorization: accessToken.authorization } }
        )
        .then((res) => {
          getHandler(userId);
          setOpenEdit(false);
        })
        .catch((err) => {
          if (err.response.data.message === "만료된 토큰입니다.") {
            refreshToken();
          } else {
            console.log(err);
          }
        });
    }
  };
  return (
    <SubmitBody>
      <NoticeTextarea 
        isDark={isDark}
        placeholder='공지사항을 입력해 주세요.'
        rows={1} 
        ref={textareaRef} 
        value={noticeContent} 
        onChange={handleNoticeContent} />
      <SubmitButton isDark={isDark} onClick={() => submitHandler(userId)}>확인</SubmitButton>
    </SubmitBody>
  );
}