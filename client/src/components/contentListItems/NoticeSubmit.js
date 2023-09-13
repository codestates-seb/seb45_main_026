import React, { useEffect,useState } from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import axios from "axios";
import { useDispatch } from "react-redux";
import { useToken } from "../../hooks/useToken";

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
  setNotices,
  setOpenEdit,
}) {
  const refreshToken = useToken();
  const [noticeContent, setNoticeContent] = useState(
    `${fixValue ? fixValue : ""}`
  );
  const handleNoticeContent = (event) => {
    setNoticeContent(event.target.value);
  };
  const getHandler = (userId) => {
    return axios
      .get(
        `https://api.itprometheus.net/channels/${userId}/announcements?page=1&size=10`
      )
      .then((res) => setNotices(res.data.data))
      .catch((err) => console.log(err));
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
          setNoticeContent("")
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
          setOpenEdit(false)
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
      <NoticeTextarea value={noticeContent} onChange={handleNoticeContent} />
      <SubmitButton onClick={() => submitHandler(userId)}>확인</SubmitButton>
    </SubmitBody>
  );
}