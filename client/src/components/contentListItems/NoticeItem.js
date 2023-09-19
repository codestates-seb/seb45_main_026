import React, { useState } from "react";
import { styled } from "styled-components";
import tokens from "../../styles/tokens.json";
import { useSelector } from "react-redux";
import { BodyTextTypo, SmallTextTypo } from "../../atoms/typographys/Typographys";
import frofileGray from "../../assets/images/icons/profile/profileGray.svg";
import NoticeSubmit from "./NoticeSubmit";
import { PositiveTextButton } from "../../atoms/buttons/Buttons";
import axios from "axios";
import { useToken } from "../../hooks/useToken";
import {ConfirmModal} from "../../atoms/modal/Modal"

const globalTokens = tokens.global;

const ItemBody = styled.div`
    width: 100%;
    min-height: 200px;
    padding: ${globalTokens.Spacing20.value}px;
    gap: ${globalTokens.Spacing28.value}px;
    border: 1px ${props=>props.isDark?globalTokens.Gray.value:globalTokens.LightGray.value} solid;
    border-radius: ${globalTokens.RegularRadius.value}px;
    display: flex;
    flex-direction: column;
`
const ProfileContainer = styled.div`
    height: 50px;
    display: flex;
    flex-direction: row;
    align-items: center;   
    gap: ${globalTokens.Spacing8.value}px;
`
const ProfileImg = styled.img`
    max-height: 50px;
    height: auto;
    width: auto;
`
const ImgContainer = styled.span`
    width: 50px;
    height: 50px;
    border-radius: ${globalTokens.CircleRadius.value}px;
    background-color: ${globalTokens.White.value};
    display: flex;
    justify-content: center;
    align-items: center;
    border: 1px solid lightgray;
    overflow: hidden;
`
const TextInfor = styled.div`
    height: 50px;
    display: flex;
    flex-direction: column;
    justify-content: center;
`
const AuthorName = styled(BodyTextTypo)`
    font-weight: ${globalTokens.Bold.value};
`
const CreatedAt = styled(SmallTextTypo)`
  color: ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
`
const NoticeContent = styled(BodyTextTypo)`
    margin-bottom: ${globalTokens.Spacing28.value}px;
`
const ButtonContainer = styled.div`
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: end;
  height: 30px;
`
const NoticeButton = styled(PositiveTextButton)`
`

export default function NoticeItem({ channelInfor, notice, accessToken, getHandler, userId }) {
  const isDark = useSelector((state) => state.uiSetting.isDark);
  const myId = useSelector((state) => state.loginInfo.myid);
  const [openEdit, setOpenEdit] = useState(false);
  const [isModalOpen,setIsModalOpen]=useState(false)
  const refreshToken = useToken();
  const date = new Date(notice.createdDate);
  date.setHours(date.getHours() + 9);
  const month = (date.getMonth() + 1).toString().padStart(2, "0");
  const day = date.getDate().toString().padStart(2, "0");

  const deleteHandler = () => {
    axios
      .delete(
        `https://api.itprometheus.net/announcements/${notice.announcementId}`,
        { headers: { Authorization: accessToken.authorization } }
      )
      .then((res) => {
        getHandler(userId)
      })
      .catch((err) => {
        if (err.response.data.message === "만료된 토큰입니다.") {
          refreshToken();
        } else {
          console.log(err);
        }
      });
  };

  return (
    <ItemBody isDark={isDark}>
      <ProfileContainer>
        <ImgContainer>
          <ProfileImg
            src={channelInfor.imageUrl ? channelInfor.imageUrl : frofileGray}
          />
        </ImgContainer>
        <TextInfor>
          <AuthorName isDark={isDark}>{channelInfor.channelName}</AuthorName>
          <CreatedAt isDark={isDark}>
            {month}월 {day}일 작성됨
          </CreatedAt>
        </TextInfor>
      </ProfileContainer>
      {openEdit ? (
        <NoticeSubmit
          announcementId={notice.announcementId}
          fixValue={notice.content}
          accessToken={accessToken}
          getHandler={getHandler}
          todo="patch"
          userId={userId}
          setOpenEdit={setOpenEdit}
        />
      ) : (
        <NoticeContent isDark={isDark}>{notice.content}</NoticeContent>
      )}
      {myId === Number(userId)?<ButtonContainer>
        <NoticeButton isDark={isDark} onClick={() => setIsModalOpen(true)}>삭제</NoticeButton>
        <NoticeButton isDark={isDark} onClick={() => setOpenEdit(!openEdit)}>수정</NoticeButton>
      </ButtonContainer>:<></>}
      <ConfirmModal isModalOpen={isModalOpen} setIsModalOpen={setIsModalOpen} isBackdropClickClose={false} content={"해당 공지사항을 삭제하시겠습니까?"} negativeButtonTitle="취소" positiveButtonTitle="삭제" handleNegativeButtonClick={()=>setIsModalOpen(false)} handlePositiveButtonClick={()=>deleteHandler()} />
    </ItemBody>
  );
}